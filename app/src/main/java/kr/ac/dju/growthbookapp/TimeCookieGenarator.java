package kr.ac.dju.growthbookapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by geonyounglim on 2017. 7. 21..
 */

public class TimeCookieGenarator {
    private String _time;

    private TimeCookieGenarator () {
        long time = System.currentTimeMillis();

        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
        _time = dayTime.format(new Date(time));

    }

    static public TimeCookieGenarator OneTimeInstance() {
        return new TimeCookieGenarator();
    }

    public String gen(String token) {

        StringBuffer shuffleTime = new StringBuffer();

        for ( int i=4; i< _time.length(); i++){
            shuffleTime.append(_time.charAt(i));
        }

        for ( int j=3; j >= 0; j-- ){
            shuffleTime.append(_time.charAt(j));
        }

        _time = shuffleTime.toString();
        _time = _time.concat(token);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {

        }
        md.update(_time.getBytes());
        byte byteData[] = md.digest();

        StringBuffer hashCodeBuffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }


        return hashCodeBuffer.toString();
    }
}
