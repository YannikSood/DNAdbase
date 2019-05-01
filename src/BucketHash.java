
/**
 * A generic hashtable for the bucket hash
 * 
 * @author yanniksood
 * @version 04/30/2019
 */
public class BucketHash<K, V> {

    private Object[] hTable;
    
    /**
     * Creates a hashtable with the size provided
     * @param size
     */
    public BucketHash(int size) {
        hTable = new Object[size];
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
        return 1;
    }
    
    public int remove() {
        return 1;
    }
    
    public int search() {
        return 1;
    }
    
    public int reHash() {
        return 1;
    }
    
    public void sFold() {
        
    }
}
