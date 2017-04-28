package cn.luo.yuan.maze.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Resource {
    private static Context context;

    public static void init(Context context) {
        Resource.context = context;
    }

    public static String getString(int id) {
        return context.getResources().getString(id);
    }

    public static Drawable loadImageFromAssets(String name) {
        try {
            return new BitmapDrawable((Resources) null, BitmapFactory.decodeStream(context.getAssets().open(name)));
        } catch (IOException e) {
            //LogHelper.logException(e, false, "False for load image from assets: " + name);
        }
        return null;
    }

    public static String readStringFromAssets(String name) {
        StringBuilder string = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(name)));
            String line = reader.readLine();
            while (StringUtils.isNotEmpty(line)) {
                string.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            LogHelper.logException(e, false, "False for load string from assets: ." + name);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return string.toString();
    }

    public static String readMonsterDescription(String id){
        return readStringFromAssets("monster/" + id);
    }

    public static Drawable loadMonsterImage(int id){
        Drawable drawable = loadImageFromAssets("monster/" + id);
        if(drawable == null){
            drawable = loadImageFromAssets("monster/" + id + ".jpg");
        }
        if(drawable == null){
            drawable = loadImageFromAssets("monster/" + id + ".png");
        }
        if(drawable == null){
            drawable = loadImageFromAssets("monster/wenhao.jpg");
        }
        return drawable;
    }
}
