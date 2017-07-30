package cn.luo.yuan.maze.client.utils;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by luoyuan on 9/20/15.
 */
public class LogHelper {
    private static String pkgName;
    private static int versionCode;
    static String logFolder = FileUtils.SD_PATH + "/log";

    public static void initLogSystem(Context context){
        pkgName = context.getPackageName();
        try {
            versionCode = context.getPackageManager()
                    .getPackageInfo(pkgName, 0).versionCode;
        }catch (Exception e){
            versionCode = 0;
        }
    }
    public static void logException(final Throwable e, String msg) {
        logException(e, msg, null);
    }

    public static void logException(final Throwable e, String msg, Context context) {
        e.printStackTrace();
        try {

            File file = FileUtils.newFileInstance(logFolder, e.getClass().getSimpleName() + "." + versionCode, true);
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

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    public static File[] getLogs(){
        return new File(logFolder).listFiles();
    }
}
