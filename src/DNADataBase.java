import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * DNADBase class is a database system for DNA sequences.
 * 
 * It will use a disk-based hash table (bucket hash) to support searched by
 * sequence ID. It also includes a memory manager to help with binary file
 * storage of sequences and sequence ID's.
 * 
 * @author  adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/5/19
 */
public class DNADataBase {
    private MemManager memManager; // memory manager
    private BucketHash bHash;

    /**
     * Default constructor for DNA Database.
     * 
     * @param   fileName                name of binary file
     * @throws IOException 
     */
    public DNADataBase(String fileName, int size) throws IOException {
        this.memManager = new MemManager(fileName);
        this.bHash = new BucketHash(size);
    }

    /**
     * Insert a sequence of length len and associated sequence ID
     * into memory manager and hash-table.
     * 
     * @param seqID     sequence's ID
     * @param len       sequence's length
     * @param seq       actual sequence
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public void insert(String seqID, String len, String seq)
        throws NumberFormatException, IOException {
        
        // memory manager insertion
        memManager.insert(seqID, seqID.length()); // insert ID
        memManager.insert(seq, Integer.parseInt(len)); // insert sequence
        
        // hash table insertion
        
    }
    
    /**
     * Insert a sequence of length len and associated sequence ID
     * into memory manager and hash-table.
     * 
     * @param seqID     sequence's ID
     * @param len       sequence's length
     * @param seq       actual sequence
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public void remove(String seqID) 
        throws NumberFormatException, IOException {
        
        // memory manager insertion
        
        // hash table insertion
        int i = 0;
        int max = bHash.getMaxSize();
        while (i < max) {
            TableEntry temp = bHash.get(i);
            if (temp.getID() != null && temp.getSequence() != null && temp.getSlot() != -1) {
                //Get ID and 
            }
        }
        
    }
    
}
