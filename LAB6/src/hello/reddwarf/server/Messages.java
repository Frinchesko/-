/**
 * Created by Пошка on 28.11.2017.
 */
package hello.reddwarf.server;

import java.nio.ByteBuffer;

public class Messages {

    public static ByteBuffer encodeString(String s) {
        return ByteBuffer.wrap(s.getBytes());
    }

    public static String decodeString(ByteBuffer message) {
        byte[] bytes = new byte[message.remaining()];
        message.get(bytes);
        return new String(bytes);
    }
}