/**
 * Class that implements the Round Robin scheduling algorithm. It has a quantum that is the amount of cycles that
 * each process is to run for before being switched, which process' turn it is to run and how many ticks have gone by
 * in between switching.
 * It checks whether a process is finished its run, so it returns the next one in the list, otherwise, it returns the same one.
 */
public class RoundRobin extends Scheduler {

    private int quantum;
    private int turn;
    private int ticks;
    private Process currProcess;

    /**
     * First constructor for class Round Robin. It initiates quantum to 1 and gets list of candidate processes from
     * parent class.
     */
    public RoundRobin() {
        super();
        this.quantum = 1; // default quantum
        /* TODO: you _may_ need to add some code here */
        turn=0;
        ticks = 0;


    }

    /**
     * Second constructor for class Round Robin. It initiates quantum to given number and runs the first constructor.
     * @param quantum Given quantum to be set.
     */

    public RoundRobin(int quantum) {
        this();
        this.quantum = quantum;

    }

    /**
     * Method that adds given process to list of candidate processes to be run.
     * @param p Process to be added to the list.
     */
    public void addProcess(Process p) {
        /* TODO: you need to add some code here */
        processes.add(p);


    }

    /**
     * Method that returns the next process to be run. If a process has not completed its run time, meaning if it has not
     * run for quantum long, return the same process. It accounts for processes being removed.
     * @return An object of class Process that is the next process to be run.
     */
    public Process getNextProcess() {

        //if the process has not run for quantum long, return the same process. else, return the next one.
        if(ticks<quantum){
            return processes.get(turn);
        }else{
            turn++;
            //if at the end of the list, go to the beginning.
            if(turn>processes.size()-1){
                turn=0;

            }
        }
        return processes.get(turn);

    }

    /**
     * Getter for field quantum.
     * @return An integer that is the quantum.
     */
    public int getQuantum() {
        return quantum;
    }

    /**
     * Getter for field turn.
     * @return An integer that is whose turn it is to run.
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Setter for field turn.
     * @param turn Given integer value to be assigned to field turn.
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * Getter for field ticks.
     * @return An integer that is the ticks that have gone by since last switch.
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * Setter for field ticks.
     * @param ticks A given integer value to be assigned to field ticks.
     */
    public void setTicks(int ticks) {
        this.ticks = ticks;
    }
}