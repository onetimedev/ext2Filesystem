import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class Directory {

    private RandomAccessFile randomAccessFile;
    Volume v;
    String path;


    public Directory(Volume vol, String path){
        this.v = vol;
        this.path = path;

        try {
            this.randomAccessFile = new RandomAccessFile(v.getFilesystemPath(), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        v.setupSuperblock();
        v.readFileSystem(path);
    }


    public byte[] getFileInfo(){
        String dirData = "";
        String[] data = v.getDirectories();

        if(data != null) {

            System.out.println("Contents at path: " + v.requestedPath + "\n");
            for (int i = 0; i < data.length; i++) {
                dirData += data[i] + "\n";
            }

            return dirData.getBytes();
        }else{
            return "no directories found at this path".getBytes();
        }
    }
}
