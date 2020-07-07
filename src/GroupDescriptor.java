import java.nio.ByteBuffer;

public class GroupDescriptor {


    private ByteBuffer buffer;
    private int[] inodeTablePointers;
    private Volume v;


    public GroupDescriptor(Volume v){

        this.v = v;
        buffer = new Buffer().readAndOrder(v.getEntireFilesystem());

    }

    /*
    group descriptor contains the inode table pointer offsets so the for each inode table the group descriptor will provide
    the offset used to find it.
     */


    public void readGroupDescriptor(){
         //each inode table is 32 bytes long
        inodeTablePointers = new int[v.getBlockGroupCount()];
        int counter = 0;
        while(counter < v.getBlockGroupCount()){
            int currentInodeTablePointer = (Ext2StaticConstants.GROUP_DESCRIPTOR_START_POSITION) + (Ext2StaticConstants.INODE_TABLE_SIZE * counter) + Ext2StaticConstants.INODE_TABLE_POINTER_OFFSET;
            inodeTablePointers[counter] = buffer.getInt(currentInodeTablePointer);
            counter++;
        }


    }

    public void printGroupDescriptor(){
        System.out.println("Group Descriptor Contents:");
        for(int i = 0; i < inodeTablePointers.length; i++){
            System.out.println("Inode table " + i+1 + " pointer in group descriptor is " + inodeTablePointers[i]);
        }
        System.out.println("");
    }


    int[] getInodeTablePointers(){
        return inodeTablePointers;
    }
}
