import student.TestCase;

/**
 * Testing the memory handle.
 * 
 * @author  adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/5/19
 */
public class MemHandleTest extends TestCase {
    private MemHandle handle;
    
    /**
     * Default constructor for tests.
     */
    public void setUp() {
        this.handle = new MemHandle(3, 10);
    }
    
    /**
     * Test constructor and getter methods.
     */
    public void testMemHandle() {
        assertEquals(3, handle.getPosition());
        assertEquals(10, handle.getLength());
    }
}
