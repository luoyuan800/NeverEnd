package cn.luo.yuan.maze.client.utils;

import android.content.Context;
import android.os.Environment;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.persistence.database.Sqlite;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    public static File saveFileIntoSD(InputStream inputStream, String folder, String name) throws IOException {
        File file = SDFileUtils.getOrCreateFile(folder, name);
        FileOutputStream fos = new FileOutputStream(file);
        int i = inputStream.read();
        while (i != -1) {
            fos.write(i);
            i = inputStream.read();
        }
        fos.flush();
        fos.close();
        return file;
    }

    public static List<String> getFilesListFromSD(String folder){
        File file = new File(SDFileUtils.SD_PATH, folder);
        if(file.exists()) {
            return Arrays.asList(file.list());
        }else{
            return Collections.emptyList();
        }
    }
    public static List<String> getFilesListFromSDWithOrder(String folder){
        File file = new File(SDFileUtils.SD_PATH, folder);
        if(file.exists()) {
            File[] fs = file.listFiles();
            Arrays.sort(fs, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return Long.compare(rhs.lastModified(),lhs.lastModified());
                }
            });
            ArrayList<String> rs = new ArrayList<>(fs.length);
            for(File f : fs){
                rs.add(f.getName());
            }
            return rs;
        }else{
            return Collections.emptyList();
        }
    }

    public static File getOrCreateFile(String folder, String fileName){
        return newFileInstance(SD_PATH + "/" + folder, fileName, false);
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
        ZipEntry entry = zis.getNextEntry();
        while (entry != null) {
            try {
                if(!entry.isDirectory()) {
                    File file = new File(context.getCacheDir(), entry.getName());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int i = zis.read();
                    while (i >= 0) {
                        fileOutputStream.write(i);
                        i = zis.read();
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        Object o = ois.readObject();
                        if (o instanceof IDModel) {
                            ((IDModel) o).setId(file.getName());
                        }
                        if (o instanceof Serializable) {
                            seris.add((Serializable) o);
                        }
                    }
                    file.deleteOnExit();
                }else{
                    File file = new File(context.getCacheDir(), entry.getName());
                    if(!file.exists()){
                        file.mkdirs();
                    }
                }
            } catch (Exception e) {
                LogHelper.logException(e, "Read object while unzip");
            }
            entry = zis.getNextEntry();
        }
        zis.close();
        //zip.delete();
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
        deleteFile(new File(SD_PATH + folder, name));
    }

    public static void deleteFile(String filePath) {
        deleteFile(new File(filePath));
    }

    public static void deleteFile(File file){
        file.delete();
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

        File filfile = newFileInstance(SDFileUtils.SD_PATH + folder, file, true);
        try (FileWriter writer = new FileWriter(filfile)){
            writer.write(msg);
        } catch (IOException e) {
            LogHelper.logException(e, "saveStringIntoSD: " + folder + "/" + file);
        }
    }

    public static boolean deleteFileFromSD(String folder, String file){
        return new File(new File(SD_PATH, folder), file).delete();
    }

    public static boolean deleteFolder(String folder) {
        try {
            File dir = new File(SD_PATH + folder);
            for (File f : dir.listFiles()) {
                f.delete();
            }
            if (dir.exists() && dir.list().length > 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "delete folder");
        }
        return true;
    }

}
