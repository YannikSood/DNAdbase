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
     *            
     * @param size
     *            size of hash table
     *            
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
                //Do nothing
            }
            else {
                System.out.println("Bucket full.Sequence " + seqID
                    + " could not be inserted");
            }
        }
        else {
            System.out.println("SequenceID " + seqID + " exists");
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
        String comp = "";
        String out = "";

        while (i < max) {
            TableEntry temp = bHash.get(i); // peek table entry at i

            // Not null, neither values are null, not tombstone so valid entry
            if (temp != null && temp.getID() != null && temp
                .getSequence() != null && temp.getSlot() != -1) {
                // Get seqID in bytes from Mem Manager
                byte[] id = memManager.getSequence(temp.getID());
                int m = temp.getID().getLength();
                
                // Convert to bytes
                comp = this.testTemp(id, m);
                // Compare
                if (comp.equals(seqID) && comp.length() == seqID.length()) {
                    byte[] seq = memManager.getSequence(temp.getSequence());

                    // Convert to bytes
                    out = this.testTemp(seq, temp.getSequence()
                        .getLength());//
                    // Remove from HT
                    bHash.insertTomb(i);
                    

                    // Remove from MM
                    memManager.release(temp.getID());
                    memManager.release(temp.getSequence());

                    found = true;
                    break;
                }
            }
            i++;
        }
        if (found) {
            System.out.println("Sequence Removed " + comp + ":");
            System.out.println(out);
        }
        else {
            System.out.println("SequenceID " + seqID + " not found");
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
                int m = temp.getID().getLength();
                
                // Convert to bytes
                comp = this.testTemp(id, m);

                // Compare
                if (comp.equals(seqID) && comp.length() == seqID.length()) {
                    found = true;
                    // Get seqID in bytes from Mem Manager
                    byte[] seq = memManager.getSequence(temp.getSequence());

                    // Convert to bytes
                    out = this.testTemp(seq, temp.getSequence().getLength());
                }
            }
            i++;
        }

        if (found) {
            System.out.println("Sequence Found: " + out);
        }
        else {
            System.out.println("SequenceID " + seqID + " not found.");
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

        System.out.println("SequenceIDs:");
        // Iterate through HT
        if (bHash.getSize() > 0) {
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

                    int m = temp.getID().getLength();
                    
                    // Convert to bytes
                    String comp = this.testTemp(id, m);

                    int slot = temp.getSlot();
                    
                    //convert to string & print
                    System.out.println(comp + ": hash slot [" + slot + "]");

                }
                i++;
            }
            if (memManager.getListSize() == 0) {
                System.out.println("Free Block List: none");
            }
            else {
                System.out.println("Free Block List:");
            }
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
        TableEntry temp = null;
        // Iterate thru the HT
        while (i < max) {
            // Get tableEntry in the current slot
            temp = bHash.get(i);

            // Not null, neither values are null, not tombstone so valid entry
            if (temp != null && temp.getID() != null && temp
                .getSequence() != null && temp.getSlot() != -1) {

                // Get seqID in bytes from Mem Manager
                byte[] id = memManager.getSequence(temp.getID());
                
                int m = temp.getID().getLength();
                
                // Convert to bytes
                String comp = this.testTemp(id, m);

                // Compare
                if (comp.equals(seqID) && comp.length() == seqID.length()) {
                    return true;
                }
            }
            i++;
        }
        return false;
    }

    /**
     * Test temp
     */
    public String testTemp(byte[] b, int l) {
        
        StringBuilder build = new StringBuilder();
        
        for (int i = 0; i < b.length; i++) {
            build.append(String.format(
                "%8s", Integer.toBinaryString(
                    b[i] & 0xFF)).replace(' ', '0'));
        }
        
        String temp = build.toString();
        String result = "";
        StringBuilder fin = new StringBuilder();
        
        for (int i = 0; i < l * 2; i++) {
            if (i != 0 && i % 2 == 0) {
                switch (result) {
                    case "00":
                        fin.append("A");
                        break;
                    case "01":
                        fin.append("C");
                        break;
                    case "10":
                        fin.append("G");
                        break;
                    case "11":
                        fin.append("T");
                        break;
                    default:
                        // do nothing
                        break;
                }
                
                result = "";
            }
            
            result = result + temp.charAt(i);
        }
        
        // append remainder
        switch (result) {
            case "00":
                fin.append("A");
                break;
            case "01":
                fin.append("C");
                break;
            case "10":
                fin.append("G");
                break;
            case "11":
                fin.append("T");
                break;
            default:
                // do nothing
                break;
        }
        
        return fin.toString();
        
    }
 

}
