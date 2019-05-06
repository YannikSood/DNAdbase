/**
 * Memory handle defined by two 4-byte integers.
 * 
 * First byte represents position in file of sequence.
 * Second byte is the length of the string.
 * 
 * @author  adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/1/19
 */
public class MemHandle {
    private int position;
    private int length;
    
    /**
     * Default constructor for memory handle.
     * 
     * @param pos   position of sequence
     * @param len   length of sequence
     */
    public MemHandle(int pos, int len) {
        this.position = pos;
        this.length = len;
    }
    
    /**
     * Return this handle's position.
     * 
     * @return      this handle's position pointer
     */
    public int getPosition() {
        return this.position;
    }
    
    /**
     * Return this handle's length pointer.
     * 
     * @return      this handle's length pointer
     */
    public int getLength() {
        return this.length;
    }
}
