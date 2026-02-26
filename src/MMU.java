import java.util.ArrayList;

public class MMU {

    private final int[] availableBlockSizes;
    private MemoryAllocationAlgorithm algorithm;
    private ArrayList<MemorySlot> currentlyUsedMemorySlots;

    /**
     * Constructor of the class.
     * @param availableBlockSizes Available Block Sizes (in kB) in the memory.
     * @param algorithm The algorithm that will be used to decide the way that the processes will be loaded into memory.
     */
    public MMU(int[] availableBlockSizes, MemoryAllocationAlgorithm algorithm) {
        this.availableBlockSizes = availableBlockSizes;
        this.algorithm = algorithm;
        this.currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
    }

    /**
     * Function that is being used to load processes into the Memory and if successful
     * return True or else False.
     * 
     * @param p The process to be loaded.
     * @return If the process was loaded successfully return True or else False.
     */
    public boolean loadProcessIntoRAM(Process p) {
        boolean fit = false;
        /* TODO: you need to add some code here
         * Hint: this should return true if the process was able to fit into memory
         * and false if not */
        int address = algorithm.fitProcess(p,currentlyUsedMemorySlots);
        p.getPCB().setAddress(address);
        
        // If the process wasn't loaded then...
        if (address == -1){
            fit = false;
        }else { // If it was loaded successfully then.
            fit = true;
        }
        
        return fit;
    }

    /**
     * Getter for currentlyUsedMemorySlots variable.
     * @return An ArrayList containing all the currently used memory slots.
     */
    public ArrayList<MemorySlot> getCurrentlyUsedMemorySlots() {
        return currentlyUsedMemorySlots;
    }

    /**
     * Getter for the availableBlockSizes variable.
     * @return An Arraylist containing all the availableBlockSize contents.
     */
    public int[] getAvailableBlockSizes() {
        return availableBlockSizes;
    }
}
