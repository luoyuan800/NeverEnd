package cn.luo.yuan.maze.client.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.ArrayMap;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.BaseActivity;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Resource {
    private static final ArrayMap<Object, Drawable> drawableCache = new ArrayMap<>(10);
    private static Context context;

    public static Drawable getSkillDrawable(Skill skill){
        return loadImageFromAssets("skill/" + skill.getClass().getSimpleName() + ".png", false);
    }

    private static void addToCache(Object key, Drawable drawable) {
        if (!drawableCache.containsKey(key)) {
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
        if (context != null) {
            return context.getString(id);
        } else {
            return String.valueOf(id);
        }
    }
    public static String getString(int id, Object...args) {
        return String.format(getString(id), (Object[]) args);
    }

    public static Drawable loadImageFromAppFolder(String name, boolean cache){
        Drawable drawable = null;
        if(cache) {
            drawable = drawableCache.get(name);
        }
        if (drawable == null) {
            try {
                File image = new File(context.getDir("image", Context.MODE_PRIVATE), name);
                if(image.exists()) {
                    drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(new FileInputStream(image)));
                    if (cache) {
                        addToCache(name, drawable);
                    }
                }
            } catch (IOException e) {
                //LogHelper.logException(e, "False to load image from app folder: " + name);
            }
        }
        return drawable;
    }

    public static Drawable loadImageFromAssets(String name, boolean cache) {
        Drawable drawable = null;
        if(cache) {
            drawable = drawableCache.get(name);
        }
        if (drawable == null) {
            try {
                drawable = new BitmapDrawable((Resources) null, BitmapFactory.decodeStream(context.getAssets().open(name)));
                if(cache) {
                    addToCache(name, drawable);
                }
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

    public static String[] getFilesInAssets(String folder) {
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

    public static void askWritePermissions(final BaseActivity.PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context instanceof BaseActivity) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (((BaseActivity) context).shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SimplerDialogBuilder.build("我需要获取读写外部存储卡的权限用于读取头像图片和记录游戏运行时发生的异常，您是否同意？", getString(R.string.conform), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((BaseActivity) context).listener = listener;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ((BaseActivity) context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }
                        }
                    }, getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, context);
                }

            }
        }
    }

    public static Drawable loadImageFromSD(String name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/neverend/" + name;
        if (new File(path).exists()) {
            return Drawable.createFromPath(path);
        } else {
            return null;
        }
    }

    public static Drawable loadDrawableFromBytes(byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return new BitmapDrawable(context.getResources(),bitmap);
    }

    public static void saveImageIntoAppFolder(String name, InputStream is){
        File file = new File(context.getDir("image", Context.MODE_PRIVATE), name);
        if(!file.exists()){
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            int b = is.read();
            while (b!=-1){
                fos.write(b);
                b = is.read();
            }
        } catch (IOException e) {
           LogHelper.logException(e, "write image to app");
        } finally {
            if(fos!=null){
                try {
                    fos.flush();
                    fos.close();
                }catch (Exception e){
                    LogHelper.logException(e, "fuse image");
                }
            }
        }
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if(nif.getInterfaceAddresses()!=null && nif.getInterfaceAddresses().size() > 0) {
                    boolean skip = false;
                    for(InterfaceAddress address : nif.getInterfaceAddresses()){
                        if(address.toString().contains("127.0.0.1")){
                            skip = true;
                            break;
                        }
                    }
                    if(skip) continue;
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }
        } catch (Exception ex) {
            LogHelper.logException(ex, "Query MAC");
        }
        return "02:00:00:00:00:00";
    }

    public static String getVersion() {
        try {
            String pkName = context.getPackageName();
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return versionCode + "";
        } catch (Exception e) {
            return "0";
        }
    }

}
