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


// Function to handle PUT request
void handle_put(int client_socket, char *filename) {
    // Trim leading and trailing whitespace from the filename
    trim_whitespace(filename);

    printf("Attempting to open file for writing: %s\n", filename);
    // Open the file for writing
    FILE *file = fopen(filename, "w");
    if (file != NULL) {
        printf("File opened successfully\n");
        send(client_socket, "SERVER 201 Created\n", strlen("SERVER 201 Created\n"), 0);
        
        char buffer[BUFFER_SIZE];
        int empty_lines_count = 0;
        while (1) {
            // Receive message from client
            int bytes_received = recv(client_socket, buffer, BUFFER_SIZE, 0);
            if (bytes_received <= 0) {
                perror("Receive failed");
                close(client_socket);
                fclose(file);
                return;
            }
            buffer[bytes_received] = '\0'; // Null-terminate the received data
            
            // Write message to file
            fputs(buffer, file);
            
            // Check for empty lines
            if (strcmp(buffer, "\n") == 0) {
                empty_lines_count++;
                if (empty_lines_count >= 2) {
                    printf("Received two consecutive empty lines, closing file\n");
                    break;
                }
            } else {
                empty_lines_count = 0;
            }
        }
        
        // Close the file
        fclose(file);
    } else {
        printf("Failed to open file for writing\n");
        send(client_socket, "SERVER 501 Put Error\n", strlen("SERVER 501 Put Error\n"), 0);
    }
}


int main(int argc, char *argv[]) {
    // Check for command line argument
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <port>\n", argv[0]);
        return -1;
    }
    
    // Parse port number from command line argument
    int port = atoi(argv[1]);
    
    // Check if port number is valid
    if (port < 1024) {
        fprintf(stderr, "Port number must be greater than or equal to 1024\n");
        return -1;
    }
    
    // Create socket
    int server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == -1) {
        perror("Socket creation failed");
        return -1;
    }
    
    // Initialize server address structure
    struct sockaddr_in server_addr;
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(port);
    
    // Bind socket to address
    if (bind(server_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Socket binding failed");
        return -1;
    }
    
    // Listen for incoming connections
    if (listen(server_socket, 10) == -1) {
        perror("Listen failed");
        return -1;
    }
    
    printf("Starting Server.... %d\n", port);
    
    while (1) {
        // Accept incoming connection
        struct sockaddr_in client_addr;
        socklen_t client_len = sizeof(client_addr);
        int client_socket = accept(server_socket, (struct sockaddr *)&client_addr, &client_len);
        if (client_socket == -1) {
            perror("Accept failed");
            continue;
        }
        
        printf("Connection accepted from %s:%d\n", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
        
        // Send HELLO message to client
        send(client_socket, "HELLO\n", strlen("HELLO\n"), 0);
        
        // Receive message from client
        char buffer[BUFFER_SIZE];
        int bytes_received = recv(client_socket, buffer, BUFFER_SIZE, 0);
        if (bytes_received <= 0) {
            perror("Receive failed");
            close(client_socket);
            continue;
        }
        buffer[bytes_received] = '\0'; // Null-terminate the received data
        
        // Process client message
        if (strncasecmp(buffer, "BYE", 3) == 0) {
            // Client wants to disconnect
            printf("Closing connection with client\n");
            close(client_socket);
        } else if (strncasecmp(buffer, "GET", 3) == 0) {
            // Client requested to get a file
            char *filename = buffer + 4; // Skip "GET "
            handle_get(client_socket, filename);
            close(client_socket);
        } else if (strncasecmp(buffer, "PUT", 3) == 0) {
            // Client requested to put a file
            char *filename = buffer + 4; // Skip "PUT "
            handle_put(client_socket, filename);
            close(client_socket);
        } else {
            // Unknown command
            send(client_socket, "SERVER 502 Command Error\n", strlen("SERVER 502 Command Error\n"), 0);
            close(client_socket);
        }
    }
    
    // Close server socket
    close(server_socket);
    
    return 0;
}
