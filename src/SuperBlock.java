import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SuperBlock {

    private String volumeName;
    private int totalInodes;
    private short magicNumber;
    private int totalBlocks;
    private int blocksPerGroup;
    private int inodesPerGroup;
    private int inodeSize; //in bytes


    private ByteBuffer buffer;

    public SuperBlock(byte[] bytes){

        buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer = new Buffer().readAndOrder(bytes);
    }


    public void parse(){
        volumeName = getFilesystemName();
        magicNumber = (buffer.getShort((Ext2StaticConstants.START_POSITION + Ext2StaticConstants.MAGIC_NUMBER_OFFSET)));
        totalInodes = buffer.getInt(Ext2StaticConstants.START_POSITION + Ext2StaticConstants.TOTAL_INODES_OFFSET);
        inodesPerGroup = buffer.getInt(Ext2StaticConstants.START_POSITION + Ext2StaticConstants.INODES_PER_GROUP_OFFSET);
        inodeSize = buffer.getInt(Ext2StaticConstants.START_POSITION + Ext2StaticConstants.INODE_SIZE_OFFSET);
        totalBlocks = buffer.getInt(Ext2StaticConstants.START_POSITION + Ext2StaticConstants.TOTAL_BLOCKS_OFFSET);
        blocksPerGroup = buffer.getInt(Ext2StaticConstants.START_POSITION + Ext2StaticConstants.BLOCKS_PER_GROUP_OFFSET);
    }

    private String getFilesystemName(){
        String volumeName;

        byte[] vName = new byte[Ext2StaticConstants.FILESYSTEM_NAME_LENGTH];
        for(int i = 0; i < Ext2StaticConstants.FILESYSTEM_NAME_LENGTH; i++){
            vName[i] = buffer.get(Ext2StaticConstants.START_POSITION + Ext2StaticConstants.FILESYSTEM_NAME_OFFSET + i);
        }

        volumeName = new String(vName);
        return volumeName;
    }


    public void printSuperBlock(){
        System.out.println("Superblock Contents:");
        System.out.println("com.xavier.hickman.Filesystem.Volume Name: " + volumeName);
        System.out.println("Magic Number: " + magicNumber);
        System.out.println("Total Inodes: " + totalInodes);
        System.out.println("Total Inodes Per Group: " + inodesPerGroup);
        System.out.println("Total size of inodes: " + inodeSize);
        System.out.println("number of blocks: " + totalBlocks);
        System.out.println("number of blocks per group: " + blocksPerGroup);
        System.out.println("number of block groups: " + new Block().getNumberOfBlockGroups(totalBlocks, blocksPerGroup));
        System.out.println("");
    }



        public int getTotalInodes(){
            return totalInodes;
        }

        public int getTotalBlocks(){
            return totalBlocks;
        }

        public int getNumberOfInodesPerGroup(){
            return inodesPerGroup;
        }


        public int getInodeSize(){
            return inodeSize;
        }

        public int getBlocksPerGroup(){
            return blocksPerGroup;
        }


}
