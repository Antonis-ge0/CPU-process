import java.util.ArrayList;

public abstract class Scheduler {

    protected ArrayList<Process> processes; // the list of processes to be executed
    
    public Scheduler() {
        this.processes = new ArrayList<Process>();
    }

    /* the addProcess() method should add a new process to the list of
     * processes that are candidates for execution. This will probably
     * differ for different schedulers */
    public abstract void addProcess(Process p);
    
    /* the removeProcess() method should remove a process from the list
     * of processes that are candidates for execution. Common for all
     * schedulers. */
    public void removeProcess(Process p) {
        /* TODO: you need to add some code here */
        Process pr=null;
        // Loops through the processes and if one that matches is found, it removes it.
        for (Process process : processes) {
            if(process.equals(p)){
                pr=process;
            }
        }
        processes.remove(pr);
    }
    
    /* the getNextProcess() method should return the process that should
     * be executed next by the CPU */
    public abstract Process getNextProcess();

    public ArrayList<Process> getProcesses() {
        return processes;
    }
}
