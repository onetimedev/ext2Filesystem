import java.util.ArrayList;
import java.util.List;

public class InodeFileModes {


    //file permissions
    static final int IFSCK = 0xC000;  // Socket file mode
    static final int IFLNK = 0xA000;  // Symbolic Link file mode
    static final int IFREG = 0x8000;  // Regular File file mode
    static final int IFBLK = 0x6000;  // Block Device file mode
    static final int IFDIR = 0x4000;  // Directory file mode
    static final int IFCHR = 0x2000;  // Character Device file mode
    static final int IFIFO = 0x1000;  // FIFO file mode


    static final int ISUID = 0x0800;  // Set process User ID file mode
    static final int ISGID = 0x0400;  // Set process Group ID file mode
    static final int ISVTX = 0x0200;  // Sticky bit file mode

    //getBytesAt, write and execute permissions (user)
    static final int IRUSR = 0x0100;  // User readBytes file mode
    static final int IWUSR = 0x0080;  // User write file mode
    static final int IXUSR = 0x0040;  // User execute file mode

    //getBytesAt, write and execute permissions (group)
    static final int IRGRP = 0x0020;  // Group readBytes file mode
    static final int IWGRP = 0x0010;  // Group write file mode
    static final int IXGRP = 0x0008;  // Group execute file mode

    //getBytesAt, write and execute permissions (others)
    static final int IROTH = 0x0004;  // Others readBytes file mode
    static final int IWOTH = 0x0002;  // Others write file mode
    static final int IXOTH = 0x0001;  // Others execute file mode


    static final int[] fileModes = {IFSCK, IFLNK, IFREG, IFBLK, IFDIR, IFCHR, IFIFO};

    static final int[] filePermissions = {ISVTX, IRUSR, IWUSR, IXUSR, IRGRP, IWGRP, IXGRP, IROTH, IWOTH, IXOTH};



}
