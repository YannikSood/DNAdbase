/**
 * The Table Entry class. Holds 2 mem handles
 * @author yanniksood
 * @version 05.07.19
 */
public class TableEntry {

    private MemHandle iD;
    private MemHandle sequence;
    private int slot;
    
    /**
     * A Table Entry
     * @param k the id 
     * @param v the sequence
     */
    public TableEntry(MemHandle k, MemHandle v) {
        iD = k;
        sequence = v;
        slot = 0;
    }
    
    /**
     * Get the id handle
     * @return id handle
     */
    public MemHandle getID() {
        return iD;
    }
    
    /**
     * Get the sequence handle
     * @return seq handle
     */
    public MemHandle getSequence() {
        return sequence;
    }
    /**
     * Get the slot
     * @return slot
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * Set the slot
     * @param i the slot to set
     */
    public void setSlot(int i) {
        slot = i;
    }
}
