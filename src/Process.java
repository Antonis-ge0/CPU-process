/**
 * Class that represents a process of the memory.
 */
public class Process {
    private ProcessControlBlock pcb;
    private int arrivalTime;
    private int burstTime;
    private int memoryRequirements;

    // Variable to keep track of the clock.
    private int clockTime = arrivalTime;
    
    public Process(int arrivalTime, int burstTime, int memoryRequirements) {
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.memoryRequirements = memoryRequirements;
        this.pcb = new ProcessControlBlock();
    }
    
    public ProcessControlBlock getPCB() {
        return this.pcb;
    }
   
    /**
     * Executes when it's the process turn to run.
     */
    public void run() {
        /* TODO: you need to add some code here
         * Hint: this should run every time a process starts running */
        if(! getPCB().getState().equals(ProcessState.RUNNING)){
            getPCB().setState(ProcessState.RUNNING, clockTime);
        }
        
        // Possibly needs change depending on the CPU class implementation.
        burstTime --;
        if(burstTime <= 0){
            getPCB().setState(ProcessState.TERMINATED,clockTime);
        }

    }
    
    /**
     * Executes when the process returns in READY state.
     */
    public void waitInBackground() {
        /* TODO: you need to add some code here
         * Hint: this should run every time a process stops running */
        if(! getPCB().getState().equals(ProcessState.READY)){
            getPCB().setState(ProcessState.READY, clockTime);
        }
    }

    public double getWaitingTime() {
        /* TODO: you need to add some code here
         * and change the return value */
        int sum = 0;
        // Check all stop times except for the last one which is the termination time.
        for(int i=0;i < getPCB().getStopTimes().size() - 1 ;i++){
            // Retract the time it stopped from the time it started running to get waiting time and add it to the summary.
            sum = sum + getPCB().getStartTimes().get(i) - getPCB().getStopTimes().get(i);
        }
        return sum;
    }
    
    public double getResponseTime() {
        /* TODO: you need to add some code here
         * and change the return value */
        if (getPCB().getStopTimes().isEmpty()){
            return 0;
        }
        else{
            return getPCB().getStopTimes().get(0) - this.arrivalTime;
        }
    }
    
    /**
     * Function used to get the turn around time of the process.
     * !! Must be executed after the process has been finished.
     * @return Turnaround time of process.
     */
    public double getTurnAroundTime() {
        /* TODO: you need to add some code here
         * and change the return value */
        double waitingTime = this.getWaitingTime();

        // Gets the time that the process was stopped.
        int indexOfLast  = getPCB().getStopTimes().size() - 1;
        return getPCB().getStopTimes().get(indexOfLast) + waitingTime;
    }
     //getters for RR Algorithm
    protected int getArrivalTime() { return arrivalTime; }
    protected int getBurstTime() { return burstTime; }

    public int getMemoryRequirements(){
        return this.memoryRequirements;
    }
    public int getClockTime(){
        return clockTime;
    }

    public void setClockTime(int time){
        this.clockTime = time;
    }
}
