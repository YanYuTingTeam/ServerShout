package io.github.theramu.servershout.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Encoding shared with AerVexHorn's authenticated display protocol. */
public final class ShoutDisplayAuth {
    private ShoutDisplayAuth() {}

    public static byte[] parseKey(String value) {
        if (!value.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("hmac-key must contain exactly 64 hexadecimal characters");
        }
        byte[] key = new byte[32];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        }
        return key;
    }

    public static byte[] encode(byte[] key, int type, UUID player, String channel, String content,
                                long timestamp, UUID nonce) throws IOException, GeneralSecurityException {
        if (key == null || key.length != 32 || (type != 1 && type != 2) || (type == 2 && player == null)) {
            throw new IllegalArgumentException("invalid authenticated display message");
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeByte(4);
        out.writeByte(type);
        out.writeLong(timestamp);
        out.writeLong(nonce.getMostSignificantBits());
        out.writeLong(nonce.getLeastSignificantBits());
        if (type == 2) out.writeUTF(player.toString());
        out.writeUTF(channel);
        out.writeUTF(content);
        out.flush();
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        byte[] signature = mac.doFinal(bytes.toByteArray());
        out.write(signature);
        return bytes.toByteArray();
    }
}
