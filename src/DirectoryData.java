public class DirectoryData {


    private String permissions;
    private String hardLinks;
    private String userId;
    private String groupId;
    private String fileSize;
    private String date;
    private String name;



    public DirectoryData(String permissions, String hardLinks, String userId, String groupId, String fileSize, String date, String name){
        this.permissions = permissions;
        this.hardLinks = hardLinks;
        this.userId = userId;
        this.groupId = groupId;
        this.fileSize = fileSize;
        this.date = date;
        this.name = name;
    }

    public void list(){
        System.out.println(permissions + " " + hardLinks + " " + userId + " " + groupId + " " + fileSize + " bytes " + date + " " + name);
    }

    public String getDirectoryData(){
        String data = permissions + " " + hardLinks + " " + userId + " " + groupId + " " + fileSize + " bytes " + date + " " + name;
        return data;
    }



}
