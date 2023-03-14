public class MemoryManager {
    // Virtual memory address size is always 32 bits -> Virtual memory should always span 2^32 addresses
    public static int VIRTUAL_SPACE = 11;
    public static int OFFSET = 2;
    public static int PHYSICAL_SPACE = 9; // 2^28 addresses in physical memory
    
    private static final int numFrames = (int) Math.pow(2, PHYSICAL_SPACE - OFFSET);
    private static final int numAddresses = (int) Math.pow(2, PHYSICAL_SPACE);
    
    public static Process[] allocated = new Process[numFrames];
    public static final int[] MAIN_MEMORY = new int[numAddresses];
    
    public static Process currentProcess;

    /**
     * @param virtualAddress The virtual address to be read from.
     * @return The data read from the specified address.
     */
    public static int getContent(int virtualAddress) {
        int physicalAddress = currentProcess.getMapping(virtualAddress);
        if(allocated[physicalAddress >>> OFFSET] == currentProcess)
            return MAIN_MEMORY[currentProcess.getMapping(virtualAddress)];
        System.out.println("Process is trying to read from non-allocated memory");
        return -1;
    }

    /**
     * @param virtualAddress The virtual address to be written to.
     * @param content The data to write to the specified address.
     */
    public static void setContent(int virtualAddress, int content) {
        int physicalAddress = currentProcess.getMapping(virtualAddress);
        System.out.println("setting content at physical address: " + physicalAddress + " with physical page: " + (physicalAddress >>> OFFSET));
        if(allocated[physicalAddress >>> OFFSET] == currentProcess)
            MAIN_MEMORY[physicalAddress] = content;
        else System.out.println("Process is trying to write to non-allocated memory");
    }

    /**
     * @param virtualPage The page number of the virtual page to enter into the process page table.
     */
    public static void allocate(int virtualPage) {
        int physicalPage = getPhysicalPageToAllocate();
        System.out.println("allocating the physical page: " + physicalPage);
        allocated[physicalPage] = currentProcess;
        currentProcess.setMapping(virtualPage, physicalPage);
    }

    /**
     * @param virtualPage The page number of the virtual page to remove the entry from in the process page table.
     */
    public static void free(int virtualPage) {
        setContent(virtualPage, 0);
        allocated[currentProcess.getMapping(virtualPage << OFFSET) >>> OFFSET] = null;
        currentProcess.setMapping(virtualPage, -1);
    }

    /**
     * @return A random physical memory location that is free for allocation.
     */
    private static int getPhysicalPageToAllocate() {
        int physicalPage = (int) (Math.random() * numFrames);
        while (allocated[physicalPage] != null)
            physicalPage = (int) (Math.random() * numFrames);
        return physicalPage;
    }
    
    public static void setCurrentProcess(Process proc) {
        currentProcess = proc;
    }
    
    public static Process getCurrentProcess() {
        return currentProcess;
    }
}
