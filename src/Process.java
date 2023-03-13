public class Process {
    private final PageTable pageTable = new PageTable();
    public Operation[] instrMemory;

    public Operation getOperation(int programCounter) {
        return instrMemory[programCounter];
    }

    public int getMapping(int virtualAddress) {
        int k = 32 - MemoryManager.OFFSET; // shift amount for rooting out the offset bits (integers are all 32 bits wide)
        int virtualPage = virtualAddress >>> MemoryManager.OFFSET;
        int offset = virtualAddress << k >>> k;
        int physicalPage = pageTable.getMapping(virtualPage);
        if (physicalPage != -1)
            return (physicalPage << MemoryManager.OFFSET) + offset;
        else {
            System.out.println("There exists no mapping for this virtual address yet");
            return 0;
        }
    }
    
    public void setMapping(int virtualPage, int physicalPage) {
        pageTable.setMapping(virtualPage, physicalPage);
    }
    
    public boolean isValid(int virtualAddress){
        return pageTable.isValid(virtualAddress);
    }
}
