public class Permissions extends InodeFileModes{


    //use bitwise and operation to determine the file permission
    //if ((attr & permission) == permission) true

    public String getInodePermissions(short fileMode)
    {


        StringBuilder inodeType = new StringBuilder();
        StringBuilder permissions = new StringBuilder();


        for(int i = 0; i < fileModes.length; i++)
        {
            if(fileMode(fileMode, fileModes[i]))
            {
                inodeType.append(getFileModeString(fileModes[i]));
            }
        }

        for(int i = 0; i < filePermissions.length; i++)
        {
            if(fileMode(fileMode, filePermissions[i]))
            {
                permissions.append(getFilePermission(filePermissions[i]));
            }
            else
            {
                permissions.append("-");
            }
        }
        return inodeType.toString() + permissions.toString();
    }

    private boolean fileMode(short fileMode, int permission)
    {
        if((fileMode & permission) == permission)
            return true;
        return false;
    }


    private String getFileModeString(int fileMode)
    {

        switch (fileMode){
            case IFSCK:
                return "socket";
            case IFLNK:
                return "symbolic link";
            case IFREG:
                return "rf";
            case IFBLK:
                return "block device";
            case IFDIR:
                return "d";
            case IFCHR:
                return "char device";
            case IFIFO:
                return "FIFO";
        }

        return "error";
    }


    private String getFilePermission(int fileMode)
    {

        if(fileMode == IRUSR || fileMode == IRGRP || fileMode == IROTH)
        {
            return "r";
        }
        else
            if(fileMode == IWUSR || fileMode == IWGRP || fileMode == IWOTH)
        {
            return "w";
        }
        else
            if(fileMode == IXUSR || fileMode == IXGRP || fileMode == IXOTH)
        {
            return "x";
        }
        else
            if(fileMode == ISVTX){
            return "t";
        }

        return "-";
    }

}
