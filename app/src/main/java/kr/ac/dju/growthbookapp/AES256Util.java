package kr.ac.dju.growthbookapp;

/**
 * Created by geonyounglim on 2017. 7. 17..
 */
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.commons.codec.binary.Base64;
import android.util.Base64;

public class AES256Util {
    private String iv;
    private Key keySpec;

    public AES256Util(String key) throws UnsupportedEncodingException {
        this.iv = key.substring(0, 16); //0번째 자리부터 16-1인 15자리수까지 전부 가져온다.

        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8"); //캐릭터 셋을 utf-8로 변환시킨다
        int len = b.length;
        if(len > keyBytes.length)
            len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len); //b배열을 복사한다 , 0번째부터 읽는다, keybytes 라는 배열에 b를 넣는다, 0번째부터 받은데이터를 입력, len의 길이만큼 복사한다
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        this.keySpec = keySpec;
    }

    // 암호화
    public String aesEncode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));

        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        String enStr = new String(Base64.encodeToString(encrypted, 0));

        return enStr;
    }

    //복호화
    public String aesDecode(String str) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));

        byte[] byteStr = Base64.decode(str.getBytes(), 0);

        return new String(c.doFinal(byteStr),"UTF-8");
    }
}