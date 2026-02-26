import java.util.ArrayList;

public abstract class MemoryAllocationAlgorithm {

    protected final int[] availableBlockSizes;
    
    /**
     * Constructor of the class.
     * @param availableBlockSizes The available block sizes as defined by the users.
     */
    public MemoryAllocationAlgorithm(int[] availableBlockSizes) {
        this.availableBlockSizes = availableBlockSizes;
    }

    /**
     * Function used to load processes inside the memory.
     * @param p The process to be loaded.
     * @param currentlyUsedMemorySlots The memorySlots that are currently being used.
     * @return The starting memory address that the proccess was loaded into. In case it failed to load return -1.
     */
    public abstract int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots);

    /**
     * A custom sorting algorithm to sort through the currentlyUsedMemorySlots array to have 
     * a better idea of how the memory is structured.
     * @param currentlyUsedMemorySlots The currently used MemorySlots.
     */
    protected void sortCurrentlyUsedSlots(ArrayList<MemorySlot> currentlyUsedMemorySlots){

        /*  Compare the memorySlots objects with each other and rank them in a ASCENDING order based on their
        starting memory address. */ 
        for(int i=0;i<currentlyUsedMemorySlots.size();i++){

            for(int j=0;j< currentlyUsedMemorySlots.size()-i-1;j++){

                if (currentlyUsedMemorySlots.get(j).getStart()>currentlyUsedMemorySlots.get(j+1).getStart()){

                    MemorySlot temp = currentlyUsedMemorySlots.get(j);
                    currentlyUsedMemorySlots.set(j, currentlyUsedMemorySlots.get(j + 1));
                    currentlyUsedMemorySlots.set(j+1,temp);

                }
            }
        }
    }
}
