public class Ext2StaticConstants {

    public static final int START_POSITION = 1024;
    public static final int GROUP_DESCRIPTOR_START_POSITION = 2048;
    public static final int BLOCK_SIZE = 1024;

    public static final int INODE_FILE_MODE_OFFSET = 0;
    public static final int INODE_USER_ID_OFFSET = 2;
    public static final int INODE_SIZE_LOWER_OFFSET = 4;
    public static final int INODE_LAST_ACCESS_OFFSET = 8;
    public static final int INODE_CREATION_TIME_OFFSET = 12;
    public static final int INODE_MOD_TIME_OFFSET = 16;
    public static final int INODE_DELETION_TIME_OFFSET = 20;
    public static final int INODE_GROUP_ID_OFFSET = 24;
    public static final int INODE_LINKS_COUNT_OFFSET = 26;
    public static final int INODE_BLOCK_POINTERS_OFFSET = 40;
    public static final int INODE_SIZE_UPPER_OFFSET = 108;

    public static final int MAGIC_NUMBER_OFFSET = 56;
    public static final int TOTAL_INODES_OFFSET = 0;
    public static final int TOTAL_BLOCKS_OFFSET = 4;
    public static final int BLOCKS_PER_GROUP_OFFSET = 32;
    public static final int INODES_PER_GROUP_OFFSET = 40;
    public static final int INODE_SIZE_OFFSET = 88;
    public static final int FILESYSTEM_NAME_LENGTH = 16;
    public static final int FILESYSTEM_NAME_OFFSET = 120;

    public static final int INODE_POINTERS_COUNT = 15;

    public static final int MAGIC_NUM = 0xef53;

    public static final int INT_SIZE = 4;
    public static final int SHORT_SIZE = 2;
    public static final int BYTE_SIZE = 8;

    public static final int NULL = 0;


    public static final int TOTAL_NUM_DATA_BLOCKS = 12;


    public static final int ROOT_INODE_POSITION = 2;

    public static final int INODE_TABLE_SIZE = 32; //32 bytes long
    public static final int INODE_TABLE_POINTER_OFFSET = 8;


    public static final int INDIRECT_BLOCK_POINTER = 12;
    public static final int DOUBLE_INDIRECT_BLOCK_POINTER = 13;
    public static final int TRIPLE_INDIRECT_BLOCK_POINTER = 14;


    public static final String NULL_REP = "0";

    public static final long MAX_BYTE_ARRAY_LENGTH = 2147483647;

}
