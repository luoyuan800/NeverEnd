package cn.luo.yuan.maze.client.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * Created by gluo on 6/5/2017.
 */
public class FileUtils {
    public static File newFile(String path, Context context){
        return null;
    }

    // With Android Level >= 23, you have to ask the user
    // for permission with device (For example read/write data on the device).
    private static boolean askPermission(int requestId, String permissionName, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            // Check if we have permission
          /*  int permission = ActivityCompat.checkSelfPermission(context, permissionName);


            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                context.requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            }*/
        }
        return true;
    }

}
