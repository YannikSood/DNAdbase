/**
 * The interface for the hashtable
 * 
 * @author yanniksood
 * @version 05.07.19
 *
 * @param <T> Type
 * @param <K> Type
 * @param <V> Type
 */
public interface BucketHashInterface <T, K, V> {

    
    /**
     * First checks if insertion is possible
     * (No Duplicates, Full Bucket)
     * Then Inserts and returns the slot it was inserted
     * 
     * @param key Key Handle
     * @param value Handle
     * @return the slot
     */
    public int insert(T string, K key, V value);
 

}
