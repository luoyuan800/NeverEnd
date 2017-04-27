package cn.luo.yuan.maze.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Resource {
    private static Context context;
    public static void init(Context context){
        Resource.context = context;
    }
    public static String getString(int id){
        return context.getResources().getString(id);
    }
    public static Drawable loadImageFromAssets(String name) {
        try {
            return new BitmapDrawable(BitmapFactory.decodeStream(context.getAssets().open(name)));
        } catch (IOException e) {
            LogHelper.logException(e, false,"False for load image from assets.");
        }
        return null;
    }
}
