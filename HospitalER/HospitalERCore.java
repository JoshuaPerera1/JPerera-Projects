// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2023T2, Assignment 3
 * Name: Joshua Perera
 * Username: pereradawa
 * ID: 300605352
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * Simple Simulation of a Hospital ER
 * 
 * The Emergency room has a waiting room and a treatment room that has a fixed
 *  set of beds for examining and treating patients.
 * 
 * When a patient arrives at the emergency room, they are immediately assessed by the
 *  triage team who determines the priority of the patient.
 *
 * They then wait in the waiting room until a bed becomes free, at which point
 * they go from the waiting room to the treatment room.
 *
 * When a patient has finished their treatment, they leave the treatment room and are discharged,
 *  at which point information about the patient is added to the statistics. 
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCore{

    // Fields for recording the patients waiting in the waiting room and being treated in the treatment room
    private Queue<Patient> waitingRoom = new ArrayDeque<Patient>();
    private static final int MAX_PATIENTS = 5;   // max number of patients currently being treated
    private Set<Patient> treatmentRoom = new HashSet<Patient>();

    // fields for the statistics
    /*# YOUR CODE HERE */
    private int totPatientsT = 0;
    private int aveTimeT = 0;
    private int timeP1 = 0;
    private int totP1 = 0;

    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    /**
     * Reset the simulation:
     *  stop any running simulation,
     *  reset the waiting and treatment rooms
     *  reset the statistics.
     */
    public void reset(boolean usePriorityQueue){
        running=false;
        UI.sleep(2*delay);  // to make sure that any running simulation has stopped

        time = 0;           // set the "tick" to zero.
        // reset the waiting room, the treatment room, and the statistics.
        /*# YOUR CODE HERE */
        // Create a waiting room data structure based on the selected option 
        if(usePriorityQueue){
            waitingRoom = new PriorityQueue<Patient>();    // using a priority queue for ordering patients
        } 
        else{
            waitingRoom = new ArrayDeque<Patient>();    // here im using an array deque for a regular queue of patients
        }
        waitingRoom.clear(); // Clear the waiting room (remove all patients)
        treatmentRoom.clear(); // clearing the treatment room
        totPatientsT = 0; // here i am resetting the total number of treated patients
        aveTimeT = 0; // resetting the average treatment time
        // clearing the graphics and text on the user interface
        UI.clearGraphics();  //clear screen
        UI.clearText(); //clear all text
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        running = true;
        while (running){         // each time step, check whether the simulation should pause.

            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all 
            //   the items on the temporary list from the set.

            /*# YOUR CODE HERE */
            time++; // increment the time
            Set<Patient> tempList = new HashSet<>(); //create a temporary list to remove discharged patients
            //this loop is iterating through patients in the treatment room
            for(Patient p:treatmentRoom){
                // Check if the patient's current treatment is finished
                if(p.currentTreatmentFinished()){
                    tempList.add(p);  // Add patient to the temporary list
                    UI.println(time+ ": Discharge: " + p.toString());   // Print discharge information
                    totPatientsT++; //increment total treated patients count
                    aveTimeT += p.getTotalWaitingTime(); // this line is adding to the total waiting time for average calculation
                }
                else{
                    p.advanceCurrentTreatmentByTick();   // Advance the patient's treatment by one tick
                }
            }

            // this loop is iterating through patients in the temporary list and remove them from the treatment room
            for(Patient i: tempList){
                treatmentRoom.remove(i); //remove the discharged patients
                if(i.getPriority() == 1){
                    totP1++;   // Increment count of priority 1 patients
                    timeP1 += i.getTotalWaitingTime();    // Add to total waiting time for priority 1 patients
                }
            }

            // this loop increments waiting time for patients in the waiting room
            for(Patient p:waitingRoom){
                p.waitForATick(); // add time to patients in waiting room
            }

            // Transfer patients from waiting room to treatment room while there's space
            while(!waitingRoom.isEmpty() && treatmentRoom.size() < MAX_PATIENTS){ 
                treatmentRoom.add(waitingRoom.poll()); // if there is space, add first element in waiting room to the treatment room
            }

            // Gets any new patient that has arrived and adds them to the waiting room
            Patient newPatient = PatientGenerator.getNextPatient(time);
            if (newPatient != null){
                UI.println(time+ ": Arrived: "+newPatient);
                waitingRoom.offer(newPatient);
            }
            redraw();  //this redraws the UI to reflect changes
            UI.sleep(delay);  //pause for the time delay
        }
        // paused, so report current statistics
        reportStatistics();
    }
    // Additional methods used by run() (You can define more of your own)

    /**
     * Report summary statistics about all the patients that have been discharged.
     */
    public void reportStatistics(){
        /*# YOUR CODE HERE */
        //Check if there are treated patients
        if(totPatientsT != 0){
            //this is calculating the average waiting time for all treated patients
            double value = aveTimeT/totPatientsT;
            //calculating the average waiting time for priority 1 patients
            double value1 = timeP1/totP1;
            //this prints the stats for all treated patients
            UI.println("Processed " + totPatientsT + " patients with an average waiting time of "+ value + " minutes");
            //this prints the stats fo priority 1 patients
            UI.println("Processed " + totP1 + " Priority 1 patients with an average waiting time of "+ value1 + " minutes");
        }
    }

    // METHODS FOR THE GUI AND VISUALISATION
    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addSlider("Speed", 1, 400, (401-delay), (double val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, PatientGenerator.getArrivalInterval(),
            PatientGenerator::setArrivalInterval);
        UI.addSlider("Prob of Pri 1", 1, 100, PatientGenerator.getProbPri1(),
            PatientGenerator::setProbPri1);
        UI.addSlider("Prob of Pri 2", 1, 100, PatientGenerator.getProbPri2(),
            PatientGenerator::setProbPri2);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
    }

    /**
     * Redraws all the patients and the state of the simulation
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);

        // Draw the treatment room and the waiting room:
        double y = 80;
        UI.setFontSize(14);
        UI.drawString("ER", 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, MAX_PATIENTS*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingRoom){
            p.redraw(x, y);
            x += 10;
        }
        UI.drawLine(0,y+2,400, y+2);
    }

    /**
     * main:  Construct a new HospitalERCore object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments){
        HospitalERCore er = new HospitalERCore();
        er.setupGUI();
        er.reset(false);   // initialise with an ordinary queue.
    }        

}
