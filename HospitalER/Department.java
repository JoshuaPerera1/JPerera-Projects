// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T2, Assignment 3
 * Name: Joshua Perera
 * Username: pereradawa
 * ID:  300605352
 */

import ecs100.*;
import java.util.*;

/**
 * A treatment Department (ER, X-Ray, MRI, ER, UltraSound, Surgery)
 * Each department will need
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *    (ordinary queue, or priority queue, depending on argument to constructor)
 */

public class Department{

    private String name;
    private int maxPatients;   // maximum number of patients receiving treatment at one time. 

    private Set<Patient> treatmentRoom;    // the patients receiving treatment
    private Queue<Patient> waitingRoom;    // the patients waiting for treatment

    /**
     * Construct a new Department object
     * Initialise the waiting queue and the current Set.
     */
    public Department(String name, int maxPatients, boolean usePriQueue){
        /*# YOUR CODE HERE */
        this.name = name;  // this sets the name of department
        this.maxPatients = maxPatients; //sets the max number of patients that facility can handle
        this.treatmentRoom = new HashSet<Patient>();  // Creates a hashSet to represent the treatment room for patients
        if(usePriQueue){ // this loop checks if a priority queue should be used for the waiting room
            this.waitingRoom = new PriorityQueue<Patient>(); //creates a priority queue for the waiting room
        }
        else{
            this.waitingRoom = new ArrayDeque<Patient>(); //creates an arraydeque for the waiting room if not using priority queue.
        }
    }

    // Methods 

    /*# YOUR CODE HERE */
    public void removePatientFromTreatmentRoom(Patient p){ // Remove a patient from the treatment room
        treatmentRoom.remove(p);
    }
    // Add a patient to the waiting room
    public void addPatientToWaitingRoom(Patient p){
        waitingRoom.add(p);
    }

    // this method performs the treatment method for patients in the treatment room
    public Set treatmentMethod(){
        Set<Patient> patients = new HashSet<>(); // create a set of patients who leave
        for(Patient p: treatmentRoom){
            p.advanceCurrentTreatmentByTick(); // tick once for patients in the treatment room
            if(p.currentTreatmentFinished()){
                patients.add(p);  // add patients whose current treatment is finished to the set
            }
        }

        for(Patient i:patients){ // remove patients and their current treatment
            treatmentRoom.remove(i);
            i.removeCurrentTreatment();
        }

        return patients; // returns the set of patients who have finished their treatment
    }

    //this method simulate patients waiting in the waiting room
    public void patientsInWaitingRoom(){
        for(Patient p:waitingRoom){
            p.waitForATick();   // simulate waiting time for patients in the waiting room
        }

        while(!waitingRoom.isEmpty() && treatmentRoom.size() < maxPatients){
            treatmentRoom.add(waitingRoom.poll()); // if there is space, add first element in waiting room to the treatment room
        }
    }

    /**
     * Draw the department: the patients being treated and the patients waiting
     * You may need to change the names if your fields had different names
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingRoom){
            p.redraw(x, y);
            x += 10;
        }
    }

}
