package com.peachy.videoplayer;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {

    public static String loadJSONFromAssetFile(Context cx, String fileName) {
        String json = null;
        InputStream is;

        try {
            is = cx.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String loadJSONFromFile(String path) {
        String json = null;
        FileInputStream fileInputStream;

        try {
            File file = new File(path);
            fileInputStream = new FileInputStream(file);

            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


}
