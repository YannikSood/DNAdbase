import student.TestCase;

public class BucketHashTest extends TestCase {
    private BucketHash hTable;
    private MemHandle m1;
    private MemHandle m2;
    
    /**
     * Unused Constructor
     */
    public BucketHashTest() {
        //Unused Constructor
    }
    
    /**
     * Set Up
     */
    public void setUp() {
        hTable = new BucketHash(64);
        m1 = new MemHandle(2, 4);
        m2 = new MemHandle(2, 4);
    }
    
    /**
     * Test Insert
     */
    public void testInsert() {
        assertEquals(6, hTable.insert("AAAAA", m1, m2));
        assertEquals(18, hTable.insert("AAAA", m1, m2));
        assertEquals(19, hTable.insert("AAA", m1, m2));
        assertEquals(0, hTable.insert("TTTTTTTTTTAAAAACCCA", m1, m2));
        assertEquals(62, hTable.insert("TCATATCTATCCAAAAAAAA", m1, m2));
        assertEquals(63, hTable.insert("TCATATCTATCCAAAAAAA", m1, m2));
        //assertEquals(32, hTable.insert("TCATATCTATCCAAAAAA", m1, m2));
        assertEquals(6, hTable.getSize());
    }
    
    /**
     * Test Insert with edge cases - full, duplicates, and tombstones
     */
    public void testInsertEdge() {
        //Test Full HT
        hTable = new BucketHash(5);
        assertEquals(0, hTable.getSize());
        assertEquals(1, hTable.insert("AAAAA", m1, m2));
        assertEquals(2, hTable.insert("AAAA", m1, m2));
        assertEquals(3, hTable.insert("AAA", m1, m2));
        assertEquals(4, hTable.insert("TCATATCTATCCAAAAAAAA", m1, m2));
        assertEquals(0, hTable.insert("TTTTTTTTTTAAAAACCCA", m1, m2));
        assertEquals(-1, hTable.insert("TCATATCTATCCAAAAAAA", m1, m2));
        
        //Test Duplicate when Search Done
        
        //Test Tombstone when remove done
    }
}
