#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <ctype.h>

#define BUFFER_SIZE 1024

// Function to trim leading and trailing whitespace characters
void trim_whitespace(char *str) {
    // Trim leading whitespace
    while (isspace(*str)) {
        str++;
    }

    // Trim trailing whitespace
    char *end = str + strlen(str) - 1;
    while (end > str && isspace(*end)) {
        *end-- = '\0';
    }
}

// Function to handle GET request
void handle_get(int client_socket, char *filename) {
    // Trim leading and trailing whitespace from the filename
    trim_whitespace(filename);

    printf("Attempting to open file: %s\n", filename);
    // Open the file for reading
    FILE *file = fopen(filename, "r");
    if (file != NULL) {
        printf("File opened successfully\n");
        // File opened successfully, send SERVER 200 OK
        send(client_socket, "SERVER 200 OK\n\n", strlen("SERVER 200 OK\n\n"), 0);
        
        // Send the contents of the file
        char buffer[BUFFER_SIZE];
        while (fgets(buffer, BUFFER_SIZE, file) != NULL) {
            send(client_socket, buffer, strlen(buffer), 0);
        }
        
        // Send two empty lines as required
        send(client_socket, "\n\n", 2, 0);
        
        // Close the file
        fclose(file);
    } else {
        printf("File not found or cannot be opened\n");
        // File not found, send SERVER 404 Not Found
        send(client_socket, "SERVER 404 Not Found\n", strlen("SERVER 404 Not Found\n"), 0);
    }
}

// Function to handle client communication
void handle_client(int client_socket) {
    // Send HELLO message to client
    send(client_socket, "HELLO\n", strlen("HELLO\n"), 0);
    
    while (1) {
        // Receive message from client
        char buffer[BUFFER_SIZE];
        int bytes_received = recv(client_socket, buffer, BUFFER_SIZE, 0);
        if (bytes_received <= 0) {
            perror("Receive failed");
            close(client_socket);
            exit(EXIT_FAILURE);
        }
        buffer[bytes_received] = '\0'; // Null-terminate the received data
        
        // Process client message
        if (strncasecmp(buffer, "BYE", 3) == 0) {
            // Client wants to disconnect
            printf("Closing connection with client\n");
            close(client_socket);
            exit(EXIT_SUCCESS);
        } else if (strncasecmp(buffer, "GET", 3) == 0) {
            // Client requested to get a file
            char *filename = buffer + 4; // Skip "GET "
            handle_get(client_socket, filename);
        } else {
            // Unknown command
            send(client_socket, "SERVER 502 Command Error\n", strlen("SERVER 502 Command Error\n"), 0);
        }
    }
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <port>\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    // Parse port number
    int port = atoi(argv[1]);
    if (port < 1024) {
        fprintf(stderr, "Port number must be greater than 1024\n");
        exit(EXIT_FAILURE);
    }

    // Create socket
    int server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Bind socket
    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(port);

    if (bind(server_socket, (struct sockaddr *)&server_address, sizeof(server_address)) == -1) {
        perror("Socket binding failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    // Listen for connections
    if (listen(server_socket, 5) == -1) {
        perror("Socket listening failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d\n", port);

    // Accept connections and fork child processes
    while (1) {
        // Accept a client connection
        struct sockaddr_in client_address;
        socklen_t client_address_size = sizeof(client_address);
        int client_socket = accept(server_socket, (struct sockaddr *)&client_address, &client_address_size);
        if (client_socket == -1) {
            perror("Client connection failed");
            continue;
        }

        // Fork a child process to handle client communication
        pid_t pid = fork();
        if (pid == -1) {
            perror("Fork failed");
            close(client_socket);
            continue;
        } else if (pid == 0) {
            // Child process
            close(server_socket); // Close parent socket in child process
            handle_client(client_socket);
            exit(EXIT_SUCCESS);
        } else {
            // Parent process
            close(client_socket); // Close client socket in parent process
        }
    }

    // Close server socket
    close(server_socket);
    
    return 0;
}
