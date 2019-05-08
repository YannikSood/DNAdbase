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
        this.memFile.seek(0); // also have to move this
        
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
            
            return insertHandle;
        }

        // freelist not empty, must go through freelist
        // using first fit
        for (int i = 0; i < freeList.size(); i++) {
            // some variables/conversions to use
            MemHandle freeBlock = freeList.get(i);
            int lenConv = ((sq.length() + 4 - 1) / 4);
            
            // work
            if (freeBlock.getLength() >= lenConv) {
                // remember original insert position before seek
                int temp = (int)memFile.getFilePointer();
                
                // get offset, seek to that position
                int offSet = freeBlock.getPosition();
                memFile.seek(offSet);
                
                // get sequence byte array, insert
                byte[] seq = stringToByteArray(sq);
                int seqPos = (int)memFile.getFilePointer();
                MemHandle insertHandle = new MemHandle(seqPos, len);
                
                memFile.write(seq);
                
                // replace block with resultant space if any
                // getLength() will return size of the freeblock in bytes
                if (lenConv == freeBlock.getLength()) {
                    freeList.remove(i);
                }
                else {
                    int newLen = freeBlock.getLength() - lenConv;
                    int newPos = (int)memFile.getFilePointer();

                    // freelist handles will hold bytes instead
                    freeList.set(i, new MemHandle(newPos, newLen));
                }
                
                // return to original insert position
                memFile.seek(temp);
                
                // return handle of insertion
                return insertHandle;
            }
        }
        
        // couldn't find space on freelist, just add to end of binary file
        byte[] seq = stringToByteArray(sq);
        int seqPos = (int)memFile.getFilePointer();
        MemHandle insertHandle = new MemHandle(seqPos, len);
        
        memFile.write(seq);
        
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
        
        // if removing from end of binary file, resize
        // else add to freelist (no alter binary file, insert will overwrite)
        int lenConv = ((seqLen + 4 - 1) / 4);
        
        if (seqPos + lenConv == memFile.length()) {
            memFile.setLength(memFile.length() - lenConv);
            memFile.seek(memFile.length());
        }
        else {
            // calculations to keep list ordered by offset
            int addPos = 0;
            boolean found = false;
            
            if (freeList.size() == 0) { // empty list
                addPos = 0;
                found = true;
            }
            else if (freeList.size() == 1) { // one element
                if (freeList.get(0).getPosition() > seqPos) { // greater than
                    addPos = 0;
                    found = true;
                }
                else { // less than
                    addPos = 1;
                    found = true;
                }
            }
            else { // more than one element
                for (int i = 0; !found && i < freeList.size(); i++) {
                    if (freeList.get(i).getPosition() > seqPos) {
                        addPos = i;
                        found = true;
                    }
                }
            }
            
            // add and merge adjacent slots if needed
            if (!found) {
                freeList.add(new MemHandle(seqPos, lenConv));
                update(freeList.size() - 1);
            }
            else {
                freeList.add(addPos, new MemHandle(seqPos, lenConv));
                update(addPos);
            }
            
            memFile.seek(temp); // return to home
        }
    }

    /**
     * Get back a copy of a stored sequence.
     * 
     * @param h             memory handle storing data
     * @return              byte array containing sequence
     * @throws IOException
     */
    public byte[] getSequence(MemHandle h) throws IOException {
        // remember original grab position before seek
        int orig = (int)memFile.getFilePointer();
        
        // prepare byte array
        int nBytes = ((h.getLength() + 4 - 1) / 4);
        byte[] temp = new byte[nBytes];
        
        // grab sequence
        memFile.seek(h.getPosition());
        
        for(int i = 0; i < nBytes; i++) {
            temp[i] = memFile.readByte();
        }

        // return to original insert position and return sequence
        memFile.seek(orig);
        return temp;
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
     * Get list size
     * 
     * @return size
     */
    public int getListSize() {
        return this.freeList.size();
    }
    
    /**
     * This method is called at the end of a removal to
     * handle adjacent freeblock merging.
     * 
     * @precondition    index i is within range
     * @param i         index in freelist to be updated
     */
    public boolean update(int i) {
        if (freeList.size() == 0) { // empty
            return false;
        }
        else if (freeList.size() == 1) { // one element
            return false; // nothing to do
        }
        else { // more than one element
            MemHandle curr = freeList.get(i);
            
            if (i == 0) { // left edge
                MemHandle next = freeList.get(i + 1);
                
                // extend current, remove next
                if (curr.getPosition()
                    + curr.getLength() == next.getPosition()) {
                        curr.setLength(curr.getLength() + next.getLength());
                        freeList.remove(i + 1);
                        
                        return true;
                }
            }
            else if (i == freeList.size() - 1) { // right edge
                MemHandle prev = freeList.get(i - 1);
                
                // extend prev's size to include current, remove current
                if (curr.getPosition() == prev.getPosition()
                    + prev.getLength()) {
                        prev.setLength(prev.getLength() + curr.getLength());
                        freeList.remove(curr);
                        
                        return true;
                }
            }
            else { // somewhere in middle
                MemHandle prev = freeList.get(i - 1);
                MemHandle next = freeList.get(i + 1);
                
                // both sides adjacent
                if ((curr.getPosition() == prev.getPosition()
                    + prev.getLength()) &&  (curr.getPosition()
                        + curr.getLength() == next.getPosition())) {
                            prev.setLength(prev.getLength() + curr.getLength() + next.getLength());
                            freeList.remove(i + 1);
                            freeList.remove(i);
                            
                            return true;
                }
                
                // left side adjacent
                if (curr.getPosition() == prev.getPosition()
                    + prev.getLength()) {
                        prev.setLength(prev.getLength() + curr.getLength());
                        freeList.remove(curr);
                        
                        return true;
                }
                
                // right side adjacent
                if (curr.getPosition()
                    + curr.getLength() == next.getPosition()) {
                        curr.setLength(curr.getLength() + next.getLength());
                        freeList.remove(i + 1);
                        
                        return true;
                }
            }
        }
        
        return false;
    }

}
