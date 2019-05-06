
/**
 * A generic hashtable for the bucket hash
 * 
 * @author yanniksood
 * @version 04/30/2019
 */
public class BucketHash  implements BucketHashInterface {

    private TableEntry[] hTable;
    private int maxSize;
    private int size;
    
    /**
     * Creates a hashtable with the size provided
     * @param size
     */
    public BucketHash(int s) {
        maxSize = s;
        hTable = new TableEntry[maxSize];
        size = 0;
    }
    
    
    /**
     * Assumes insertion is possible
     * (No Duplicates, Full Bucket)
     * Then Inserts and returns the slot it was inserted
     * @param key Key Handle
     * @param value Handle
     * @return the slot
     */
    public int insert(Object key, Object value) {
     // if (insertion possible) { run sFold, find slot, insert, increment size, return slot}
        //Get slot
        String k = (String) key;
        String v = (String) value;
        int i = sfold(k, size);
        
        //Check if slot is empty
        if (hTable[i] == null) {
            TableEntry temp = new TableEntry(k, v);
            hTable[i] = temp;
            size++;
            return i;
        }
        //Else insert with collision resolution
        
        return -1;
    }

    /**
     * Removes and returns the entry
     * @param key Key 
     * @return the removed
     */
    public TableEntry remove(Object key) {
        // if (no problems) { use sFold to find slot, if match remove, otherwise traverse & remove }
        String k = (String) key;
        int i = sfold(k, size);
        if (hTable[i].getKey().equals(k)) {
            TableEntry temp = hTable[i];
            hTable[i] = null;
            return temp;
        }
        else {
            for (int j = 0; j < maxSize; j++) {
                if (hTable[j].getKey().equals(k)) {
                    TableEntry temp = hTable[j];
                    hTable[j] = null;
                    return temp;
                } 
            }
        }
        return null;
    }


    @Override
    public int search(Object key) {
        String k = (String) key;
        int i = sfold(k, size);
        
        if (hTable[i].getKey().equals(k)) {
            return i;
        }
        else {
            for (int j = 0; j < maxSize; j++) {
                if (hTable[j].getKey().equals(k)) {
                    return j;
                } 
            }
        }
        return -1;
    }
    
    public int reHash() {
        return 1;
    }
    
    public int sfold(String s, int M) {
        long sum = 0, mul = 1;
        for (int i = 0; i < s.length(); i++) {
          mul = (i % 4 == 0) ? 1 : mul * 256;
          sum += s.charAt(i) * mul;
        }
        return (int)(Math.abs(sum) % M);
    }
}
