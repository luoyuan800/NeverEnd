package cn.luo.yuan.maze.persistence.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 11/28/2016.
 */
public class ObjectDB {
    private String table;

    public ObjectDB(String table) {
        this.table = table;
        File root = new File(table);
        if (!root.exists() || !root.isDirectory()) {
            root.mkdirs();
        }
    }

    public synchronized String save(Serializable object, String id) {
        try {
            String path = table + "/" + object.getClass().getName() + "@" + id;
            File file = new File(path);
            file.deleteOnExit();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String save(Serializable object) {
        return save(object, UUID.randomUUID().toString());
    }

    public synchronized <T> T loadObject(Class<T> type, String id) {
        File file = new File(table + "/" + type.getName() + "@" + id);
        T o = load(type, file);
        if (o != null) return o;
        return null;
    }

    private <T> T load(Class<T> type, File file) {
        if (file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                Object o = ois.readObject();
                ois.close();
                return type.cast(o);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public synchronized <T> List<T> loadAllObjects(Class<T> type) {
        File[] roots = new File(table).listFiles();
        if (roots == null) {
            return Collections.emptyList();
        }
        ArrayList<T> list = new ArrayList<T>(roots.length);
        for (File file : roots) {
            list.add(load(type, file));
        }
        return list;
    }

    public synchronized void clear() {
        File[] roots = new File(table).listFiles();
        if (roots != null) {
            for (File file : roots) {
                file.delete();
            }
        }

    }

    public void delete(String clazz, String id){
        File file = new File(table + "/" + clazz + "_" + id);
        file.delete();
    }

}
