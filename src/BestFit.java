import java.util.ArrayList;

public class BestFit extends MemoryAllocationAlgorithm {

    // A static variable of a 2D ArrayList that represents the memory blocks of the memory.
    protected static ArrayList<ArrayList<MemorySlot>> blockMemorySlots;
     
    /**
     * Constructor of the class.
     */
    public BestFit(int[] availableBlockSizes) {
        super(availableBlockSizes);

        // Initialise the blockMemorySlots ArrayList.
        blockMemorySlots = new ArrayList<>();
        for(int i = 0; i<availableBlockSizes.length;i++){
            blockMemorySlots.add(new ArrayList<>());
        }
    }

    /**
     * This function implements the Best Fit Algorithm in order to fit the processes in the RAM.
     * Basicaly it finds the available block with minimum size which can hold the process and 
     * allocates the memory there. By doing so it creates partitions that will be used by other processes later.
     *
     * @param p The process whose currently being processed.
     * @param currentlyUsedMemorySlots The memory blocks currently in use.
     * @return Returns the start address of where the process was loaded.
     */
    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {

        // Flag to see if the process can fit iniside the memory.
        boolean fit = false;
        // The memory address where we stored the process inside the memory.
        int address = -1;
        // Variable which holds the address of the block memory in which we will assign the process.
        int i = -1;
        // Variable to store the minimum availalable blovk size.
        int min = 10000;
        // Simple int variable to perform check inside loop.
        int temp = 0;
        // MemorySlot object to create a new MemorySlot where we will store the process.
        MemorySlot m;
        // Used to store a memorySlot in case it has to be removed.
        MemorySlot toBeRemoved = null;

        /* 
         * Hint: this should return the memory address where the process was
         * loaded into if the process fits. In case the process doesn't fit, it
         * should return -1. */

        for(int j=0;j< super.availableBlockSizes.length;j++){
            for (MemorySlot memorySlot : blockMemorySlots.get(j)) {
                if(!currentlyUsedMemorySlots.contains(memorySlot)){
                    toBeRemoved = memorySlot;
                }
                
            }
            if (toBeRemoved != null){
                    blockMemorySlots.get(j).remove(toBeRemoved);
            }
        }

        // Looping through the memory to find the minimum but also fitting available slot in the memory.
        for(int j=0;j < super.availableBlockSizes.length;j++){
            temp = super.availableBlockSizes[j];
            if(!blockMemorySlots.get(j).isEmpty()){
                // Reduce block size if other processes already use it.
                for (MemorySlot memorySlot : blockMemorySlots.get(j)) {
                    temp -= memorySlot.getEnd() - memorySlot.getStart(); 
                }
            }
            // If all the requierements are met then try to store process.
            if (temp <= min && temp >= p.getMemoryRequirements()){
                min = temp;
                i = j;
                temp = 0;
                fit = true;
            }
        }

        if (fit){
            fit = false;
            address = 0;
            //if the block already has other processes in
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
                    for (int j = 1; j < blockMemorySlots.get(i).size() - 1; j++) {
                        if ((blockMemorySlots.get(i).get(j).getEnd() - blockMemorySlots.get(i).get(j + 1).getStart()) >= p.getMemoryRequirements()) {
                            fit = true;
                            address = blockMemorySlots.get(i).get(j).getStart();
                            m = new MemorySlot(address,address+p.getMemoryRequirements(),blockMemorySlots.get(i).get(j).getBlockStart(),
                                    blockMemorySlots.get(i).get(j).getBlockEnd());
                            currentlyUsedMemorySlots.add(m);
                            blockMemorySlots.get(i).add(m);
                            break;
                        }
                    }
                }
                //checking if the process fits at the end
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
                    blockMemorySlots.set(i,new ArrayList<>());

                    // Mapping the memory based on the available block sizes.
                    for(int j=0; j<i;j++){
                        address += availableBlockSizes[j];
                    }
                    fit= true;
                    m = new MemorySlot(address,address+p.getMemoryRequirements(),address,address+availableBlockSizes[i]);
                    currentlyUsedMemorySlots.add(m);
                    blockMemorySlots.get(i).add(m);
                }
            }

            sortCurrentlyUsedSlots(currentlyUsedMemorySlots);
        }
        
        // Return the address that the process was loaded into.
        return address;
    }


}
