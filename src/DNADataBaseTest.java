import java.io.IOException;
import student.TestCase;

/**
 * HH
 * 
 * @author yanniksood
 * @version 05.07.19
 */
public class DNADataBaseTest extends TestCase {
    private DNADataBase dbase;


    /**
     * Unused
     */
    public DNADataBaseTest() {
        // Unused
    }


    /**
     * @throws IOException
     *             Setup
     */
    public void setUp() throws IOException {
        dbase = new DNADataBase("mFile.bin", 64);
    }


    /**
     * Test insert
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public void testInsert() throws NumberFormatException, IOException {
        assertNotNull(dbase.insert("AAAAA", "40",
            "AAAATTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT"));
    }

}
