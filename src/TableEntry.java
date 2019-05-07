/**
 * 
 * @author yanniksood
 *
 */
public class TableEntry {

    private MemHandle iD;
    private MemHandle sequence;
    private int slot;
    
    public TableEntry(MemHandle k, MemHandle v) {
        iD = k;
        sequence = v;
        slot = 0;
    }
    
    public MemHandle getID() {
        return iD;
    }
    
    public MemHandle getSequence() {
        return sequence;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public void setSlot(int i) {
        slot = i;
    }
}
