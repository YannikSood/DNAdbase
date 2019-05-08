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

        // Check for duplicates
        if (!this.insertSearch(seqID)) {

            MemHandle idHandle = memManager.insert(seqID, seqID.length()); // insert
            MemHandle seqHandle = memManager.insert(seq, Integer.parseInt(len));
            int slot = bHash.insert(seqID, idHandle, seqHandle);

            if (slot > -1) {
                System.out.println(seqID + " was inserted");
            }
            else {
                System.out.println(seqID + " HT Full");
            }
        }
        else {
            System.out.println(seqID + " is a duplicate");
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
        boolean found = false;
        
        while (i < max) {
            TableEntry temp = bHash.get(i); // peek table entry at i

            // Not null, neither values are null, not tombstone so valid entry
            if (temp != null && temp.getID() != null && temp
                .getSequence() != null && temp.getSlot() != -1) {
                // Get seqID in bytes from Mem Manager
                byte[] id = memManager.getSequence(temp.getID());

                // Convert to bytes
                String comp = new String(id);

                // Compare
                if (comp.equals(seqID)) {
                    //Remove from HT
                    bHash.insertTomb(i);
                    
                    //Remove from MM
                    memManager.release(temp.getID());
                    memManager.release(temp.getSequence());
                    
                    found = true;
                    break;
                }
            }
        }
        if (found) {
            System.out.println(seqID + " Removed");
        }
        else {
            System.out.println(seqID + " not found/removed");
        }
        

    }


    /**
     * Search a sequence in memory manager and hash-table as a helper for insert
     * 
     * @param seqID
     *            sequence's ID
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    private boolean insertSearch(String seqID)
        throws NumberFormatException,
        IOException {

        int i = 0;
        int max = bHash.getMaxSize();

        // Iterate thru the HT
        while (i < max) {
            // Get tableEntry in the current slot
            TableEntry temp = bHash.get(i);

            // Not null, neither values are null, not tombstone so valid entry
            if (temp != null && temp.getID() != null && temp
                .getSequence() != null && temp.getSlot() != -1) {

                // Get seqID in bytes from Mem Manager
                byte[] id = memManager.getSequence(temp.getID());

                // Convert to bytes
                String comp = new String(id);

                // Compare
                if (comp.equals(seqID)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Search a sequence in memory manager and hash-table as a helper for insert
     * 
     * @param seqID
     *            sequence's ID
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public void search(String seqID) throws NumberFormatException, IOException {

        boolean found = false;
        int i = 0;
        int max = bHash.getMaxSize();
        // String for seqID and Sequence
        String comp = "";
        String out = "";

        // Iterate thru the HT
        while (i < max) {
            // Get tableEntry in the current slot
            TableEntry temp = bHash.get(i);

            // Not null, neither values are null, not tombstone so valid entry
            if (temp != null && temp.getID() != null && temp
                .getSequence() != null && temp.getSlot() != -1) {

                // Get seqID in bytes from Mem Manager
                byte[] id = memManager.getSequence(temp.getID());

                // Convert to bytes
                comp = new String(id);

                // Compare
                if (comp.equals(seqID)) {
                    found = true;
                    // Get seqID in bytes from Mem Manager
                    byte[] seq = memManager.getSequence(temp.getID());

                    // Convert to bytes
                    out = new String(seq);
                }
            }
        }

        if (found) {
            System.out.println(seqID + " Found");
        }
        else {
            System.out.println(seqID + " NOt found");
        }
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

        // Iterate through HT
        while (i < max) {
            // Get value at i
            TableEntry temp = bHash.get(i);

            // Confirm it is valid
            if (temp != null && temp.getID() != null && temp
                .getSequence() != null && temp.getSlot() != -1) {
                // Get ID and pass into Mem Man, then convert to string and
                // print

                // Get seqID in bytes from Mem Manager
                byte[] id = memManager.getSequence(temp.getID());

                // Convert to bytes
                String comp = new String(id);

                // Get sequence
                byte[] seq = memManager.getSequence(temp.getID());

                // Convert to bytes
                String out = new String(seq);

                int slot = temp.getSlot();

                // Output message here.

            }
        }

    }

}
