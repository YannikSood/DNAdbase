/**
 * 
 * @author yanniksood
 *
 */
public class TableEntry {

    private MemHandle iD;
    private MemHandle sequence;
    
    public TableEntry(MemHandle k, MemHandle v) {
        iD = k;
        sequence = v;
    }
    
    public MemHandle getID() {
        return iD;
    }
    
    public MemHandle getSequence() {
        return sequence;
    }
}
