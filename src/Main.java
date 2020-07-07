public class Main {

    static Volume vol;

    public static void main(String[] arg0){

        vol = new Volume("/Users/xavierhickman/Documents/Computer Science Yr 2/Module 211 - Operating Systems/FileSystemCW/src/com/xavier/hickman/Filesystem/ext2fs");
        file();
    }


    static public void directory(){
        Directory d = new Directory(vol, "/");
        byte[] buf = d.getFileInfo();
        System.out.format(new String(buf).trim());
    }


    static public void file(){
        Ext2File f = new Ext2File(vol, "/two-cities");
        byte[] buf = f.read(0, f.size());
        System.out.format(new String(buf).trim());

    }
}
