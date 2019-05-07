
public interface BucketHashInterface <T, K, V> {

    
    /**
     * First checks if insertion is possible
     * (No Duplicates, Full Bucket)
     * Then Inserts and returns the slot it was inserted
     * @param key Key Handle
     * @param value Handle
     * @return the slot
     */
    public int insert(T string, K key, V value);
    
    public TableEntry remove(K key);
    
    public int search(K key);
    
    public int reHash();

}
