import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Ext2File {

    private RandomAccessFile randomAccessFile;

    private Volume v;
    private String path;

    public long fileStartPoint;
    public long fileEndPoint;

    public long fileSize = 0;

    Ext2File(Volume vol, String path){


        v = vol;
        this.path = path;

        try {
            this.randomAccessFile = new RandomAccessFile(v.getFilesystemPath(), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        v.setExt2File(this);
        v.setupSuperblock();
        v.readFileSystem(path);

    }


    public byte[] read(long startByte, long length){

        fileStartPoint = startByte;
        fileEndPoint = length;
        v.getReader().bytesToRead = (int) (fileEndPoint - fileStartPoint);

        v.getReader().getType();

        if(v.invalidDirectory){
            return "Invalid path".getBytes();
        }

        System.out.println("Contents at path: " + v.requestedPath + "\n");
        if(v.isResultAFile()){
            return v.getResultData().getBytes();
        }else if(!v.isResultAFile()){

            return "Path leads to a directory".getBytes();

        }else{
            return null;
        }
    }



    public void seek(long position){
        try {
            randomAccessFile.seek(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long position(){
        try {
            return randomAccessFile.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long size(){

        return fileSize;
    }



}
