import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Reader {

    private boolean isFile;
    private Inode inode;
    private Volume v;

    private RandomAccessFile randomAccessFile;

    private StringBuilder fileData;

    boolean tripInd = false;

    int bytesToRead;

    int indirectionLevel;


    public Reader(Volume v){
        this.v = v;
        try {
            this.randomAccessFile = new RandomAccessFile(v.getFilesystemPath(), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void setIsFile(boolean isFile){
        this.isFile = isFile;
    }

    public void setInode(Inode inode){
        this.inode = inode;
    }


    public void getType(){

        if(inode != null) {
            int[] blockPointers = inode.getBlockPointers();
            checkForIndirection(blockPointers);

            if (isFile) {
                fileData = new StringBuilder();
                for (int i = 0; i < 12; i++) {

                    if (blockPointers[i] != 0) {
                        v.setResultBool(true);
                        readFile(blockPointers[i]);
                    }
                }
            } else {
                for (int i = 0; i < 12; i++) {
                    if (blockPointers[i] != 0) {
                        v.setResultBool(false);
                        readDirectory(blockPointers[i], v.getInodeSize());
                    }
                }
            }
        }else{
            v.invalidDirectory = true;
        }

    }


    private void checkForIndirection(int[] blockPointers) {
        fileData = new StringBuilder();
        if (blockPointers[Ext2StaticConstants.INDIRECT_BLOCK_POINTER] != 0) {

            int pointer = blockPointers[Ext2StaticConstants.INDIRECT_BLOCK_POINTER];
            readIndirect(pointer, v.getInodeSize());
            indirectionLevel = 1;
        }

        if (blockPointers[Ext2StaticConstants.DOUBLE_INDIRECT_BLOCK_POINTER] != 0) {
            int pointer = blockPointers[Ext2StaticConstants.DOUBLE_INDIRECT_BLOCK_POINTER];
            readDblIndirect(pointer, v.getInodeSize());
            indirectionLevel = 2;

        }

        if (blockPointers[Ext2StaticConstants.TRIPLE_INDIRECT_BLOCK_POINTER] != 0) {

            int pointer = blockPointers[Ext2StaticConstants.TRIPLE_INDIRECT_BLOCK_POINTER];
            readTrplIndirect(pointer, v.getInodeSize());
            indirectionLevel = 3;

        }



    }




    private void readIndirect(int blockPointer, int inodeSize){
        byte[] data = getBytesAt((blockPointer * Ext2StaticConstants.START_POSITION), Ext2StaticConstants.START_POSITION);
        ByteBuffer buffer = new Buffer().readAndOrder(data);

        for(int i = 0; i < buffer.limit(); i += 4){
            if(buffer.limit() > i) {
                if (buffer.getInt(i) != 0) {
                    v.setResultBool(true);
                    readIndirectFile(buffer.getInt(i));
                }
            }
        }
    }


    private void readDblIndirect(int blockPointer, int inodeSize){
        byte[] data = getBytesAt((blockPointer * Ext2StaticConstants.START_POSITION), Ext2StaticConstants.START_POSITION);
        ByteBuffer buffer = new Buffer().readAndOrder(data);


        for(int i = 0; i < buffer.limit(); i += 4){
            if(buffer.limit() > i){
                if(buffer.getInt(i) != 0){
                    readIndirect(buffer.getInt(i), inodeSize);
                }
            }
        }
    }

    private void readTrplIndirect(int blockPointer, int inodeSize){
        byte[] data = getBytesAt((blockPointer * Ext2StaticConstants.START_POSITION), Ext2StaticConstants.START_POSITION);
        ByteBuffer buffer = new Buffer().readAndOrder(data);


        for(int i = 0; i < buffer.limit(); i += 4){
            if(buffer.limit() > i) {
                if (buffer.getInt(i) != 0) {
                    readDblIndirect(buffer.getInt(i), inodeSize);
                }
            }
        }
    }

    private void readDirectory(int blockNumber, int inodeSize){
            int startPosition = (blockNumber * Ext2StaticConstants.START_POSITION);
            byte[] blockData = v.getReader().getBytesAt(startPosition, Ext2StaticConstants.START_POSITION);

            ByteBuffer buffer = new Buffer().readAndOrder(blockData);


        //jump amount starts at 4 because an integer is 4 bytes in size
            short jmpAmount = 0;
            int counter = 0;

            ArrayList<String> directories = new ArrayList<String>();
            while(counter < buffer.limit()){
                jmpAmount = buffer.getShort(counter + Ext2StaticConstants.INT_SIZE);

                int inodeOffset = buffer.getInt(counter);

                byte nameBytes = buffer.get(counter + Ext2StaticConstants.INT_SIZE + Ext2StaticConstants.SHORT_SIZE);
                byte[] charBytes = new byte[nameBytes];

                for(int j = 0; j < charBytes.length; j++){
                    charBytes[j] = buffer.get(j + counter + (Ext2StaticConstants.INT_SIZE * Ext2StaticConstants.SHORT_SIZE));
                }

                int containingBlock = new Block().getBlockNumber(inodeOffset, v);

                Inode inode = new Inode(v.getReader().getBytesAt(containingBlock, inodeSize));

                long fileSize = getFileSize(inode.getSizeUpper(), inode.getSizeLower());
                String dirName = new String(charBytes).trim();
                DirectoryData directory = new DirectoryData(
                        String.valueOf(new Permissions().getInodePermissions(inode.getFileMode())),
                        String.valueOf(inode.getNumberHardLinksReferFile()),
                        String.valueOf(inode.hasRootAccess(0)),
                        String.valueOf(inode.hasRootAccess(1)),
                        String.valueOf(fileSize),
                        String.valueOf(inode.getDate()),
                        dirName
                );
                directories.add(directory.getDirectoryData());
                counter += jmpAmount;
            }

            v.setDirectories(toArray(directories));
    }


    private String[] toArray(ArrayList<String> data){

        String[] result = new String[data.size()];

        for(int i = 0; i < data.size(); i++){
            result[i] = data.get(i);
        }

        return result;


    }


    public long getFileSize(int upper32, int lower32){

        long upper = (long) upper32;
        long lower = (long) lower32;


        return (upper << 32 | lower);

    }




    private void readFile(int blockNumber){
        if(bytesToRead > 0) {
            long startPositionFull = (blockNumber * Ext2StaticConstants.START_POSITION) + v.getExt2File().fileStartPoint;
            byte[] blockDataFull = getBytesAt(startPositionFull, bytesToRead);
            v.getExt2File().fileSize += getFileSize(blockDataFull);
            fileData.append(new String(blockDataFull).trim());
            v.getExt2File().fileStartPoint += blockDataFull.length;


            if ((bytesToRead - blockDataFull.length) < 0) {
                bytesToRead = 0;
            } else {
                bytesToRead = bytesToRead - blockDataFull.length;
            }


            v.setResultData(fileData.toString());
        }
    }


    private void readIndirectFile(int blockNumber){
        if(bytesToRead > 0) {

            long fileSize = getFileSize(inode.getSizeUpper(), inode.getSizeLower());



            long startPositionFull = (blockNumber * Ext2StaticConstants.START_POSITION);
            byte[] blockDataFull = getBytesAt(startPositionFull, Ext2StaticConstants.BLOCK_SIZE);
            fileData.append(new String(blockDataFull).trim());
            StringBuilder sparse = new StringBuilder();



            if(v.getExt2File().fileEndPoint >= (fileSize - fileData.length()) && v.getExt2File().fileEndPoint == fileSize) {

                long len = (v.getExt2File().fileEndPoint - v.getExt2File().fileStartPoint);

                if (len < fileData.length()) {

                    String t = fileData.substring((int) (fileData.length() - len), fileData.length());
                    fileData = new StringBuilder();
                    fileData.append(t);

                } else if (len > fileData.length() && len <= Ext2StaticConstants.MAX_BYTE_ARRAY_LENGTH) {

                    bytesToRead -= fileData.length();
                    do {
                        sparse.append(Ext2StaticConstants.NULL_REP);
                        bytesToRead--;
                    } while (bytesToRead > 0 && (sparse.length() + fileData.length()) < fileSize);

                } else if (len > Ext2StaticConstants.MAX_BYTE_ARRAY_LENGTH) {
                    fileData = new StringBuilder();
                    fileData.append("no data to show");
                    System.out.println("Cannot process data longer than " + Ext2StaticConstants.MAX_BYTE_ARRAY_LENGTH);
                }

            }else if(v.getExt2File().fileEndPoint < fileSize && v.getExt2File().fileEndPoint > (fileSize - fileData.length())){

                long len = (v.getExt2File().fileEndPoint - fileSize);



                long amntChar = (v.getExt2File().fileEndPoint - v.getExt2File().fileStartPoint);
                if(amntChar < fileData.length()){

                    String t = fileData.substring((int)(fileData.length() - amntChar), (int) (fileData.length() + len));
                    fileData = new StringBuilder();
                    fileData.append(t);


                }else if(amntChar > fileData.length()){
                    String t = fileData.substring(0, (int) (fileData.length() + len));
                    fileData = new StringBuilder();
                    fileData.append(t);

                    long sparseLen = (v.getExt2File().fileEndPoint - v.getExt2File().fileStartPoint) - fileData.length();
                    do{
                        sparse.append(Ext2StaticConstants.NULL_REP);
                        sparseLen--;
                    }while(sparseLen > 0);
                }

            }else{

                long len = (v.getExt2File().fileEndPoint - v.getExt2File().fileStartPoint);
                fileData = new StringBuilder();
                do{
                    sparse.append(Ext2StaticConstants.NULL_REP);
                    len--;
                }while(len > 0);


            }


            v.setResultData(sparse.toString() + fileData.toString());
        }
    }



    private int getFileSize(byte[] data){
        String da = new String(data).trim();
        return (da.getBytes().length);
    }


    public byte[] getBytesAt(long startByte, long length){

        int arrLen = (int) length;

        byte[] bytes = new byte[arrLen];

        try{
            if(randomAccessFile.length() < startByte){
                new Ext2Error("random access file length is less than the requested start byte\nRAF Length: " + String.valueOf(randomAccessFile.length()) + "\nstart byte: " + String.valueOf(startByte)).print();

            }else {
                randomAccessFile.seek(startByte);
                randomAccessFile.readFully(bytes);
            }
        }catch (IOException e){
            e.printStackTrace();
        }


        return bytes;
    }


}
