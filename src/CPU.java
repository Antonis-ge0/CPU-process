import java.util.ArrayList;

/**
 * Class that implements the cpu of the computer. It has objects of class Scheduler and MMU for scheduling and memory
 * management and it stores all processes that are to be executed and the current process. It also stores if it needs to
 * skip a cycle, and if it needs to call the mmu.
 * @author Spyridon Drakakis
 */
public class CPU {

    public static int clock = 0; // this should be incremented on every CPU cycle

    private Scheduler scheduler;
    private MMU mmu;
    private Process[] processes;

    //
    private int currentProcess; //process being loaded in the cpu (Pid)
    private boolean twoCycles; //indicates if two cycles are required for an operation
    private boolean loadingTime; //indicates if it is "loading time" for processes waiting

    /**
     * Constructor for class CPU
     * @param scheduler An object of class Scheduler, used for scheduling
     * @param mmu An object of class MMU, used for memory management.
     * @param processes An array of class Process, used to store information for the proceses to be run.
     */
    public CPU(Scheduler scheduler, MMU mmu, Process[] processes) {
        this.scheduler = scheduler;
        this.mmu = mmu;
        this.processes = processes;
        currentProcess=-1;
    }

    /**
     * Method that ticks the cpu until there is nothing else to do, either all processes are terminated, or they did could
     * not be run due to memory shortages.
     */
    public void run() {
        /* TODO: you need to add some code here
         * Hint: you need to run tick() in a loop, until there is nothing else to do... */
        boolean done;
        while (true){
            done = checkIfDone();

            if (done){
                break;
            }

            tick();
        }

    }

    /**
     * Method that implements every clock cycle and their operations, namely loading processes into memory, running the scheduler and running the
     * process itself. If an operation requires two cycles, it skips the second tick.
     */
    public void tick() {

        //if conducting a change of state that requires two cycles, skip the second one.
        if(twoCycles){
            twoCycles=false;
            clock++;
            return;
        }else{



            if (checkIfNoProcesses()){
                clock++;
                return;
            }

            //if it is the first process of the program, load every process that have arrived and will arrive
            // before the first process is loaded and get
            //the appropriate process to be run.

            if (currentProcess==-1){
                if(loadProcesses()){
                    if(checkIfNoProcesses()){
                        clock++;
                        return;
                    }
                    Process p = scheduler.getNextProcess();
                    currentProcess = p.getPCB().getPid();
                    p.getPCB().setState(ProcessState.RUNNING,clock);
                    twoCycles = true;
                }

            }else {

                if (scheduler instanceof SRTF) {
                    //if process is finished, load waiting processes and run scheduler.
                    if (getCurrentProcess().getPCB().getState()==ProcessState.TERMINATED){
                        //if it is not loading time, delete process from memory
                        if(!loadingTime) {
                            deleteProcessFromMemory();
                        }else {
                            //when done loading processes, run scheduler.
                            if (loadProcesses()) {
                                loadingTime = false;
                                Process p = scheduler.getNextProcess();
                                currentProcess = p.getPCB().getPid();
                                //two cycles after READY-->RUNNING
                                twoCycles = true;
                            }
                        }
                    }


                    //if a process arrives that has priority, load it into memory
                    for (Process p : processes) {

                        if (p.getPCB().getState() == ProcessState.NEW && p.getArrivalTime() <= clock && !p.getPCB().getHasBeenChecked()) {
                            if (p.getBurstTime() < getCurrentProcess().getBurstTime()) {
                                //if the process fits into memory
                                if (mmu.loadProcessIntoRAM(p)) {
                                    scheduler.addProcess(p);
                                    p.getPCB().setState(ProcessState.READY, clock);
                                    //when an interrupt happens, it is always loading time.
                                    loadingTime = true;
                                    clock++;
                                    return;
                                }else{
                                    p.getPCB().setHasBeenChecked(true);
                                }
                            }
                        }
                    }

                    //if it is loading time, load waiting processes
                    if (loadingTime){
                        if(loadProcesses()){
                            loadingTime=false;
                            Process p = scheduler.getNextProcess();
                            //switch

                            if (p!=null&&!p.equals(getCurrentProcess())){
                                if(getCurrentProcess().getPCB().getState()!=ProcessState.TERMINATED) {
                                    getCurrentProcess().setClockTime(clock);
                                    getCurrentProcess().waitInBackground();
                                }
                                currentProcess=p.getPCB().getPid();
                                p.getPCB().setState(ProcessState.RUNNING,clock);
                                twoCycles = true;
                            }
                        }
                    }
                    //if it is not loading time, run scheduler without loading processes
                    else{
                        Process p = scheduler.getNextProcess();

                        Process e = getCurrentProcess();
                        //if current process is not the same as before, conduct a switch, otherwise, run it.

                        if (!p.equals(getCurrentProcess())){
                            if(getCurrentProcess().getPCB().getState()!=ProcessState.TERMINATED) {
                                getCurrentProcess().setClockTime(clock);
                                getCurrentProcess().waitInBackground();
                            }
                            currentProcess = p.getPCB().getPid();
                            p.getPCB().setState(ProcessState.RUNNING,clock);
                            twoCycles = true;
                        }
                        //if no processes are to be loaded and no switches are to be made, run the current process.
                        else{

                            getCurrentProcess().run();
                        }
                    }



                }
                else if (scheduler instanceof FCFS){
                    //if process has terminated, load waiting processes and run scheduler.
                    if (getCurrentProcess().getPCB().getState()==ProcessState.TERMINATED){
                        if(!loadingTime) {
                            deleteProcessFromMemory();
                            return;
                        }else {
                            if (loadProcesses()) {
                                //when done loading processes run scheduler and conduct a switch.
                                loadingTime = false;
                                Process p = scheduler.getNextProcess();
                                currentProcess = p.getPCB().getPid();
                                p.getPCB().setState(ProcessState.RUNNING,clock);
                                twoCycles = true;
                            }
                        }
                    }
                    //if current process has not terminated, run it.
                    else{
                        getCurrentProcess().run();

                    }

                }








                else if (scheduler instanceof RoundRobin){
                    //if a process has terminated, delete it from memory, load waiting processes and run scheduler.
                    if (getCurrentProcess().getPCB().getState()==ProcessState.TERMINATED){
                        if(!loadingTime) {
                            deleteProcessFromMemory();
                        }else {
                            if (loadProcesses()) {
                                loadingTime = false;
                                //list of candidate processes has shrunk by one, and so does turn.
                                ((RoundRobin) scheduler).setTurn(((RoundRobin) scheduler).getTurn()-1);
                                //guarantee that ticks>quantum
                                ((RoundRobin) scheduler).setTicks(((RoundRobin) scheduler).getTicks()+((RoundRobin) scheduler).getQuantum());
                                Process p = scheduler.getNextProcess();
                                //reset ticks so that the process runs for quantum
                                ((RoundRobin) scheduler).setTicks(0);

                                currentProcess = p.getPCB().getPid();
                                p.getPCB().setState(ProcessState.RUNNING,clock);
                                //two cycles after a switch
                                twoCycles = true;
                            }
                            return;
                        }
                    }



                    //if a process has finished its turn in the cpu
                    if(((RoundRobin) scheduler).getTicks() == ((RoundRobin) scheduler).getQuantum()){
                        //load waiting processes
                        if (loadProcesses()) {
                            loadingTime = false;
                            //run the scheduler
                            Process p = scheduler.getNextProcess();
                            //reset the ticks
                            ((RoundRobin) scheduler).setTicks(0);
                            //make the switch
                            if(getCurrentProcess().getPCB().getState()!=ProcessState.TERMINATED) {
                                getCurrentProcess().setClockTime(clock);
                                getCurrentProcess().waitInBackground();
                            }

                            currentProcess = p.getPCB().getPid();
                            p.getPCB().setState(ProcessState.RUNNING,clock);
                            twoCycles = true;
                        }
                    }
                    //if no process has terminated and the process currently loaded in the cpu has not finished its run
                    //run it.
                    else{
                        ((RoundRobin) scheduler).setTicks(((RoundRobin) scheduler).getTicks()+1);
                        getCurrentProcess().run();
                    }

                }
            }

        }
        clock++;


    }

    /**
     * Method that removes a process from memory.
     */
    private void deleteProcessFromMemory() {
        MemorySlot m =null;
        //remove process from list of candidate processes to be run
        scheduler.removeProcess(getCurrentProcess());
        //find the slot in which the process is loaded and remove it from memory
        for (MemorySlot slotToBeDeleted : mmu.getCurrentlyUsedMemorySlots()) {
            //check if address of the process is the same the start of the slot
            if (slotToBeDeleted.getStart() == getCurrentProcess().getPCB().getAddress()) {
                m=slotToBeDeleted;
            }
        }
        //remove slot from memory
        mmu.getCurrentlyUsedMemorySlots().remove(m);
        //reset hasBeenChecked because a process that could not fit before may be able to do so now.
        setHasBeenChecked();
        //after process is deleted from memory, it is always loading time.
        loadingTime=true;
    }

    /**
     * process that checks if there are no more things to do. It loops through the processes and confirms that every one
     * of them is terminated. Returns a boolean that is true when all processes are terminated and false if they are not.
     * @return A boolean that is true when all processes are terminated and false if they are not.
     */
    private boolean checkIfDone(){
        boolean done =false;
        for(Process p:processes){
            if(p.getPCB().getState()!=ProcessState.TERMINATED){
                done = false;
                break;
            }
            done = true;
        }
        return done;

    }

    /**
     * Method that loads any waiting processes. Only when a process is loaded does it tick the clock.
     * Returns a boolean that is true when all processes have been checked
     * and false if they have not.
     * @return a boolean that is true when all processes have been checked and false if they have not.
     */

    private boolean loadProcesses(){
        int count = 0;
        //checks all processes
        for (Process p : processes){
            //if they have arrived and are not loaded successfully  and it has not been checked if they fit into the memory or not
            if (p.getPCB().getState()==ProcessState.NEW && p.getArrivalTime()<=clock && !p.getPCB().getHasBeenChecked()){
                //check if they can be loaded into memory at all
                checkIfPossible(p);
                //if they cannot fit into memory at this time, set hasBeenChecked to true
                if(!mmu.loadProcessIntoRAM(p)){
                    p.getPCB().setHasBeenChecked(true);

                }else{
                    //if they fit into memory, add them to list of candidate processes to be run
                    scheduler.addProcess(p);
                    p.getPCB().setState(ProcessState.READY,clock);
                    break;
                }

            }
            count++;

        }

        return count == processes.length;
    }

    /**
     * Method that returns the current process that is loaded in the cpu. Returns an object of class Process.
     * @return An object of class Process that is the process that is loaded in the cpu.
     */
    private Process getCurrentProcess(){
        for(Process p:processes){
            if (p.getPCB().getPid()==currentProcess){
                return p;
            }
        }
        return null;
    }

    /**
     * Method that resets hasBeenChecked in every process. It is called when a process is terminated and space is freed.
     */
    private void setHasBeenChecked(){
        for (Process p:processes){
            p.getPCB().setHasBeenChecked(false);
        }
    }

    /**
     * Method that checks if a process can be loaded to memory even if all blocks are empty. If it cannot, set state to
     * terminated, as it would never be able to run
     * @param p An object of class Process that is to be checked if it fits in memory.
     */
    private void checkIfPossible(Process p){
        boolean flag = false;
        for(int i=0;i<mmu.getAvailableBlockSizes().length;i++){
            if(p.getMemoryRequirements()<=mmu.getAvailableBlockSizes()[i]){
                flag = true;
                break;
            }
        }

        if(!flag){
            p.getPCB().setState(ProcessState.TERMINATED,clock);

        }
    }

    /**
     * Method that checks if there are no processes that have arrived. If so, it compels the cpu to wait until there is
     * at least one process that has arrived. Returns a boolean that is true when there are no processes that have arrived and
     * false if there are.
     * @return A boolean that is true when there are no processes that have arrived and false if there are.
     */

    private boolean checkIfNoProcesses(){
        int count=0;
        for(Process p:processes){

            if(p.getArrivalTime()<=clock&&p.getPCB().getState()!=ProcessState.TERMINATED){
                count++;
            }
        }
        return !(count>0);
    }


}
