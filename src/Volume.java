import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Volume {


    private SuperBlock superBlock;
    private GroupDescriptor groupDescriptor;
    private Ext2File ext2File;
    private Reader reader;

    private String path;

    private byte[] filesystem;

    private int blockCount;
    private int blocksPerGroup;
    private int inodeSize;
    private int blockGroupCount;

    private String resultData = "";
    private boolean isResultAFile;
    private String[] directories;

    boolean invalidDirectory = false;

    public String requestedPath;

    Volume(String path){
        this.path = path;
        Path fileLocation = Paths.get(path);

        try{
            filesystem = Files.readAllBytes(fileLocation);
        } catch (IOException readingError){
            readingError.printStackTrace();
            new Ext2Error("Cannot read filesystem").print();
        }

        this.reader = new Reader(this);

    }


    public void setupSuperblock(){
        if(filesystem != null) {
            superBlock = new SuperBlock(filesystem);
            superBlock.parse();


            this.blockCount = superBlock.getTotalBlocks();
            this.blocksPerGroup = superBlock.getBlocksPerGroup();
            this.inodeSize = superBlock.getInodeSize();
            this.blockGroupCount = new Block().getNumberOfBlockGroups(blockCount, blocksPerGroup);
            superBlock.printSuperBlock();
            setupGroupDescriptor();
        }else{
            new Ext2Error("cant getBytes At filesystem at path '" + path + "'").print();
        }
    }

    private void setupGroupDescriptor(){
        //there are copies of the group descriptor but they all the same information so 1 group descriptor
        groupDescriptor = new GroupDescriptor(this);
        groupDescriptor.readGroupDescriptor();

    }



    public void readFileSystem(String path){
        groupDescriptor.printGroupDescriptor();
        this.requestedPath = path;
        int rootInodeNumber = new Block().getBlockNumber(Ext2StaticConstants.ROOT_INODE_POSITION, this);

        //System.out.println("root inode number " + rootInodeNumber);

        byte[] rootInodeData = reader.getBytesAt(rootInodeNumber, inodeSize);
        Inode inode = new Inode(rootInodeData);
        String[] pathArr = formatPath(path);
        Calculator calculator = new Calculator(this, pathArr, inode);
        Inode currentInode = calculator.getInode();


        if(currentInode != null) {
            String inodePermissions = new Permissions().getInodePermissions(currentInode.getFileMode());
            reader.setIsFile(inodePermissions.contains("rf"));
            reader.setInode(currentInode);

            if(inodePermissions.contains("rf")) {
                ext2File.fileSize = getReader().getFileSize(currentInode.getSizeUpper(), currentInode.getSizeLower());
            }else{
                reader.getType();
            }

            //System.out.println("permissions: " + inodePermissions);
        }else{
            invalidDirectory = true;
        }

        //new com.xavier.hickman.Filesystem.Helper().dumpHexBytes(rootInodeData);

    }


    private String[] formatPath(String path){

        if(path.length() == 0){
            path = "/";
        }
        if(path.charAt(0) != '/'){
            path = "/" + path;
        }

        if(path.length() == 1){
            if(path.equals("/")){
                return new String[0];
            }
        }else if(path.length() > 1 && path.contains("/")){
            String[] result = path.split("/");
            return result;
        }else{
            System.out.println("error invalid path");
        }

        return null;
    }

    public SuperBlock getSuperBlock(){
        return superBlock;
    }

    public GroupDescriptor getGroupDescriptor(){
        return groupDescriptor;
    }

    public Ext2File getExt2File(){
        return ext2File;
    }


    public int getInodeSize(){
        return inodeSize;
    }


    public String getResultData(){
        return resultData;
    }

    public void setResultData(String resultData){
        this.resultData += resultData;
    }

    public boolean isResultAFile(){
     return isResultAFile;
    }

    public void setResultBool(boolean isFile){
        isResultAFile = isFile;
    }


    public String[] getDirectories() {
        return directories;
    }

    public void setDirectories(String[] directories){
        this.directories = directories;
    }


    public byte[] getEntireFilesystem(){
        return filesystem;
    }

    public int getBlockGroupCount(){
        return blockGroupCount;
    }

    public Reader getReader(){
        return reader;
    }


    public void setExt2File(Ext2File ext2File){
        this.ext2File = ext2File;
    }

    public String getFilesystemPath(){
        return path;
    }


}
