package cn.luo.yuan.maze.client.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Resource {
    private static Context context;
    private static final ArrayMap<Object, Drawable> drawableCache = new ArrayMap<>(10);

    private static void addToCache(Object key, Drawable drawable){
        if(!drawableCache.containsKey(key)) {
            synchronized (drawableCache) {
                if (drawableCache.size() >= 10) {
                    drawableCache.removeAt(0);
                }
            }
        }
        drawableCache.put(key, drawable);
    }

    public static void init(Context context) {
        Resource.context = context;
    }

    public static String getString(int id) {
        if(context!=null) {
            return context.getString(id);
        }else{
            return String.valueOf(id);
        }
    }

    public static Drawable loadImageFromAssets(String name) {
        Drawable drawable = drawableCache.get(name);
        if(drawable == null) {
            try {
                drawable =  new BitmapDrawable((Resources) null, BitmapFactory.decodeStream(context.getAssets().open(name)));
                addToCache(name, drawable);
            } catch (IOException e) {
                //LogHelper.logException(e, "False to load image from assets: " + name);
            }
        }
        return drawable;
    }

    public static String readStringFromAssets(String path, String name) {
        StringBuilder string = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open((StringUtils.isNotEmpty(path) ? path + "/" : "") + name)));
            String line = reader.readLine();
            while (StringUtils.isNotEmpty(line)) {
                string.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            LogHelper.logException(e, "False for load string from assets: ." + name);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogHelper.logException(e, "Resource->readStringFromAssets{" + name + "}");
                }
            }
        }
        return string.toString();
    }

    public static String[] getFilesInAssets(String folder){
        try {
            return context.getAssets().list(folder);
        } catch (IOException e) {
            return new String[0];
        }
    }

    public static String getSingInfo() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            if (signatures != null && signatures.length > 0) {
                return signatures[0].toCharsString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
