import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.LinkedList;
import student.TestCase;

/**
 * Testing the memory manager.
 * 
 * @author  adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/5/19
 */
public class MemManagerTest extends TestCase {
    private MemManager mem;
    private byte[] result;

    /**
     * Default setup for each test.
     * @throws IOException 
     */
    public void setUp() throws IOException {
        this.mem = new MemManager("mFile.bin");
        this.result = null;
    }
    
    /**
     * Test insertion base case (empty freelist).
     * 
     * @throws IOException
     */
    public void testInsertBase() throws IOException {
        // insert some sequences
        MemHandle hanOne = mem.insert("ACGT", 4);
        MemHandle hanTwo = mem.insert("ACGTAAAA", 8);
        MemHandle hanThree = mem.insert("AAAT", 4);
        
        // free list size check
        assertEquals(0, mem.getList().size());
        
        // handle checks
        assertEquals(0, hanOne.getPosition());
        assertEquals(4, hanOne.getLength());
        
        assertEquals(1, hanTwo.getPosition());
        assertEquals(8, hanTwo.getLength());
        
        assertEquals(3, hanThree.getPosition());
        assertEquals(4, hanThree.getLength());
        
        // memory file check
        RandomAccessFile raf = new RandomAccessFile("mFile.bin", "r");
        result = new byte[4];
        
        for (int i = 0; i < 4; i++) {
            result[i] = (byte)raf.read();
        }
        
        assertEquals("[27, 27, 0, 3]", Arrays.toString(result));
    }
    
    /**
     * Test insertion when freelist is not empty.
     */
    public void testInsertFree() {
        // add some free blocks to the list
        LinkedList<MemHandle> list = mem.getList();
        
        
        
    }
    
    
    /**
     * Test insertion when freelist has no available free slots.
     */
    

    /**
     * Test stringToByteArray method base cases.
     */
    public void testStringToByteArrayBase() {
        // testing base cases
        result = mem.stringToByteArray("A");
        assertEquals("[0]", print(result));
        
        result = mem.stringToByteArray("C");
        assertEquals("[64]", print(result));
        
        result = mem.stringToByteArray("G");
        assertEquals("[-128]", print(result));
        
        result = mem.stringToByteArray("T");
        assertEquals("[-64]", print(result));
    }
    
    /**
     * Test stringToByteArray method incomplete bytes.
     */
    public void testStringToByteArrayIncomplete() {
        // testing incomplete bytes
        result = mem.stringToByteArray("ACG");
        assertEquals("[24]", print(result));
        
        result = mem.stringToByteArray("ACGTACT");
        assertEquals("[27, 28]", print(result));
        
        result = mem.stringToByteArray("AACAATTATT");
        assertEquals("[4, 60, -16]", print(result));
    }
    
    /**
     * Testing edge cases for stringToByteArray.
     */
    public void testStringToByteArrayEdge() {
        result = mem.stringToByteArray("AAAA");
        assertEquals("[0]", print(result));
        
        result = mem.stringToByteArray("TTTT");
        assertEquals("[-1]", print(result));
        
        result = mem.stringToByteArray("AAAAT");
        assertEquals("[0, -64]", print(result));
        
        result = mem.stringToByteArray("TTTTA");
        assertEquals("[-1, 0]", print(result));
    }
    
    /**
     * Testing indistinguishable cases.
     */
    public void testStringToByteArrayInd() {
        result = mem.stringToByteArray("AA");
        assertEquals("[0]", print(result));
        
        result = mem.stringToByteArray("AAA");
        assertEquals("[0]", print(result));
        
        result = mem.stringToByteArray("AAAA");
        assertEquals("[0]", print(result));
        
        result = mem.stringToByteArray("AAAAA");
        assertEquals("[0, 0]", print(result));
    }
    
    /**
     * Private helper for printing.
     */
    private String print(byte[] temp) {
        // visual checks
        System.out.println(Arrays.toString(temp));

        for (int i = 0; i < temp.length; i++) {
            String s1 = String.format(
                "%8s", Integer.toBinaryString(
                    temp[i] & 0xFF)).replace(' ', '0');

            System.out.println(s1);
        }
        
        return Arrays.toString(temp);
    }
    
}
