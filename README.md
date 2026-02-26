# CPU simulator
## CPU processing simulation
Implements the CPU of a computer. It has a Scheduler and a Memory Management Unit(MMU) and it stores all processes that are to be executed. It also stores if it needs to skip a cycle, and if it needs to call the MMU.
It uses scheduling algorithms such as FCFS, SRTF and Round Robin and memory allocation algorithms such as Best Fit, First Fit, Next Fit and Worst Fit. 
> [!NOTE]  
> Processes, scheduling algorithm and memory allocation algorithm are all fixed in the PC class. They can easily be changed to any of the above with the necessary values for the process!
