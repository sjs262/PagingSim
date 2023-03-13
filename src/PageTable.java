import java.util.Arrays;

public class PageTable {
    final private int numEntries = (int) Math.pow(2, MemoryManager.VIRTUAL_SPACE - MemoryManager.OFFSET);
    
    private final boolean[] valid = new boolean[numEntries];
    private final int[] physPages = new int[numEntries];
    
    public PageTable() {
        Arrays.fill(physPages, -1);
    }

    /**
     * @param virtualPage The virtual page number to be mapped from.
     * @param physicalPage The physical frame number to be mapped to.
     */
    public void setMapping(int virtualPage, int physicalPage) {
        physPages[virtualPage] = physicalPage;
        
        System.out.println("setting mapping: " + virtualPage + "-->" + physPages[virtualPage]);
    }

    /**
     * @param virtualPage The virtual page number to be mapped from.
     * @return The physical frame number mapped from the specified virtual page number.
     */
    public int getMapping(int virtualPage) {
        System.out.println("getting mapping: " + virtualPage + "-->" + physPages[virtualPage]);
        
        return physPages[virtualPage];
    }

    /**
     * @param virtualPage The virtual page number to be returned the validity of.
     * @return Whether the physical page frame mapped from the specified virtual page number resides in main memory.
     */
    public boolean isValid(int virtualPage) {
        return valid[virtualPage];
    }

    /**
     * @param virtualPage The virtual page number to be set the validity of.
     * @param validity Whether the physical page frame mapped from the specified virtual page number resides in main memory.
     */
    public void setValid(int virtualPage, boolean validity) {
        valid[virtualPage] = validity;
    }
}