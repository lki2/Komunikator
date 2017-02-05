package koncewicz.lukasz.komunikator;

import android.util.Log;

import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

import koncewicz.lukasz.komunikator.utils.RsaUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RsaUtilsTest {



    @Test
    public void addition_isCorrect() throws Exception {

        PublicKey pubKf = RsaUtils.publicKeyFromBase64("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDH2XFh9E9NKG116bEOBHWfc3Hz3fXifg70COspg2JasLngf5SBRdAlDFodmHZK9TC/3qPlrT0r4/kyBEGoEcm2zNqiDw27JMxZJyn5bBIqxKJAYZXeFOJPJ8pNV4eSXmnBAodwpP96VcuS34SnBJfClq4E4gQ73ENd6dZAyoTAzQIDAQAB");
        PrivateKey privKf = RsaUtils.privateKeyFromBase64("MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMfZcWH0T00obXXpsQ4EdZ9zcfPd9eJ+DvQI6ymDYlqwueB/lIFF0CUMWh2Ydkr1ML/eo+WtPSvj+TIEQagRybbM2qIPDbskzFknKflsEirEokBhld4U4k8nyk1Xh5JeacECh3Ck/3pVy5LfhKcEl8KWrgTiBDvcQ13p1kDKhMDNAgMBAAECgYBoDhyN/xHHP9R2f33zqXjA0/AyJJChJDeO8pHW5JSyWa/+Zw8gnAP7Nko5fKei8bU3QaoerSvbjXCzjSnOY6ydd0ALTasSeLfpv77BAmSYT4XY7tSKYF8bcebRTNcqjRQOwYWhgPDEPNOFXc/Ea1hSUU4p9Hoq5Pl78UaQLrsZgQJBAP3n63jZN8Kwnkpe2LW58RzABKoXShe787bsgKJSVCFE0O+sKpSeTIpdzaq/LBz+lSKwmM5WVayVMPtnZTMK0O0CQQDJf2REN62Zn0szhBVHGK7mgQXeLhtvfu4tgGUU35oMwAS7Bki1P1t4tec5+C+ryszTniYdBrVQvXNk/sFiUhNhAkEAtJAPhruCyfNsPhtBJcr7yqRLLOKvED3bTYTW3ZiKt7YGl8rp5RLF/8hBkGGvb1ckm6zWnjYLa1YVpVOcRjY06QJBAJTxsfArKAvyj+gYrpHLUR3aKbg+ZNFaBj1kN1PipUDff5+v2XcAymDENZPIPZTDZ/zVXl7+YnC4WeBB4DkJaoECQQCMZYo95vJj7p5UJf8RefeYzHsQ7l4pvaImORvRnYbV4v1dmRXwsTbo7CXFl7wzbiGEWOz09f2a9tThi2VpDe3l");

        String wejscie = "Test!@123";

        byte[] encryptedContent;
        encryptedContent = RsaUtils.RSAEncrypt(wejscie.getBytes(), pubKf);
        assertNotEquals(wejscie, new String(encryptedContent));
        encryptedContent = RsaUtils.RSADecrypt(encryptedContent, privKf);
        assertEquals(wejscie, new String(encryptedContent));
    }
}
