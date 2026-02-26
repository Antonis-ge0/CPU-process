import java.util.ArrayList;

/**
 * Class that implements the Worst Fit Algorithm to assign memory to processes.
 */
public class WorstFit extends MemoryAllocationAlgorithm {

    // A static Arraylist of ArrayLists that is being used to represent the memory structure that holds the processes.
    protected static ArrayList<ArrayList<MemorySlot>> blockMemorySlots;
     
    public WorstFit(int[] availableBlockSizes) {
        super(availableBlockSizes);

        // Initialise the blockMemorySlots ArrayList based on the available block sizes.
        blockMemorySlots = new ArrayList<>();
        for(int i = 0; i<availableBlockSizes.length;i++){
            blockMemorySlots.add(new ArrayList<>());
        }
    }

    /**
     * This function implements the Worst Fit Algorithm in order to fit the processes in the Scheduler.
     * Basicaly it finds the available block with maximum size and allocates the memory there and by doing
     * so it creates partitions that will be used by other processes later.
     *
     * @param p The process whose currently being processed.
     * @param currentlyUsedMemorySlots The memory blocks currently in use.
     * @return
     */
    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        boolean fit = false;
        int address = -1;
        // The block address of the currently used available block size.
        int i = -1;
        // Value that helps us find the maximum size of block available.
        int max = 0;
        int temp = 0;
        // Used to create the new memory slot that will hold the process in case it fits.
        MemorySlot m;
        // Holds a memorySlot in case it needs to be removed.
        MemorySlot toBeRemoved = null;

        /* 
         * Hint: this should return the memory address where the process was
         * loaded into if the process fits. In case the process doesn't fit, it
         * should return -1. 
         */
        for(int j=0;j< super.availableBlockSizes.length;j++){
            // Remove finished processes from the blockMemorySlots.
            for (MemorySlot memorySlot : blockMemorySlots.get(j)) {
                if(!currentlyUsedMemorySlots.contains(memorySlot)){
                    toBeRemoved = memorySlot;
                }
            }
            // If a memory slot doesnt match the currently used ones remove it from the blockMemorySlots.
            if (toBeRemoved != null){
                    blockMemorySlots.get(j).remove(toBeRemoved);
            }
        }
        // Find the maximum available block size that can fit the process.
        for(int j=0;j < super.availableBlockSizes.length;j++){
            temp = super.availableBlockSizes[j];
            // Retract the currently used space in the memory from the available block sizes to find the correct block.
            if(!blockMemorySlots.get(j).isEmpty()){
                for (MemorySlot memorySlot : blockMemorySlots.get(j)) {
                    temp -= memorySlot.getEnd() - memorySlot.getStart(); 
                }
            }
            if (temp > max){
                max = temp;
                i = j;
                temp = 0;
            }
        }
        // If max is at least the size of memory requirements of the process then allow to be loaded.
        if (max >= p.getMemoryRequirements()){
            fit = true;
        }

        if (fit){
            address = 0;
            fit = false;

            //if the block already has other processes currently loaded into it.
            if(!blockMemorySlots.get(i).isEmpty()) {
                //Checking if the process fits at the beginning of the block
                if ((blockMemorySlots.get(i).get(0).getStart() - blockMemorySlots.get(i).get(0).getBlockStart()) >= p.getMemoryRequirements()) {
                    fit = true;
                    address = blockMemorySlots.get(i).get(0).getStart();
                    m = new MemorySlot(address,address+p.getMemoryRequirements(),blockMemorySlots.get(i).get(0).getBlockStart(),blockMemorySlots.get(i).get(0).getBlockEnd());
                    currentlyUsedMemorySlots.add(m);
                    blockMemorySlots.get(i).add(m);
                }
                //checking if the process fits between other processes
                if (!fit) {
                    for (int j = 1; j < blockMemorySlots.get(i).size() ; j++) {
                        if ((blockMemorySlots.get(i).get(j).getEnd() - blockMemorySlots.get(i).get(j + 1).getStart()) >= p.getMemoryRequirements()) {
                            fit = true;
                            address = blockMemorySlots.get(i).get(j).getEnd();
                            m = new MemorySlot(address,address+p.getMemoryRequirements(),blockMemorySlots.get(i).get(j).getBlockStart(),
                                    blockMemorySlots.get(i).get(j).getBlockEnd());
                            currentlyUsedMemorySlots.add(m);
                            blockMemorySlots.get(i).add(m);
                            break;
                        }
                    }
                }
                //checking if the process fits at the end of the block.
                if (!fit) {
                    if ((blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockEnd() - blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getEnd()) >=
                            p.getMemoryRequirements()) {

                        address = blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getEnd();
                        m = new MemorySlot(address, address + p.getMemoryRequirements(), blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockStart(),
                                blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockEnd());
                        currentlyUsedMemorySlots.add(m);
                        blockMemorySlots.get(i).add(m);
                    }
                }
                //if the block is empty, check if the process can fit in the block and put it at the beginning of the block.
            }else{
                if(availableBlockSizes[i]>=p.getMemoryRequirements()){
                    // Find the  block start and end based on the available block sizes.
                    blockMemorySlots.set(i,new ArrayList<>());
                    for(int j=0; j<i;j++){
                        address += availableBlockSizes[j];
                    }
                    fit= true;
                    m = new MemorySlot(address,address+p.getMemoryRequirements(),address,address+availableBlockSizes[i]);
                    currentlyUsedMemorySlots.add(m);
                    blockMemorySlots.get(i).add(m);
                }
            }
            //calculating address of a block based on size in kB (assuming the memory is byte indexed)
            sortCurrentlyUsedSlots(currentlyUsedMemorySlots);
        }
        return address;
    }
}

