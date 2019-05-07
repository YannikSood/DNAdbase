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
     * 
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
        raf.close();
    }
    
    /**
     * Test insertion when freelist is not empty.
     * 
     * @throws IOException
     */
    public void testInsertFree() throws IOException {
        // add A's to visually color up the file
        mem.insert("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 40);
        
        // add some free blocks to the list
        // even though they're not really free here, we'll assume they are
        // and overwrite
        LinkedList<MemHandle> list = mem.getList();
        list.add(new MemHandle(0, 1));
        list.add(new MemHandle(5, 2));
        
        // should skip to the second block
        assertEquals(2, mem.getList().size());
        MemHandle handOne = mem.insert("TTTTT", 5); // actually uses up 2 bytes
        assertEquals(1, mem.getList().size());
        
        // should insert to the first block
        MemHandle handTwo = mem.insert("TTTA", 4);
        assertEquals(0, mem.getList().size());
        
        // we used all of second block with TTTTT_ _ _
        // used all of first block with TTTA
        // should add to end of file because no suitable block
        MemHandle handThree = mem.insert("ACGT", 4);
        
        // handle checks
        assertEquals(5, handOne.getPosition());
        assertEquals(0, handTwo.getPosition());
        assertEquals(10, handThree.getPosition());
    }
    
    /**
     * Test insertion when freelist is not empty (additional edge cases).
     * 
     * @throws IOException
     */
    public void testInsertFreeEdges() throws IOException {
        // add A's to visually color up the file
        mem.insert("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 40);
        
        // add some free blocks to the list
        // even though they're not really free here, we'll assume they are
        // and overwrite
        LinkedList<MemHandle> list = mem.getList();
        list.add(new MemHandle(0, 1));
        list.add(new MemHandle(5, 2));
        
        // use first block
        assertEquals(2, mem.getList().size());
        MemHandle handOne = mem.insert("T", 1);
        assertEquals(1, mem.getList().size());
        
        // use half of second block and allocate remainder
        assertEquals(2, mem.getList().get(0).getLength());
        MemHandle handTwo = mem.insert("TTTA", 4);
        assertEquals(1, mem.getList().size());
        assertEquals(1, mem.getList().get(0).getLength());
        
        // use remainder
        MemHandle handThree = mem.insert("A", 1);
        assertEquals(0, mem.getList().size());
        
        // handle checks
        assertEquals(0, handOne.getPosition());
        assertEquals(5, handTwo.getPosition());
        assertEquals(6, handThree.getPosition());
    }
    
    
    /**
     * Test insertion when freelist has no available free slots.
     * 
     * @throws IOException
     */
    public void testInsertNoFree() throws IOException {
        // add A's to visually color up the file
        mem.insert("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 40);
        
        // add some free blocks to the list
        // even though they're not really free here, we'll assume they are
        // and overwrite
        LinkedList<MemHandle> list = mem.getList();
        list.add(new MemHandle(0, 1));
        list.add(new MemHandle(5, 2));
        
        // request for a block that is too large, will add to end
        MemHandle handOne = mem.insert("ACGTACGTACGTACGT", 16);
        assertEquals(2, mem.getList().size());
    }
    
    /**
     * Testing the release method on an empty file.
     * 
     * @throws IOException
     */
    public void testReleaseBase() throws IOException {
        Exception exception = null;
        
        try {
            mem.release(new MemHandle(0, 1));
        }
        catch (IOException e) {
            exception = e;
            assertNotNull(exception);
            assertTrue(e instanceof IOException);
        }
    }
    
    /**
     * Testing release method. Middle case.
     * 
     * @throws IOException
     */
    public void testReleaseMiddle() throws IOException {
        // insert some sequences
        MemHandle one = mem.insert("AAAAA", 5); // 2 bytes
        MemHandle two = mem.insert("ACGT", 4); // 1 byte
        MemHandle three = mem.insert(
            "AAAATTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT", 40); // 10 bytes
        
        // size check
        RandomAccessFile raf = new RandomAccessFile("mFile.bin", "r");
        assertEquals(13, raf.length());
        
        // releasing middle
        assertEquals(0, mem.getList().size());
        mem.release(two); // should release middle 1 byte, does not modify file
        assertEquals(13, raf.length());
        assertEquals(1, mem.getList().size()); // added to freelist
        
        // check freeblock handle
        assertEquals(2, mem.getList().get(0).getPosition());
        assertEquals(1, mem.getList().get(0).getLength());
        
        // check no change occurred
        result = new byte[1];
        raf.seek(2);
        result[0] = raf.readByte();
        
        assertEquals("[27]", Arrays.toString(result));
        raf.close();
    }
    
    /**
     * Testing release left side.
     * 
     * @throws IOException
     */
    public void testReleaseLeft() throws IOException {
        // insert some sequences
        MemHandle one = mem.insert("AAAAA", 5); // 2 bytes
        MemHandle two = mem.insert("ACGT", 4); // 1 byte
        MemHandle three = mem.insert(
            "AAAATTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT", 40); // 10 bytes
        
        // size check
        RandomAccessFile raf = new RandomAccessFile("mFile.bin", "r");
        assertEquals(13, raf.length());
        
        // release left
        assertEquals(0, mem.getList().size());
        mem.release(one); // should release left 2 bytes, does not modify file
        assertEquals(13, raf.length());
        
        assertEquals(1, mem.getList().size()); // added to freelist
        
        // check freeblock handle
        assertEquals(0, mem.getList().get(0).getPosition());
        assertEquals(2, mem.getList().get(0).getLength());
        
        // check no change occurred
        result = new byte[2];
        raf.seek(0);
        result[0] = raf.readByte();
        result[1] = raf.readByte();
        
        assertEquals("[0, 0]", Arrays.toString(result));
        raf.close();
    }
    
    /**
     * Test release on last block of file.
     * 
     * @throws IOException
     */
    public void testReleaseRight() throws IOException {
        // insert some sequences
        MemHandle one = mem.insert("AAAAA", 5); // 2 bytes
        MemHandle two = mem.insert("ACGT", 4); // 1 byte
        MemHandle three = mem.insert(
            "AAAATTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT", 40); // 10 bytes
        
        // size check
        RandomAccessFile raf = new RandomAccessFile("mFile.bin", "r");
        assertEquals(13, raf.length());
        
        // release right
        assertEquals(0, mem.getList().size());
        mem.release(three); // should release right 10 bytes, trims size
        assertEquals(3, raf.length()); // check size decreased
        
        assertEquals(0, mem.getList().size()); // not added to freelist
    }
    
    /**
     * Test a release followed by an insertion that causes overwrite.
     * 
     * @throws IOException
     */
    public void testReleaseInsert() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("mFile.bin", "r");
        testReleaseLeft(); // release left
        assertEquals(13, raf.length());
        
        MemHandle hand = mem.insert("TTTTTTT", 7); // insert 2 bytes, overwrite
        assertEquals(13, raf.length()); // size doesn't change
        
        // check change
        result = new byte[2];
        raf.seek(0);
        result[0] = raf.readByte();
        result[1] = raf.readByte();
        
        assertEquals("[-1, -4]", Arrays.toString(result));
        raf.close();
        
        // check handle (for sanity)
        assertEquals(0, hand.getPosition());
        assertEquals(7, hand.getLength());
        
    }
    
    /**
     * Test a release followed by an insertion that inserts to end when
     * freelist is empty.
     * 
     * @throws IOException
     */
    public void testReleaseInsertEmptyFreeList() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("mFile.bin", "r");
        testReleaseRight(); // release right
        assertEquals(3, raf.length());
        assertEquals(0, mem.getList().size());

        // new insertion should append, 2 bytes
        MemHandle hand = mem.insert("ACCATT", 6);
        
        // check change
        assertEquals(5, raf.length());
        
        result = new byte[2];
        raf.seek(3);
        result[0] = raf.readByte();
        result[1] = raf.readByte();
        
        assertEquals("[20, -16]", Arrays.toString(result));
        raf.close();
        
        // check handle (for sanity)
        assertEquals(3, hand.getPosition());
        assertEquals(6, hand.getLength());
    }
    
    /**
     * Test a release followed by an insertion that is unable to find
     * suitable freeblock and therefore appends to end.
     * 
     * Note: NEFAppend stands for Not Empty Free Append
     * 
     * @throws IOException
     */
    public void testReleaseInsertNEFAppend() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("mFile.bin", "r");
        testReleaseLeft(); // release left
        assertEquals(13, raf.length());
        assertEquals(1, mem.getList().size());
        
        MemHandle handOne = mem.insert("TTTTTTTTT", 9); // insert 3 bytes, appends
        assertEquals(16, raf.length()); // size increases
        
        // sanity check that overwrite is working
        MemHandle handTwo = mem.insert("ACGTT", 5);
        assertEquals(16, raf.length()); // no change in size
        
        assertEquals(27, raf.readByte());
        assertEquals(-64, raf.readByte());
        raf.close();
        
        // check handle (for sanity)
        assertEquals(13, handOne.getPosition());
        assertEquals(9, handOne.getLength());
        assertEquals(0, handTwo.getPosition());
        assertEquals(5, handTwo.getLength());
    }
    
    /**
     * Test a release following by insertion looks through more than
     * one block to find a suitable location.
     */
    public void testReleaseInsertSearches() {
        
    }
    
    /**
     * Test getSequence method.
     * 
     * @throws IOException
     */
    public void testGetSequence() throws IOException {
        MemHandle one = mem.insert("AAAAA", 5); // 2 bytes
        MemHandle two = mem.insert("ACGT", 4); // 1 byte
        MemHandle three = mem.insert(
            "AAAATTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT", 40); // 10 bytes
        
        byte[] seqOne = mem.getSequence(one);
        byte[] seqTwo = mem.getSequence(two);
        byte[] seqThree = mem.getSequence(three);
        
        assertEquals("[0, 0]", Arrays.toString(seqOne));
        assertEquals("[27]", Arrays.toString(seqTwo));
        assertEquals("[0, -1, 85, -86, 0, 85, -86, -1, 0, -1]",
            Arrays.toString(seqThree));
    }
    
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
     * Private helper for visual checks.
     * 
     * @param temp      byte array to be printed
     * @return          String representation of byte array
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
