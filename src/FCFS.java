/**
 * Class that implements the FCFS scheduling algorithm. It stores the list of candidate processes to be run and when called
 * returns the process that is next in queue.
 * @author Antonis Georgosopoulos
 */
public class FCFS extends Scheduler {

    /**
     * Constructor for class FCFS. It runs the constructor for parent class, fetching the list of candidate processes to be run.
     */
    public FCFS() {
        super();
    }

    /**
     * Method that adds a given process to the list of candidate processes to be run.
     * @param p Given process to be added to the list of candidate processes to be run.
     */
    public void addProcess(Process p) {
        processes.add(p);
    }

    /**
     * Method that returns the process that needs to run. It finds the process that is next in queue
     * and returns it.
     * @return An object of class Process that is the next process to be run.
     */
    public Process getNextProcess() {
        int i = 0;
        Process minProcess = processes.get(i);
        for (i = 1; i < processes.size(); i++) {
            if (processes.get(i).getArrivalTime() > minProcess.getArrivalTime())
                minProcess = processes.get(i);
        }
        return minProcess;
    }
}