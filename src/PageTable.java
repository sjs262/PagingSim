public class PageTable {
    final private PageTableEntry[] entries = new PageTableEntry[(int) Math.pow(2, 26)];

    public void setMapping(int virtualAddress, int physicalPageNumber) {
        int index = virtualAddress >> 6;
        entries[index] = new PageTableEntry(physicalPageNumber);
    }

    public int getMapping(int virtualAddress) {
        int pageOffset = virtualAddress % (1 << 6);
        int index = virtualAddress >>> 6;
        return (entries[index].getMapping() << 6) + pageOffset;
    }

    private class PageTableEntry {
        private boolean valid; // whether the frame is present in main memory
        private boolean dirty; // whether the frame is dirty
        private final int physicalPageNumber; // the mapping from the virtual page number

        public PageTableEntry(int physicalPageNumber) {
            valid = true;
            dirty = false;
            this.physicalPageNumber = physicalPageNumber;
        }

        public boolean isValid() {
            return valid;
        }
        public void setValid() {
            valid = true;
        }
        public void setInvalid() {
            valid = false;
        }

        public boolean isDirty() {
            return dirty;
        }
        public void setDirty() {
            dirty = true;
        }
        public void setClean() {
            dirty = false;
        }

        public int getMapping() {
            return physicalPageNumber;
        }
    }
}