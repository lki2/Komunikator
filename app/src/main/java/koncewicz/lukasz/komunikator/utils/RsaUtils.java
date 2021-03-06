package koncewicz.lukasz.komunikator.utils;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaUtils {
    private static final String algorithm = "RSA";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance(algorithm);
        kpg.initialize(1024);
        return kpg.genKeyPair();
    }

    public static PrivateKey privateKeyFromBase64(String privateKey) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        byte[] keyBytes = Base64.decode(privateKey.getBytes(), Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory fact = KeyFactory.getInstance(algorithm);
        return fact.generatePrivate(keySpec);
    }

    public static PublicKey publicKeyFromBase64(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        byte[] keyBytes = Base64.decode(publicKey.getBytes(), Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory fact = KeyFactory.getInstance(algorithm);
        return fact.generatePublic(keySpec);
    }

    public static String keyToBase64(Key key){
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    @Nullable
    public static byte[] RSAEncrypt(final byte[] plain, Key key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plain);
    }

    public static byte[] RSADecrypt(final byte[] encryptedBytes, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedBytes);
    }
}
