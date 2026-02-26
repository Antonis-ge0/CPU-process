import java.util.ArrayList;

public class ProcessControlBlock {
    
    private final int pid;
    private ProcessState state;
    // the following two ArrayLists should record when the process starts/stops
    // for statistical purposes
    private ArrayList<Integer> startTimes; // when the process starts running
    private ArrayList<Integer> stopTimes;// when the process stops running
    //for use in CPU when deciding whether it has been tried to load the process into memory
    private boolean hasBeenChecked;

    private static int pidTotal= 0;
    private int address;
    
    public ProcessControlBlock() {
        this.state = ProcessState.NEW;
        this.startTimes = new ArrayList<Integer>();
        this.stopTimes = new ArrayList<Integer>();

        hasBeenChecked = false;
        /* TODO: you need to add some code here
         * Hint: every process should get a unique PID */

        // totalPID increments with each new process(1,2,3...etc).
        this.pid = pidTotal;
        pidTotal++;
    }

    public ProcessState getState() {
        return this.state;
    }
    
    /**
     * Function responsible for changing the state of the process
     * and updating the time record when changes occur.
     * @param state The new state that the process entered.
     * @param currentClockTime The current clock time.
     */
    public void setState(ProcessState state, int currentClockTime) {
        /* TODO: you need to add some code here
         * Hint: update this.state, but also include currentClockTime
         * in startTimes/stopTimes */
         // Assign the given state to the object variable.

         for ( ProcessState pro_state : ProcessState.values()) {
            if (pro_state.equals(state)){
                this.state = state;
            }
         }
         // Include the time it started to run.
         if (state.equals(ProcessState.RUNNING)){
            startTimes.add(currentClockTime);
         }
         // Include the time it stopped to run (termination or return to READY state).
         else if (state.equals(ProcessState.TERMINATED)){
            // Always will be located in the last index of the array.
            stopTimes.add(currentClockTime);
         }
         else if (state.equals(ProcessState.READY)){
            stopTimes.add(currentClockTime);
         }
    }
    
    public int getPid() { 
        return this.pid;
    }
    
    public ArrayList<Integer> getStartTimes() {
        return startTimes;
    }
    
    public ArrayList<Integer> getStopTimes() {
        return stopTimes;
    }

    public boolean getHasBeenChecked() {
        return hasBeenChecked;
    }
    public void setHasBeenChecked(Boolean check){
        hasBeenChecked = check;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}
