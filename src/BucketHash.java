
/**
 * A generic hashtable for the bucket hash
 * 
 * @author yanniksood
 * @version 04/30/2019
 */
public class BucketHash implements BucketHashInterface {

    private TableEntry[] hTable;
    private int maxSize;
    private int size;
    private int numBuckets;
    private int bucketSize;


    /**
     * Creates a hashtable with the size provided
     * 
     * @param size
     */
    public BucketHash(int s) {
        maxSize = s;
        hTable = new TableEntry[maxSize];
        size = 0;
        numBuckets = s / 32;
        bucketSize = 32;
    }


    /**
     * Get the size
     * 
     * @return size
     */
    public int getSize() {
        return size;
    }


    /**
     * Get the size
     * 
     * @return size
     */
    public int getMaxSize() {
        return maxSize;
    }


    /**
     * Assumes insertion is possible
     * (No Duplicates, Full Bucket)
     * Then Inserts and returns the slot it was inserted
     * 
     * @param key
     *            Key Handle
     * @param value
     *            Handle
     * @param seqID
     *            the sequence for sFold
     * @return the slot
     */
    public int insert(String seqID, MemHandle key, MemHandle value) {
        // if (insertion possible) { run sFold, find slot, insert, increment
        // size, return slot}
        // Get slot
        String k = seqID;
        int i = sfold(k, maxSize);
        // int bucket = (i % bucketSize);
      
        
        
        for (int j = 0; j < 32; j++) {
            if (hTable[i] == null || hTable[i].getSlot() == -1) {
                break;
            }
            else if ((i + 1) % 32 == 0) {
                i -= 31;
            }
            else {
                i++;
            }

        }
        if (hTable[i] == null || hTable[i].getSlot() == -1) {
            TableEntry temp = new TableEntry(key, value);
            temp.setSlot(i);
            hTable[i] = temp;
            size++;
            return i;
        }

        return -1;
    }


    /**
     * A method to return the table entry at the slot
     * Used for remove, search and print
     * 
     * @param i
     *            the slot to peek
     * @return the tableEntry at the slot
     */
    public TableEntry get(int i) {
        if (hTable[i].getSlot() != -1) {
            TableEntry temp = hTable[i];
            return temp;
        }
        return null;
    }


    /**
     * Insert tombstone
     * 
     * @param slot
     *            the slot to insert
     */
    public void insertTomb(int slot) {
        TableEntry tomb = new TableEntry(null, null);
        tomb.setSlot(-1);
        hTable[slot] = tomb;
        size--;
    }


    /**
     * Sfold
     * 
     * @param s
     *            string
     * @param M
     *            size
     * @return slot
     */
    public int sfold(String s, int M) {
        int intLength = s.length() / 4;
        long sum = 0;
        for (int j = 0; j < intLength; j++) {
            char c[] = s.substring(j * 4, (j * 4) + 4).toCharArray();
            long mult = 1;
            for (int k = 0; k < c.length; k++) {
                sum += c[k] * mult;
                mult *= 256;
            }
        }

        char c[] = s.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
        }

        sum = (sum * sum) >> 8;
        return (int)(Math.abs(sum) % M);
    }


    @Override
    public int insert(Object string, Object key, Object value) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public TableEntry remove(Object key) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int search(Object key) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public int reHash() {
        // TODO Auto-generated method stub
        return 0;
    }
}
