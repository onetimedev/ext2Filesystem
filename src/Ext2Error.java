public class Ext2Error {

    private String error;
    private String header = "Ext2 Error: ";

    public Ext2Error(String error){
        this.error = error;
    }

    public void print(){
        //System.out.println(header + error);
    }
}
