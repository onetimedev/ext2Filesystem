public class Helper {


    public void dumpHexBytes(byte[] bytes){


        int lineCounter = 0;

        for(int i = 0; i < bytes.length; i++){

            if(i % 26 == 0 && i != 0){
                System.out.println("");
                lineCounter = 0;
            }

            String hex = getHex(bytes[i]);
            System.out.print(hex + " ");
            lineCounter++;

            if(i == (bytes.length-1) && lineCounter < 26){
                for(int j = 0; j < (26 - lineCounter); j++){
                    System.out.print("XX ");
                }
            }
        }


    }



    public String getHex(byte byteData){
        return String.format("%02X", byteData);
    }
}
