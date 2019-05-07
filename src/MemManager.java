import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * Memory manager stores strings into a binary file (First Fit).
 * Such strings may also be removed from the file. 
 * Memory manager uses a linked list to track free blocks.
 * 
 * @author  adaniel1 (Daniel Almeida) & yannik24 (Yannik Sood)
 * @version 5/5/19
 */
public class MemManager {
    private RandomAccessFile memFile;
    private LinkedList<MemHandle> freeList;

    /**
     * Default constructor for memory manager.
     * 
     * @param   mF              binary file for memory manager
     * @throws IOException      bad input for file name
     */
    public MemManager(String mF) throws IOException {
        // memory file
        this.memFile = new RandomAccessFile(mF, "rw");
        this.memFile.setLength(0); // overwrite previous file?
        
        // free list
        this.freeList = new LinkedList<>();
    }

    /**
     * Store a record and return a handle to it.
     * 
     * @param seq           sequence to insert
     * @param len           sequence's length
     * 
     * @return              memory handle storing offsets
     * @throws IOException
     */
    public MemHandle insert(String sq, int len)
        throws IOException {
        
        // base case for empty freelist
        if (freeList.isEmpty()) {
            // get sequence byte array
            byte[] seq = stringToByteArray(sq);

            // insert sequence, add info to handle and return it.
            int seqPos = (int)memFile.getFilePointer();
            MemHandle insertHandle = new MemHandle(seqPos, len);
            
            memFile.write(seq);
            // System.out.println(seqPos); // printing offset after insert
            // System.out.println(memFile.length()); // file size after insert
            
            // update(); // merge adjacent free blocks if any
            
            return insertHandle;
        }

        // freelist not empty, must go through freelist
        // using first fit
        for (int i = 0; i < freeList.size(); i++) {
            if (freeList.get(i).getLength() >= len) {
                // remember original insert position before seek
                int temp = (int)memFile.getFilePointer();
                
                // get offset, seek to that position
                int offSet = freeList.get(i).getPosition();
                memFile.seek(offSet);
                
                // get sequence byte array, insert
                byte[] seq = stringToByteArray(sq);
                int seqPos = (int)memFile.getFilePointer();
                MemHandle insertHandle = new MemHandle(seqPos, len);
                
                memFile.write(seq);
                
                // replace handle with resultant space if any
                // len is the sequence being inserted
                // getLength() will return size of the free block
                MemHandle freeBlock = freeList.get(i);
                int lenConv = ((sq.length() + 4 - 1) / 4);
                int blckConv = ((freeBlock.getLength() + 4 - 1) / 4);
                
                if (lenConv == blckConv) {
                    freeList.remove(i);
                }
                else {
                    int newLen = freeBlock.getLength() - sq.length();
                    int newPos = (int)memFile.getFilePointer();

                    int newLenInBytes = ((newLen + 4 - 1) / 4);
                    freeList.set(i, new MemHandle(newPos, newLenInBytes * 4));
                }
                
                // return to original insert position
                memFile.seek(temp);
                
                // update(); // merge adjacent free blocks if any
                
                // return handle of insertion
                return insertHandle;
            }
        }
        
        // couldn't find space on freelist, just add to end of binary file
        byte[] seq = stringToByteArray(sq);
        int seqPos = (int)memFile.getFilePointer();
        MemHandle insertHandle = new MemHandle(seqPos, len);
        
        memFile.write(seq);
        
        // update(); // merge adjacent free blocks if any
        
        return insertHandle;
    }

    /**
     * Release space associated with a record.
     * 
     * @param h             memory handle storing data
     * 
     * @precondition        assumes valid release calls (does check empty)
     * @throws IOException  
     */
    public void release(MemHandle h) throws IOException {
        // if the file is empty, return
        if (memFile.length() == 0) {
            throw new IOException("File is empty.");
        }
        
        // remember original remove position before seek
        int temp = (int)memFile.getFilePointer();

        // grab relevant data from handle
        int seqPos = h.getPosition();
        int seqLen = h.getLength();
        
        // seek to the position and remove the record.
        memFile.seek(seqPos);
        
        // if removing from end of binary file, add to freelist and resize else
        // add to freelist (no alter binary file, insert will overwrite)
        // freeList.add(new MemHandle(seqPos, seqLen));
        freeList.add(h);
        
        int lenConv = ((seqLen + 4 - 1) / 4);
        if (seqPos + lenConv == memFile.length()) {
            memFile.setLength(memFile.length() - lenConv);
        }
        
        // return to original insert position and merge adjacent blocks
        memFile.seek(temp);
        update();
    }

    /**
     * Get back a copy of a stored sequence.
     * 
     * @param h     memory handle storing data
     * @return      byte array containing sequence
     */
    public byte[] getSequence(MemHandle h) {
        

        return null;
    }

    /**
     * String parser converts Strings to byte array.
     * 
     * A - 00 - 0
     * C - 01 - 1
     * G - 10 - 2
     * T - 11 - 3
     * 
     * @param s     String to be converted
     */
    public byte[] stringToByteArray(String s) {
        // create byte array of appropriate length
        // each letter is 2 bits
        int numBytes = ((s.length() + 4 - 1) / 4);
        byte[] temp = new byte[numBytes];

        // temp int holds changes
        int b = 0x00;

        // byte counter
        int byteCount = 0;

        // actual work
        for (int i = 0; i < s.length(); i++) {
            switch(s.charAt(i)) {
                case 'A':
                    int a = 0x00 << (6 - ((i % 4) * 2)); // shift
                    b = (b + a); // modify

                    break;
                case 'C':
                    int c = 0x01 << (6 - ((i % 4) * 2)); // shift
                    b = (b + c); // modify

                    break;
                case 'G':
                    int g = 0x02 << (6 - ((i % 4) * 2)); // shift
                    b = (b + g); // modify

                    break;
                case 'T':
                    int t = 0x03 << (6 - ((i % 4) * 2)); // shift
                    b = (b + t); // modify

                    break;
                default:
                    // do nothing
                    break;
            }

            // if a byte is full, add to byte array, increment count and reset
            if ((i + 1) % 4 == 0) {
                temp[byteCount] = (byte)(b & 0xff); // grab byte
                byteCount++;
                b = 0x00;
            }
            // if it wasn't full and we're at end of string, add remainder
            else if (i == s.length() - 1) {
                temp[byteCount] = (byte)(b & 0xff); // grab byte
                byteCount++;
                b = 0x00;
            }

        }

        return temp;
    }
    
    /**
     * Return the freelist for testing purposes.
     * 
     * @return  the list
     */
    LinkedList<MemHandle> getList() {
        return this.freeList;
    }
    
    /**
     * This method is called at the end of a removal to
     * handle adjacent freeblock merging.
     * 
     * @return      true if merging occured
     *              false if freelist is empty or no merge occured
     */
    private boolean update() {
        boolean result = false;
        
        // merge adjacent blocks if any are found in freelist
        for (int i = 0; i < freeList.size(); i++) {
            
            
            result = true;
        }
        
        
        return result;
    }

}
