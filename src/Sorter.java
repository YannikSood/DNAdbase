import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Sorter class responsible for parsing all data.
 * 
 * @author  adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/1/19
 */
public class Sorter {
    private String cFile; // command file name
    private String hFile; // hash file name
    private final int HTSIZE; // never changes size once program starts
    private String mFile; // memory manager file name

    private DNADataBase dBase; // DNA Database


    /**
     * Default constructor initializes database and variables, 
     * checks for legal inputs and parses input commands from command file.
     * 
     * @param cF                        command file name
     * @param hF                        hash file name
     * @param hTS                       desired hash table size
     * @param mF                        memory manager binary file name
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    public Sorter(String cF, String hF, String hTS,
        String mF) throws NumberFormatException, IOException {

        // initialize hash-table-size constant
        this.HTSIZE = Integer.parseInt(hTS);

        // check legal inputs
        if (HTSIZE % 32 == 0) {
            // file names
            this.cFile = cF;
            this.hFile = hF;
            this.mFile = mF;

            // initialize database
            this.dBase = new DNADataBase(mFile);

            // parsing
            try {
                // command file
                File cmmdFile = new File(cFile);

                // scanner for file
                Scanner fileScan = new Scanner(cmmdFile);
                fileScan.useDelimiter("\\s+");

                while (fileScan.hasNextLine()) {
                    String line = fileScan.nextLine();

                    // line count
                    /*Scanner lineCount = new Scanner(line);
                        int count = 0;
                        while (lineCount.hasNext()) {
                            lineCount.next();
                            count++;
                        }
                        lineCount.close();*/

                    Scanner lineScan = new Scanner(line); // line scanner

                    while (lineScan.hasNext()) {
                        String command = lineScan.next();

                        String seqID = "";
                        String len = "";
                        String seq = "";

                        switch (command) {
                            case "insert":
                                seqID = lineScan.next();
                                len = lineScan.next();
                                seq = fileScan.nextLine();

                                if (stringCheck(seqID) && stringCheck(seq)) {
                                    dBase.insert(seqID, len, seq);
                                }

                                break;
                            case "remove":
                                seqID = lineScan.next();

                                if (stringCheck(seqID)) {
                                    
                                }

                                break;
                            case "print":
                                

                                break;
                            case "search":
                                seqID = lineScan.next();

                                if (stringCheck(seqID)) {
                                    
                                }

                                break;
                            default:
                                // do nothing
                                break;
                        }

                        // printTest(command, seqID, len, seq);
                    }

                    lineScan.close();
                }

                fileScan.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            // do nothing, database not initialized
            System.out.println("Illegal hash-table-size parameter.");
        }
    }

    /**
     * Check that the string contains valid characters A, C, G or T
     * 
     * @param sq   sequence to be checked
     * 
     * @return      true if valid sequence
     */
    private boolean stringCheck(String sq) {
        if (sq.length() == 0) {
            System.out.println("Invalid Sequence: " + sq);
            return false;
        }

        for (int i = 0; i < sq.length(); i++) {
            if (sq.charAt(i) != 'A' && sq.charAt(i) != 'C'
                && sq.charAt(i) != 'G' && sq.charAt(i) != 'T') {

                System.out.println("Invalid Sequence: " + sq);

                return false;
            }
        }

        return true;
    }

    /**
     * Helper method for testing if parser is working correctly.
     * 
     * @param c     Command
     * @param n     Name
     * @param x     x coordinate
     * @param y     y coordinate
     * @param w     width
     * @param h     height
     *//*
    private void printTest(String c, String sID, String l, String s) {
        System.out.println(c + " " + sID + " " + l + "\n" + s);
    }*/

}
