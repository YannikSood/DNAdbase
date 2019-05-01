
/**
 * A generic hashtable for the bucket hash
 * 
 * @author yanniksood
 * @version 04/30/2019
 */
public class BucketHash<K, V> {

    private Object[] hTable;
    private int size;
    
    /**
     * Creates a hashtable with the size provided
     * @param size
     */
    public BucketHash(int s) {
        hTable = new Object[s];
        size = 0;
    }
    
    
    /**
     * First checks if insertion is possible
     * (No Duplicates, Full Bucket)
     * Then Inserts and returns the slot it was inserted
     * @param key Key Handle
     * @param value Handle
     * @return the slot
     */
    public int insert(K key, V value) {
        // if (insertion possible) { run sFold, find slot, insert, increment size, return slot}
       
        return 1;
    }
    
    public int remove() {
        // if (no problems) { use sFold to find slot, if match remove, otherwise traverse & remove }
        return 1;
    }
    
    public int search() {
        return 1;
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
