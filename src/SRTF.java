/**
 * Class that implements the SRTF scheduling algorithm. It stores the list of candidate processes to be run and when called
 * returns the one with the shortest remaining burst time.
 */
public class SRTF extends Scheduler {
    /**
     * Constructor for class SRTF. It runs the constructor for parent class, fetching the list of candidate processes to be run.
     */
    public SRTF() {
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
     * Method that returns the next process to be run. For all processes in the list processes, it finds the process
     * with the shortest remaining burst time and returns it. If two processes have the same burst time, it returns
     * the one with the smaller Pid
     * @return An object of class Process that is the next process to be run.
     */
    public Process getNextProcess() {

        Process nextProcess = processes.get(0);
        for (Process p :processes){
            //compare burst time of all processes in the list, if
            if (p.getBurstTime()<nextProcess.getBurstTime()){
                nextProcess = p;
            }else if (p.getBurstTime()== nextProcess.getBurstTime()){
                {
                    if (p.getPCB().getPid()<nextProcess.getPCB().getPid()){
                        nextProcess = p;
                    }
                }

            }
        }
        return nextProcess;
    }
}
