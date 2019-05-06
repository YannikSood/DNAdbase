import java.io.IOException;

/**
 * Main class for program input from command-line.
 * 
 * @author  adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/1/19
 */
public class DNAdbase {
    /**
     * Default constructor
     */
    public DNAdbase() {
        // nothing to do
    }

    /**
     * Main method invokes sorter with inputs of command file, 
     * hash file, hash table size and memory file.
     * 
     * @param args  String array containing input arguments.
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public static void main(String[] args)
        throws NumberFormatException, IOException {

        new Sorter(args[0], args[1], args[2], args[3]);
    }

}
