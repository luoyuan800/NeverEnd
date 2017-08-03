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
    public static SaveService instance = new SaveService();
    private HashSet<String> nameSet = new HashSet<>();
    private Random random = new Random(System.currentTimeMillis());

    private SaveService() {
        File root = new File("save");
        if (!root.exists() || !root.isDirectory()) {
            root.mkdirs();
        }
        Collections.addAll(nameSet, root.list());
    }

    public byte[] getSaveFile(String id) {
        try {
            File file = new File("save/" + id);
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
                file.deleteOnExit();
                return bos.toByteArray();
            }
        } catch (Exception e) {
            LogHelper.error(e);
        }
        return null;
    }

    public String saveFile(byte[] data) {
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
            File file = new File("save/" + name);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
            return name;
        } catch (Exception e) {
            LogHelper.error(e);
        }
        return "";
    }

    String saveFile(String name, InputStream is) {
        File folder = new File(root, "save");
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