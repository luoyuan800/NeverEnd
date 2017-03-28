package cn.luo.yuan.maze.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by luoyuan on 9/20/15.
 */
public class LogHelper {
    private static String pkgName;
    private static int versionCode;
    public static void initLogSystem(Context context){
        pkgName = context.getPackageName();
        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(pkgName, 0).versionCode;
        }catch (Exception e){
            versionCode = 0;
        }
    }
    public static void logException(final Exception e, boolean upload, String msg) {
        e.printStackTrace();
        try {

            File path = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/neverend/logs/");
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/maze/log/" + e.getClass().getSimpleName() + "." + versionCode);
            if (!file.exists()) {
                file.createNewFile();

            }
            FileWriter writer = new FileWriter(file, false);
            try {
                if (msg != null) {
                    writer.write(msg + "\n");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace(new PrintWriter(writer));
            writer.flush();
            writer.close();
            if (upload) {
                //TODO

            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

}
