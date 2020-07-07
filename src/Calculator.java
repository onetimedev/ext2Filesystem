import java.nio.ByteBuffer;

public class Calculator {


    private Volume v; //instance of volume which is shared by all classes reading on the filesystem
    private String[] path; //path array that is passed by the user for example (/deep/down/in/the/filesystem/there/lived/a/file)
    private Inode inode; //the inode that is currently being looked at, this starts with the root inode and ends up being the value of the inode that points to the file being searched for
    private ByteBuffer buffer; //instance of buffer to order and keep track of the bytes being read


    public Calculator(Volume v, String[] path, Inode inode){
        this.v = v;
        this.path = path;
        this.inode = inode;

    }


    /**
     * this method uses the variables passed through the constructor to locate the inode that points to the file being searched for.
     * the method sets resultInode to the root inode (inode 2) passed into the class constructor, it then runs a for loop over the path
     * array so if the path is /deep/down/in/the/filesystem/there/lived/a/file then the loop runs 9 times. the integer value inodeOffset is used
     * to call getInodeOffset() which is a method that calculates the inode offset of the current segment of the path being searched for.
     * The method then checks to see if the inodeOffset is greater than 0, otherwise the path rested is invalid. A byte a array is created which
     * uses the com.xavier.hickman.Filesystem.Reader class which the instance is accessed through the instance of com.xavier.hickman.Filesystem.Volume, this then calls a method getBytesAt() which will get bytes
     * in the filesystem starting from a defined starting point in this case the block number of the inode and point the reader method will stop reading is
     * the size of the inode and this is how you read from the start of a given inode to the end point of it. The method then creates a new com.xavier.hickman.Filesystem.Inode and sets the result
     * inode value to it and then the loop goes round again until the final inode is found which should point to the file at the end of the path.
     *
     * @return an instance of the com.xavier.hickman.Filesystem.Inode class with the relevant data about the file or directory requested
     */
    public Inode getInode()
    {

        Inode resultInode = inode;

        for(int i = 0; i < path.length; i++){

            int inodeOffset = getInodeOffset(path[i]);

            if(inodeOffset > 0){

                int blockNumber = new Block().getBlockNumber(inodeOffset, v);
                byte[] inodeBytes = v.getReader().getBytesAt(blockNumber, v.getInodeSize());

                resultInode = new Inode(inodeBytes);
                resultInode.setupInode();
            }else{
                new Ext2Error("cant get inode which means the file or dir requested does not exists or is corrupted");
                resultInode = null;
            }


        }


        return resultInode;
    }


    /**
     * this method calculates the current inodeOffset using the current position of the path to check if the
     * located inode is the correct one, the for loop runs 12 times beacuse there are 12 direct block although
     * there are actually 15 but the last 3 and the different levels of indirection and indirection is handled in the reader
     * class. An integer array is created and populated with the block pointers found in the current inode, then if the
     * current block pointer is not 0 then the
     * @param path
     * @return
     */
    private int getInodeOffset(String path){


        int[] blockPointers = inode.getBlockPointers();


        for(int i = 0; i < Ext2StaticConstants.TOTAL_NUM_DATA_BLOCKS; i++){

            if(blockPointers[i] != Ext2StaticConstants.NULL){

                byte[] blockPointerData = v.getReader().getBytesAt((blockPointers[i] * Ext2StaticConstants.START_POSITION), Ext2StaticConstants.START_POSITION);
                buffer = new Buffer().readAndOrder(blockPointerData);

                return getStringData(path);
            }else{
                System.out.println("Block pointer " + i + " was 0");
            }


        }


        return 0;

    }


    private int getStringData(String path){

        short jumpAmount = 0;
        int t = 0;
        while(t < buffer.limit()){
            t += jumpAmount;
            jumpAmount = jump(t);

            byte[] nameBytes = new byte[getPathNameLength(t)];

            for(int i = 0; i < nameBytes.length; i++){
                int jump =  i + t + Ext2StaticConstants.BYTE_SIZE;
                nameBytes[i] = buffer.get(jump);
            }


            byte[] test = path.getBytes();

            boolean result = true;
            if(test.length == nameBytes.length) {
                for (int i = 0; i < test.length; i++) {
                    if (test[i] != nameBytes[i]) {
                        result = false;
                        break;
                    }

                }
            }else{
                result = false;
            }

            //byte buffer's limit() method returns a value that has 3 empty bytes so to prevent
            // an index out of bounds exception i remove 3 from the .limit() value to make sure that the condition is correct.
            if(result && t < (buffer.limit() -3)){
                return buffer.getInt(t);
            }


        }
        return 0;
    }


    private byte getPathNameLength(int counter){

        if((counter+6) > (buffer.limit()-3)){
            new Ext2Error("error name len was 0");
        }else {
            return buffer.get(counter + 6);
        }

        return (byte) 0;
    }


    private short jump(int counter){
        //jumping 4 places to the next integer (integer size is 4)
        if((counter+4) > (buffer.limit()-3)){
            new Ext2Error("error jump was 0");
        }else {
            return buffer.getShort(counter + 4);
        }

        return 0;
    }



}
