package cn.luo.yuan.maze.client.utils;

import android.content.Context;
import android.os.Environment;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.persistence.database.Sqlite;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by gluo on 6/5/2017.
 */
public class FileUtils {

    private static String DB_NAME = Sqlite.DB_NAME;
    public static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/neverend/";

    public static File newFileInstance(String folder, String fileName, boolean delete) {
        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(folder + "/" + fileName);
        if (file.exists() && delete) {
            file.delete();
        }else{
            return file;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            file = null;
            LogHelper.logException(e, "newFileInstance @ " + folder + "/" + fileName);
        }
        return file;
    }

    public static void unzipSaveFiles(File saveFile, Context context) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(saveFile));
        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            if (entry.getName().contains(DB_NAME)) {
                File databasePath;
                if (entry.getName().equalsIgnoreCase(DB_NAME)) {
                    databasePath = context.getDatabasePath(DB_NAME);
                } else {
                    databasePath = new File(context.getDatabasePath(DB_NAME).getParent() + "/" + entry.getName());
                }
                databasePath.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(databasePath);
                int i = zis.read();
                while (i >= 0) {
                    fileOutputStream.write(i);
                    i = zis.read();
                }
                fileOutputStream.flush();
            } else {
                FileOutputStream fileOutputStream = context.openFileOutput(entry.getName(), Context.MODE_PRIVATE);
                int i = zis.read();
                while (i >= 0) {
                    fileOutputStream.write(i);
                    i = zis.read();
                }
                fileOutputStream.flush();
            }
            entry = zis.getNextEntry();
        }
        saveFile.deleteOnExit();
    }

    public static String zipSaveFiles(String uuid, Context context, boolean containLogs) {
        FileOutputStream fos;
        ZipOutputStream zos = null;
        String name = SD_PATH + "/" + uuid + ".maze";
        File fileInSD = newFileInstance(SD_PATH, uuid + ".maze", true);
        try {
            fos = new FileOutputStream(fileInSD);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            /*File folder = context.getFilesDir();
            for (File file : folder.listFiles()) {
                try {
                    if(file.isDirectory()){
                        ZipEntry zipfolder = new ZipEntry(file.getName());
                        zos.putNextEntry(zipfolder);
                        for(String fileName : file.list()){

                        }
                    }
                    byte[] bytes = new byte[1024];
                    ZipEntry entry = new ZipEntry(fileName);
                    zos.putNextEntry(entry);
                    FileInputStream fileInputStream = context.openFileInput(fileName);
                    int index = fileInputStream.read(bytes);
                    while (index != -1) {
                        zos.write(bytes);
                        index = fileInputStream.read(bytes);
                    }
                    zos.closeEntry();
                }catch (Exception e){
                    //Ignore
                }
            }*/
            if(containLogs) {
                    for (File file : LogHelper.getLogs()) {
                        try {
                            byte[] bytes = new byte[1024];
                        ZipEntry entry = new ZipEntry(file.getName());
                        zos.putNextEntry(entry);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        int index = fileInputStream.read(bytes);
                        while (index != -1) {
                            zos.write(bytes);
                            index = fileInputStream.read(bytes);
                        }
                        zos.closeEntry();
                        } catch (Exception e) {
                            //Ignore
                        }
                    }

            }
            try {
                File databasePath = context.getDatabasePath(DB_NAME);
                byte[] bytes = new byte[1024];
                ZipEntry entry = new ZipEntry(databasePath.getName());
                zos.putNextEntry(entry);
                FileInputStream fileInputStream = new FileInputStream(databasePath);
                int index = fileInputStream.read(bytes);
                while (index != -1) {
                    zos.write(bytes);
                    index = fileInputStream.read(bytes);
                }
            }catch (IOException e){
                //ignore
            }
        } catch (Exception e) {
            LogHelper.logException(e, "Zip save");
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LogHelper.logException(e, "Zip save");
            }
        }
        return name;
    }
}
