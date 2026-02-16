

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGO = "AES";

    public static String encrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getKeyBytes(key), ALGO));
        byte[] enc = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(enc);
    }

    public static String decrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(getKeyBytes(key), ALGO));
        byte[] dec = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(dec, "UTF-8");
    }

    private static byte[] getKeyBytes(String key) throws Exception {
        byte[] b = new byte[16];
        byte[] keyBytes = key.getBytes("UTF-8");
        System.arraycopy(keyBytes, 0, b, 0, Math.min(keyBytes.length, 16));
        return b;
    }
}
