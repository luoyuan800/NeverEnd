package cn.luo.yuan.maze.server.persistence.serialize;


import cn.luo.yuan.maze.model.IDModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 11/28/2016.
 */
public class ObjectTable<T extends Serializable> {
    private File root;
    private Class<T> table;
    private HashMap<String, SoftReference<T>> cache;

    public ObjectTable(Class<T> table, File root) {
        this.root = new File(root, table.getName());
        if(!this.root.exists()){
            this.root.mkdirs();
        }
        this.table = table;
        cache = new HashMap<>();
    }

    public synchronized String save(T object, String id) throws IOException {
        File entry = buildFile(id);
        if (entry.exists()) {
            throw new IOException("Object with id: " + id + " already existed!");
        }
        saveObject(object, entry);
        cache.put(id, new SoftReference<T>(object));
        return id;
    }

    public synchronized String update(T object, String id) {
        File file = buildFile(id);
        if (file.exists()) {
            file.delete();
        }
        saveObject(object, file);
        cache.put(id, new SoftReference<T>(object));
        return id;
    }

    public String save(T object) throws IOException {
        if (object instanceof IDModel) {
            if (((IDModel) object).getId() == null) {
                ((IDModel) object).setId(UUID.randomUUID().toString());
            }
            return save(object, ((IDModel) object).getId());
        } else {
            return save(object, UUID.randomUUID().toString());
        }
    }

    public synchronized T loadObject(String id) {
        T object = null;
        SoftReference<T> ref = cache.get(id);
        if (ref != null) {
            object = ref.get();
        }
        if (object == null) {
            String name = getName(id);
            object = load(name);
        }
        return object;
    }

    public List<T> loadAll() {
        List<T> list = new ArrayList<>();
        if (root.isDirectory()) {
            for (File file : root.listFiles()) {
                T entry = loadEntry(file);
                if (entry != null)
                    list.add(entry);
            }
        }
        return list;
    }

    public List<String> loadAllId(){
        List<String> list = new ArrayList<>();
        if (root.isDirectory()) {
            Collections.addAll(list, root.list());
        }
        return list;
    }

    public synchronized void clear() {
        if(root.isDirectory()){
            for(File file : root.listFiles()){
                file.delete();
            }
        }
        root.delete();
        cache.clear();
    }

    public void delete(String id) {
        buildFile(id).delete();
        cache.remove(id);
    }

    public void fuse() throws IOException {
        for (SoftReference<T> ref : cache.values()) {
            if(ref!=null) {
                T t = ref.get();
                if (t != null) {
                    if (t instanceof IDModel) {
                        update(t, ((IDModel) t).getId());
                    }
                }
            }
        }
    }

    public void close(){
        cache.clear();
    }

    private void saveObject(T object, File entry) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(entry));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            //LogHelper.logException(e,"ObjectDb->save{" + object + ", " + id + "}");
        }
    }

    private String getName(String id) {
        return id;
    }

    private T load(String id) {
        File entry = buildFile(id);
        return loadEntry(entry);
    }

    private T loadEntry(File entry) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(entry))) {
            Object o = ois.readObject();
            ois.close();
            return table.cast(o);
        } catch (IOException | ClassNotFoundException e) {
            //LogHelper.logException(e,"Sqlite->load{" + name + "}");
        }
        return null;
    }

    private File buildFile(String id) {
        return new File(root, id);
    }

    public int size(){
        return root.isDirectory() ? root.list().length : 0;
    }
}
