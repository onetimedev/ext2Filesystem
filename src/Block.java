public class Block {

    //there is only 1 inode table per block group

    /**
     * This method gets the block offset using the inode offset passed to it. It checks to see if the
     * inode offset is greater than the total number of inodes, if so it will return 0 because this is a corrupt
     * call beacuse the provided inode offset it greater than the total number inodes. If the offset is less then
     * the method will get the inode block group, then get the position of that block group and use that index to
     * address the inode table pointer array to access the correct inode table, then will use that to calculate the
     * correct block and return it as an integer.
     * @param inodeOffset the offset of the inode requesting the block number
     * @param v instance of the com.xavier.hickman.Filesystem.Volume class to provide access to all filesystem data
     * @return an integer which represents the block number in the filesystem
     */
    public int getBlockNumber(int inodeOffset, Volume v){

        int blockNumber = 0;
            if(inodeOffset > v.getSuperBlock().getTotalInodes()){

                new Ext2Error("the inode offset is invalid as it is greater than the total number of inodes found in the superblock").print();

            }else if(inodeOffset < v.getSuperBlock().getTotalInodes()){
                double inodeBlockGroup = getInodeBlockGroup(inodeOffset, v.getSuperBlock().getNumberOfInodesPerGroup()); //this is the block group that the inode is inside of
                int blockGroupPosition = getInodeIndex(inodeOffset, v.getSuperBlock().getNumberOfInodesPerGroup()); //gets the index of the inode in the group descriptors inode table
                int inodeTablePointer = v.getGroupDescriptor().getInodeTablePointers()[blockGroupPosition]; //the inode table inodeBlockGroup of the block group of the block group (1,2 or 3 in this instance)
                double block = calculateContainingBlock(inodeBlockGroup, v.getSuperBlock().getInodeSize(), inodeTablePointer);
                blockNumber = (int) block;
            }
        return blockNumber;
    }

    /**
     * this method calculates the block group using the inode offset and the number of
     * inodes per group in the filesystem, it decrements the offset by 1 because inodes count from
     * the number 1 and arrays start from 0 so decrementing provides the correct offset. Modulo
     * is then used to divide and get the remainder
     * @param inodeNumber inode offset provided by the inode requesting the block number
     * @param inodesPerGroup number of inodes per group read from the superblock
     * @return the remainder of dividing inodeNumber by number of inodes per group (modulo)
     */
    private double getInodeBlockGroup(int inodeNumber, int inodesPerGroup){

        inodeNumber--;
        return (inodeNumber % inodesPerGroup);
    }

    /**
     * this method gets the inodes index in the group descriptors inode table by dividing the two values
     * and returns an integer
     * @param inodeNumber inode offset
     * @param inodesPerGroup number of inodes per group (superblock)
     * @return an integer value
     */
    private int getInodeIndex(int inodeNumber, int inodesPerGroup){
        //gets the index of the inode from the group descriptors inode table
        return (inodeNumber/inodesPerGroup);
    }

    /**
     * this method calculates the block that contains the inode by multiplying the block group number by the size of the inode, then dividing by
     * the starting position of data at byte 1024, then it adds that to the value of the inoide table pointer and then the whole value is multiplied by 1024
     * and this takes us to the inode
     * @param inodeBlockGroup inodes block group
     * @param inodeSize size of the inode
     * @param inodeTablePointer the inode table that the inode resides in
     * @return returns the value of the equation explained above
     */
    private double calculateContainingBlock(double inodeBlockGroup, int inodeSize, int inodeTablePointer){
        return ((inodeBlockGroup * inodeSize / Ext2StaticConstants.START_POSITION) + inodeTablePointer) * Ext2StaticConstants.START_POSITION;
    }


    /**
     * this method calculates the number of block groups in the filesystem, it uses the number of blocks and
     * divides that by the number blocks per group and then does a modulo calculation in an inline if statement
     * to see if the value is 0 and if so it returns the actual value of the block groups and if the modulo is not 0
     * then it will add 1 to the blockGroups value and return that.
     * @param blocks
     * @param blocksPerGroup
     * @return returns the result of calculation described above
     */
    public int getNumberOfBlockGroups(int blocks, int blocksPerGroup){
        int blockGroups = blocks / blocksPerGroup; //get number of block groups
        return (blockGroups % blocksPerGroup) == 0 ? blockGroups : blockGroups+1; //inline if statemnt to check if the modulo value of block groups and blocks per group is 0
    }



}
