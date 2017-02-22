package koncewicz.lukasz.komunikator;

import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import koncewicz.lukasz.komunikator.utils.RsaUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RsaUtilsTest {

    @Test
    public void test() throws Exception {
        String testString = "Test!@123";

        KeyPair kp = RsaUtils.generateKeyPair();
        PublicKey pubKey = kp.getPublic();
        PrivateKey privKey = kp.getPrivate();

        byte[] encryptedString, decryptedString;

        encryptedString = RsaUtils.RSAEncrypt(testString.getBytes(), pubKey);
        assertNotEquals(testString, new String(encryptedString));

        decryptedString = RsaUtils.RSADecrypt(encryptedString, privKey);
        assertEquals(testString, new String(decryptedString));
    }
}