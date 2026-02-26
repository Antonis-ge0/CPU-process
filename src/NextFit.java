import java.util.ArrayList;

/**
 * Class that implements the Next fit memory allocation algorithm. It maps the memory in a two-dimensional array
 * according to the blocks given, and updates that map with each process loaded or removed.
 * Then it runs the Next fit algorithm to load a process given, if it fits in memory, from the block that loaded last time
 * it was called.
 */
public class NextFit extends MemoryAllocationAlgorithm {

    ArrayList<ArrayList<MemorySlot>> blockMemorySlots;
    private MemorySlot previousMemorySlot;
    private int k;
    private int previousAddress;

    /**
     * Constructor for class First fit.
     * @param availableBlockSizes An array containing the sizes of the blocks given.
     */
    public NextFit(int[] availableBlockSizes) {
        super(availableBlockSizes);
        blockMemorySlots = new ArrayList<>();
        k=0;
        previousAddress=0;
        previousMemorySlot=null;
        for(int i = 0; i<availableBlockSizes.length;i++){
            blockMemorySlots.add(new ArrayList<>());
        }
    }

    /**
     * Method that implements the Next fit algorithm. For each block consecutively, it tries to fit the process either
     * at the start of the block, in between other processes or at the end of the block. If a block is empty, it tries
     * to fit the process at the start. If the process fit somewhere, it stops and returns the address where it was stored.
     * It always begins from the point it last ended.
     * @param p An object of class Process that is the process to be loaded
     * @param currentlyUsedMemorySlots An arraylist of class MemorySlot that contains the memory slots that are occupied
     * by other processes
     * @return an integer that is the address where the process was loaded
     */
    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        boolean flag = false;
        boolean flag2=false;
        boolean fit = false;
        boolean flag3 = false;
        int address = -1;
        int address2 = 0;
        int i=k;
        /* TODO: you need to add some code here
         * Hint: this should return the memory address where the process was
         * loaded into if the process fits. In case the process doesn't fit, it
         * should return -1. */
        while(!fit && !flag )
        {
            blockMemorySlots.set(i,new ArrayList<>());
            for (MemorySlot m : currentlyUsedMemorySlots) {
                if (m.getBlockStart() == address2) {
                    if (!blockMemorySlots.get(i).contains(m)) {
                        blockMemorySlots.get(i).add(m);
                    }
                }
            }

            if (blockMemorySlots.get(i).contains(previousMemorySlot)){
                i = blockMemorySlots.get(i).indexOf(previousMemorySlot);
            }else{
                if(!flag3){
                    i=k;
                    flag3=true;
                }
                // previousAddress = previousMemorySlot.getEnd();
            }
            //If the block already has other processes in
            if (blockMemorySlots.get(i).size() > 0) {
                if(previousAddress==blockMemorySlots.get(i).get(0).getBlockStart()) {
                    //Checks if the process fits at the beginning of the block
                    if ((blockMemorySlots.get(i).get(0).getStart() - blockMemorySlots.get(i).get(0).getBlockStart()) >= p.getMemoryRequirements()) {
                        fit = true;
                        k=i;
                        previousAddress = blockMemorySlots.get(i).get(0).getBlockStart();
                        //previousSlot=0;
                        address = blockMemorySlots.get(i).get(0).getStart();
                        MemorySlot m = new MemorySlot(address, address + p.getMemoryRequirements(), blockMemorySlots.get(i).get(0).getBlockStart(), blockMemorySlots.get(i).get(0).getBlockEnd());

                        previousMemorySlot=m;
                        currentlyUsedMemorySlots.add(m);
                    }
                }
                //Checks if the process fits between other processes
                if (!fit) {
                    for (int j = 1; j < blockMemorySlots.get(i).size() - 1; j++) {
                        if ((blockMemorySlots.get(i).get(j).getEnd() - blockMemorySlots.get(i).get(j + 1).getStart()) >= p.getMemoryRequirements()) {
                            fit = true;
                            address = blockMemorySlots.get(i).get(j).getEnd();
                            k=i;
                            MemorySlot m = new MemorySlot(address, address + p.getMemoryRequirements(), blockMemorySlots.get(i).get(j).getBlockStart(),
                                    blockMemorySlots.get(i).get(j).getBlockEnd());
                            previousAddress = address;
                            //previousSlot = j;
                            currentlyUsedMemorySlots.add(m);
                            previousMemorySlot=m;
                            break;
                        }
                    }
                }
                //Checks if the process fits at the end
                if (!fit) {
                    if ((blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getEnd() - blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockEnd()) >=
                            p.getMemoryRequirements()) {
                        address = blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getEnd();
                        MemorySlot m = new MemorySlot(address, address + p.getMemoryRequirements(), blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockStart(),
                                blockMemorySlots.get(i).get(blockMemorySlots.get(i).size() - 1).getBlockEnd());
                        previousMemorySlot=m;
                        currentlyUsedMemorySlots.add(m);
                        break;
                    }
                }
                //If the block is empty
            } else {
                //Checks if the process fits at the beginning of the block
                if (availableBlockSizes[i] >= p.getMemoryRequirements()) {
                    address = address2;
                    fit = true;
                    MemorySlot m = new MemorySlot(address, address + p.getMemoryRequirements(), address2, address2 + availableBlockSizes[i]);
                    currentlyUsedMemorySlots.add(m);
                    previousMemorySlot = m;
                    break;
                }
            }

            i = (i + 1) % availableBlockSizes.length;
            if(flag2){
                flag=true;
            }
            if(i==k){
                flag2 = true;
            }
            address2 += availableBlockSizes[i];
        }
        sortCurrentlyUsedSlots(currentlyUsedMemorySlots);
        System.out.println(blockMemorySlots);

        return address;
    }
}
