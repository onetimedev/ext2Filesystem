import java.nio.ByteBuffer;
import java.util.Date;

public class Inode {

    //offset = starting byte


    //found information about inode structure at - https://wiki.osdev.org/Ext2

    //TS = timestamp

    private short fileMode;
    private short ownerUserId;
    private int fileSize; //size in bytes lower 32 bits
    private int lastAccessedTS;
    private int creationTS;
    private int lastModifiedTS;
    private int deletedTS;
    private short groupIdOwner;
    private short numberHardLinksReferFile;
    private int first12Pointers;
    private int indirectPointer;
    private int doubleIndirectPointer;
    private int tripleIndirectPointer;
    private int fileSizeUpper32; //upper 32 bits

    private static int[] inodePointers;


    private ByteBuffer buffer;


    public Inode(byte[] bytes){
        buffer = new Buffer().readAndOrder(bytes);


        inodePointers = new int[Ext2StaticConstants.INODE_POINTERS_COUNT];
        setupInode();
    }


    public void setupInode(){


        fileMode = buffer.getShort(Ext2StaticConstants.INODE_FILE_MODE_OFFSET);
        ownerUserId = buffer.getShort(Ext2StaticConstants.INODE_USER_ID_OFFSET);
        fileSize = buffer.getInt(Ext2StaticConstants.INODE_SIZE_LOWER_OFFSET);
        lastAccessedTS = buffer.getInt(Ext2StaticConstants.INODE_LAST_ACCESS_OFFSET);
        creationTS = buffer.getInt(Ext2StaticConstants.INODE_CREATION_TIME_OFFSET);
        lastModifiedTS = buffer.getInt(Ext2StaticConstants.INODE_MOD_TIME_OFFSET);
        deletedTS = buffer.getInt(Ext2StaticConstants.INODE_DELETION_TIME_OFFSET);
        groupIdOwner = buffer.getShort(Ext2StaticConstants.INODE_GROUP_ID_OFFSET);
        numberHardLinksReferFile = buffer.getShort(Ext2StaticConstants.INODE_LINKS_COUNT_OFFSET);


        int sizeOfInt = 4;

        for(int i = 0; i < Ext2StaticConstants.INODE_POINTERS_COUNT; i++){
            inodePointers[i] = buffer.getInt(Ext2StaticConstants.INODE_BLOCK_POINTERS_OFFSET + (i * sizeOfInt));
        }

        fileSizeUpper32 = buffer.getInt(Ext2StaticConstants.INODE_SIZE_UPPER_OFFSET);


    }



    int[] getBlockPointers(){
        if(inodePointers != null) {
            return inodePointers;
        }else{
            return null;
        }
    }




    public int hasRootAccess(int type){


        switch(type){

            case 0:
                //user
                return (int) ownerUserId;
            case 1:
                //guest
                return (int) groupIdOwner;
        }

        return 0;
    }


    public int getSizeLower(){
        return fileSize;
    }

    public int getSizeUpper(){
        return fileSizeUpper32;
    }

    public short getNumberHardLinksReferFile(){
        return numberHardLinksReferFile;
    }


    public Date getDate(){
        return new Date((long) creationTS* 1000);
    }


    public short getFileMode(){
        return fileMode;
    }




}
