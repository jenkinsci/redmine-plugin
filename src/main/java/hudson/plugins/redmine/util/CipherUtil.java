package hudson.plugins.redmine.util;

import java.security.MessageDigest;

/**
 *
 * @author Yasuyuki Saito
 */
public abstract class CipherUtil {

    /** */
    public static final String SHA1 = "SHA-1";

    /**
     *
     * @param value
     * @return
     * @throws Exception
     */
    public static String encodeSHA1(String value) throws Exception {
        return encode(value, SHA1);
    }

    /**
     *
     * @param value
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static String encode(String value, String algorithm) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            byte[] textBytes = value.getBytes();
            md.update(textBytes);
            byte[] digestBytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte digest : digestBytes) {
                sb.append(String.format("%02x", digest));
            }

            return sb.toString();
        } catch (Exception e) {
            throw e;
        }
    }
}
