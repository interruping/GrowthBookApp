package kr.ac.dju.growthbookapp;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.ByteBuffer;

/**
 * Created by geonyounglim on 2017. 7. 17..
 */

public class UserInfoSafeStorage {
    static private String _fileName = "USER.safe";
    private String _key;

    private Context _context;

    public UserInfoSafeStorage(Context context) {
        _context = context;
        _key = "0123456789ABCDEF";
    }

    public void setKey(String key) {
        _key = key;
    }

    public boolean isSafeUsed() {
        File ext = new File(_context.getFilesDir(),_fileName);
        return ext.exists();
    }

    public void put(UserInfo userinfo) {
        File fd = new File(_context.getFilesDir(), _fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(fd, false);

            AES256Util aes = new AES256Util(_key);
            String eId = aes.aesEncode(userinfo.id);
            String ePw = aes.aesEncode(userinfo.pw);

            ByteBuffer forId = ByteBuffer.allocate(4);
            forId.putInt(eId.length());

            ByteBuffer forPw = ByteBuffer.allocate(4);
            forPw.putInt(ePw.length());

            byte[] idlen = forId.array();
            byte[] pwlen = forPw.array();
            outputStream.write(idlen);
            outputStream.write(pwlen);
            outputStream.write(eId.getBytes());
            outputStream.write(ePw.getBytes());
            outputStream.close();

        } catch ( Exception e ){

        }


    }

    public UserInfo get(){
        File fd = new File(_context.getFilesDir(), _fileName);


        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fd));
            byte[] idlen = new byte[4];
            byte[] pwlen = new byte[4];
            buf.read(idlen, 0, idlen.length);
            buf.read(pwlen, 0, pwlen.length);

            ByteBuffer wrappedIdlen = ByteBuffer.wrap(idlen);
            ByteBuffer wrappedPwlen = ByteBuffer.wrap(pwlen);

            int IntegerIdlen = wrappedIdlen.getInt();
            int IntegerPwlen = wrappedPwlen.getInt();

            byte[] eIdByte = new byte[IntegerIdlen];
            byte[] ePwByte = new byte[IntegerPwlen];

            buf.read(eIdByte, 0, IntegerIdlen);
            buf.read(ePwByte, 0, IntegerPwlen);

            AES256Util aes = new AES256Util(_key);
            String id = aes.aesDecode(new String(eIdByte));
            String pw = aes.aesDecode(new String(ePwByte));

            UserInfo result = new UserInfo();
            result.id = id;
            result.pw = pw;

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return new UserInfo();

    }

    public void delete() {
        new File(_context.getFilesDir(),_fileName).delete();
    }

    static public class UserInfo {
        public String id;
        public String pw;
    }


}
