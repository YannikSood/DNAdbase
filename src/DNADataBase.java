import java.io.IOException;

/**
 * DNADBase class is a database system for DNA sequences.
 * 
 * It will use a disk-based hash table (bucket hash) to support searched by
 * sequence ID. It also includes a memory manager to help with binary file
 * storage of sequences and sequence ID's.
 * 
 * @author adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/5/19
 */
public class DNADataBase {
    private MemManager memManager; // memory manager
    private BucketHash bHash;


    /**
     * Default constructor for DNA Database.
     * 
     * @param fileName
     *            name of binary file
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
     * @param seqID
     *            sequence's ID
     * @param len
     *            sequence's length
     * @param seq
     *            actual sequence
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public void insert(String seqID, String len, String seq)
        throws NumberFormatException,
        IOException {

        // memory manager insertion
        // insert
        // sequence

        // Check for duplicates
        if (!this.search(seqID)) {
            MemHandle idHandle = memManager.insert(seqID, seqID.length()); // insert
            MemHandle seqHandle = memManager.insert(seq, Integer.parseInt(len));
            int slot = bHash.insert(seqID, idHandle, seqHandle);
            if (slot > -1) {
                // Print positive output
            }
            else {
                // insertion declined, print rejection output
            }
        }
        else {
            //Print rejection state
        }
    }


    /**
     * Remove a sequence from memory manager and hash-table.
     * 
     * @param seqID
     *            sequence's ID
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public void remove(String seqID) throws NumberFormatException, IOException {

        // hash table remove
        int i = 0;
        int max = bHash.getMaxSize();
        while (i < max) {
            TableEntry temp = bHash.get(i); //peek table entry at i
            
            if (temp.getID() != null && temp.getSequence() != null && temp
                .getSlot() != -1) {
                // Get bits and convert to string. Compare and if found, insert
                // a tombstone
                // At I and print output.
            }
        }

    }


    /**
     * Search a sequence in memory manager and hash-table.
     * 
     * @param seqID
     *            sequence's ID
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public boolean search(String seqID)
        throws NumberFormatException,
        IOException {

        // hash table remove
        int i = 0;
        int max = bHash.getMaxSize();
        while (i < max) {
            TableEntry temp = bHash.get(i);
            if (temp.getID() != null && temp.getSequence() != null && temp
                .getSlot() != -1) {
                // Get bits from mem manager and convert to string, then compare
                // to sequence ID. if found, print output.
                return true;
            }
        }
        return false;
    }


    /**
     * Print all sequences in memory manager and hash-table.
     * 
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public void print() throws NumberFormatException, IOException {

        // hash table remove
        int i = 0;
        int max = bHash.getMaxSize();
        while (i < max) {
            TableEntry temp = bHash.get(i);
            if (temp.getID() != null && temp.getSequence() != null && temp
                .getSlot() != -1) {
                // Get ID and pass into Mem Man, then convert to string and
                // print
            }
        }

    }

}
