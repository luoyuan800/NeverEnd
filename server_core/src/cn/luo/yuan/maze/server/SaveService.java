package cn.luo.yuan.maze.server;


import cn.luo.yuan.maze.utils.Random;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by gluo on 12/9/2016.
 */
public class SaveService {
    public static File root;
    private static SaveService instance;
    private HashSet<String> nameSet = new HashSet<>();
    private Random random = new Random(System.currentTimeMillis());

    public synchronized static SaveService instance(){
       if(instance ==null){
           instance = new SaveService();
       }
        return instance;
    }

    public static void setRoot(File root){
        SaveService.root = new File(root, "file");
    }

    private SaveService() {
        if (!root.exists() || !root.isDirectory()) {
            root.mkdirs();
        }
        String[] saves = new File(root, "save").list();
        if(saves!=null) {
            Collections.addAll(nameSet, saves);
        }
    }

    public byte[] getSaveFile(String id) {
        try {
            File file = new File(new File(SaveService.root, "save"), id);
            if (file.exists()) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                FileInputStream fis = new FileInputStream(file);
                int b = fis.read();
                while (b != -1) {
                    bos.write(b);
                    b = fis.read();
                }
                fis.close();
                nameSet.remove(id);
                return bos.toByteArray();
            }
        } catch (Exception e) {
            LogHelper.error(e);
        }
        return null;
    }

    public void delete(String id){
        new File(new File(SaveService.root, "save"), id).delete();
        nameSet.remove(id);
    }

    String saveSave(InputStream is){
        File folder = new File(root, "save");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            StringBuilder sb = new StringBuilder();
            while (sb.length() < 4) {
                sb.append((char) (random.nextInt(25) + 97));
            }
            String name = sb.toString();
            while (nameSet.contains(name)) {
                name = name + (random.nextInt(9) + 1);
            }
            nameSet.add(name);
            File file = new File(folder, name);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            int data = is.read();
            while (data != -1) {
                fos.write(data);
                data = is.read();
            }
            fos.flush();
            fos.close();
            return name;
        } catch (Exception e) {
            LogHelper.error(e);
        }
        return "";
    }

    String saveFile(String name, InputStream is) {
        File folder = new File(root, "exception");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, name);
        if(file.exists()){
            file = new File(folder, name + "_" + random.nextInt());
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            int data = is.read();
            while (data != -1) {
                fos.write(data);
                data = is.read();
            }
            fos.flush();
        } catch (IOException e) {
            LogHelper.error(e);
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                LogHelper.error(e);
            }
        }

        return name;
    }
}
