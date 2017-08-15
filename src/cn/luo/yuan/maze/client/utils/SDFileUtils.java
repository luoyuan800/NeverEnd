package cn.luo.yuan.maze.client.utils;

import android.content.Context;
import android.os.Environment;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by gluo on 6/5/2017.
 */
public class SDFileUtils {

    public static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/neverend/";
    private static String DB_NAME = Sqlite.DB_NAME;

    public static List<String> getFilesListFromSD(String folder){
        return Arrays.asList(new File(SDFileUtils.SD_PATH, folder).list());
    }

    public static File newFileInstance(String folder, String fileName, boolean delete) {
        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(folder + "/" + fileName);
        if (file.exists() && delete) {
            file.delete();
        } else {
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

    public static List<Serializable> unzipObjects(File zip, Context context) throws IOException {
        List<Serializable> seris = new ArrayList<>();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
        ObjectInputStream ois = new ObjectInputStream(zis);
        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            try {
                Object o = ois.readObject();
                if(o instanceof Serializable){
                    seris.add((Serializable) o);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            entry = zis.getNextEntry();
        }
        zis.close();
        zip.deleteOnExit();
        return seris;
    }

    public static void clearLog() {
        for (File file : LogHelper.getLogs()) {
            file.deleteOnExit();
        }
    }

    public static String zipLogFiles(String uuid) {
        uuid.replaceAll("\\?", "NN");
        FileOutputStream fos;
        ZipOutputStream zos = null;
        String name = SD_PATH + "/" + uuid + ".maze";
        File fileInSD = newFileInstance(SD_PATH, uuid + ".maze", true);
        try {
            fos = new FileOutputStream(fileInSD);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            for (File file : LogHelper.getLogs()) {
                zipFile(zos, file);
            }
        } catch (Exception e) {
            LogHelper.logException(e, "Zip log");
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LogHelper.logException(e, "Zip log");
            }
        }
        return name;
    }

    public static String zipFiles(String uuid, List<File> files) {
        uuid.replaceAll("\\?", "NN");
        FileOutputStream fos;
        ZipOutputStream zos = null;
        String name = SD_PATH + "/" + uuid + ".maze";
        File fileInSD = newFileInstance(SD_PATH, uuid + ".maze", true);
        try {
            fos = new FileOutputStream(fileInSD);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            for (File file : files) {
                zipFile(zos, file);
            }
        } catch (Exception e) {
            LogHelper.logException(e, "Zip file");
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LogHelper.logException(e, "Zip file");
            }
        }
        return name;
    }

    private static void zipFile(ZipOutputStream zos, File file) {
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

    public static void deleteFile(String folder, String name){
        deleteFile(new File(new File(SD_PATH, folder), name));
    }

    public static void deleteFile(String filePath) {
        deleteFile(new File(filePath));
    }

    public static void deleteFile(File file){
        file.deleteOnExit();
    }

    public static String readStringFromSD(String folder, String name) {
        StringBuilder string = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(SDFileUtils.SD_PATH + folder + "/" + name)));
            String line = reader.readLine();
            while (StringUtils.isNotEmpty(line)) {
                string.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            LogHelper.logException(e, "False for load string from SD: ." + name);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogHelper.logException(e, "Resource->readStringFromSD{" + name + "}");
                }
            }
        }
        return string.toString();
    }

    public static void saveStringIntoSD(String folder, String file, String msg){
        File folderFile = new File(folder);
        if(!folderFile.exists()){
            folderFile.mkdirs();
        }
        File filfile = new File(folderFile, file);
        try (FileWriter writer = new FileWriter(filfile)){
            writer.write(msg);
        } catch (IOException e) {
            LogHelper.logException(e, "saveStringIntoSD: " + folder + "/" + file);
        }
    }

    public static void deleteFileFromSD(String folder, String file){
        new File(new File(SD_PATH, folder), file).deleteOnExit();
    }
}
