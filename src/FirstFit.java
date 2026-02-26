import java.util.ArrayList;

/**
 * Class that implements the First fit memory allocation algorithm. It maps the memory in a two-dimensional array
 * according to the blocks given, and updates that map with each process loaded or removed.
 * Then it runs the first fit algorithm to load a process given, if it fits in memory.
 */
public class FirstFit extends MemoryAllocationAlgorithm {
    //updated map of all blocks and memory slots in those blocks.
    ArrayList<ArrayList<MemorySlot>> blockMemorySlots;
    /**
     * Constructor for class First fit.
     * @param availableBlockSizes An array containing the sizes of the blocks given.
     */
    public FirstFit(int[] availableBlockSizes) {
        super(availableBlockSizes);
        //initializing updated map of all blocks
        blockMemorySlots = new ArrayList<>();
        for(int i = 0; i<availableBlockSizes.length;i++){
            blockMemorySlots.add(new ArrayList<>());
        }
    }
    /**
     * Method that implements the First fit algorithm. For each block consecutively, it tries to fit the process either
     * at the start of the block, in between other processes or at the end of the block. If a block is empty, it tries
     * to fit the process at the start. If the process fit somewhere, it stops and returns the address where it was stored.
     * @param p An object of class Process that is the process to be loaded
     * @param currentlyUsedMemorySlots An arraylist of class MemorySlot that contains the memory slots that are occupied
     * by other processes
     * @return an integer that is the address where the process was loaded
     */
    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        boolean fit = false;
        //address to be returned.
        int address = -1;
        //starting address of each block checked
        int currAddress=0;
        /* TODO: you need to add some code here
         * Hint: this should return the memory address where the process was
         * loaded into if the process fits. In case the process doesn't fit, it
         * should return -1. 
         */
        //Mapping the block into consecutive MemorySlots and Free Spaces
        for (int i=0;i<availableBlockSizes.length && !fit ;i++){
            blockMemorySlots.set(i,new ArrayList<>());
            for (MemorySlot m: currentlyUsedMemorySlots) {
                //if a memory slot is in the current block, add it the i-th row
                if (m.getBlockStart() == currAddress) {
                    if(!blockMemorySlots.get(i).contains(m)) {
                        blockMemorySlots.get(i).add(m);
                    }
                }
            }
            //if the block already has other processes in
            if(blockMemorySlots.get(i).size()>0) {
                //Checking if the process fits at the beginning of the block
                if ((blockMemorySlots.get(i).get(0).getStart() - blockMemorySlots.get(i).get(0).getBlockStart()) >= p.getMemoryRequirements()) {
                    fit = true;
                    address = blockMemorySlots.get(i).get(0).getStart();
                    MemorySlot m = new MemorySlot(address,address+p.getMemoryRequirements(),blockMemorySlots.get(i).get(0).getBlockStart(),blockMemorySlots.get(i).get(0).getBlockEnd());
                    currentlyUsedMemorySlots.add(m);
                }
                //checking if the process fits between other processes
                if (!fit) {
                    for (int j = 1; j < blockMemorySlots.get(i).size() - 1; j++) {
                        if ((blockMemorySlots.get(i).get(j).getEnd() - blockMemorySlots.get(i).get(j + 1).getStart()) >= p.getMemoryRequirements()) {
                            fit = true;
                            address = blockMemorySlots.get(i).get(j).getEnd();
                            MemorySlot m = new MemorySlot(address,address+p.getMemoryRequirements(),blockMemorySlots.get(i).get(j).getBlockStart(),
                                    blockMemorySlots.get(i).get(j).getBlockEnd());
                            currentlyUsedMemorySlots.add(m);
                            break;
                        }
                    }
                }
                //checking if the process fits at the end
                if (!fit) {
                    if ((blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockEnd() - blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getEnd()) >=
                            p.getMemoryRequirements()) {

                        address = blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getEnd();
                        MemorySlot m = new MemorySlot(address, address + p.getMemoryRequirements(), blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockStart(),
                                blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockEnd());
                        currentlyUsedMemorySlots.add(m);
                        break;
                    }
                }
                //if the block is empty, check if the process can fit in the block and put it at the beginning of the block.
            }else{
                if(availableBlockSizes[i]>=p.getMemoryRequirements()){
                    address = currAddress;
                    fit= true;
                    MemorySlot m = new MemorySlot(address,address+p.getMemoryRequirements(),currAddress,currAddress+availableBlockSizes[i]);
                    currentlyUsedMemorySlots.add(m);
                    break;
                }
            }
            //calculating address of next block
            currAddress+= availableBlockSizes[i];
        }
        sortCurrentlyUsedSlots(currentlyUsedMemorySlots);
        System.out.println(blockMemorySlots);

        return address;
    }
}
