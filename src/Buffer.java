import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Buffer {

    /**
     * this method returns an instance of ByteBuffer having read in the bytes passed and reorders them so that
     * they are in little endian format which means the least significant bit in the sequence is stored at the start.
     * @param data byte array of data to read into the buffer and reorder
     * @return returns the buffer with the correct data in the correct byte order
     */
    public ByteBuffer readAndOrder(byte[] data){
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }
}
