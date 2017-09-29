package cn.luo.yuan.maze.serialize;


import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 11/28/2016.
 */
public class ObjectTable<T extends Serializable> implements Runnable {
    private String root;
    private Class<T> table;
    private HashMap<String, SoftReference<T>> cache;
    private ReferenceQueue<T> queue;

    public ObjectTable(Class<T> table, File root) {
        queue = new ReferenceQueue<T>();
        File file = new File(root, table.getName());
        if (!file.exists()) {
            file.mkdirs();
        }this.root = file.getAbsolutePath();

        this.table = table;
        cache = new HashMap<>();
    }

    public synchronized String save(T object, String id) throws IOException {
        File entry = buildFile(id);
        entry.setWritable(true);
        if (entry.exists()) {
            return update(object, id, true);
        } else {
            if(object instanceof IDModel){
                ((IDModel) object).setId(id);
            }
            saveObject(object, entry);
            cache.put(id, new SoftReference<T>(object, queue));
        }
        return id;
    }

    public synchronized String update(T object, String id, boolean addToCache) throws IOException {
        File file = buildFile(id);
        if (file.exists()) {
            file.delete();
        }
        saveObject(object, file);
        if(addToCache) {
            cache.put(id, new SoftReference<T>(object, queue));
        }
        return id;
    }

    public String save(T object) throws IOException {
        String id;
        if (object instanceof IDModel) {
            if (StringUtils.isEmpty(((IDModel) object).getId())) {
                id = UUID.randomUUID().toString();
            }else{
                id = ((IDModel) object).getId();
            }
            return save(object, id);
        } else {
            return save(object, UUID.randomUUID().toString());
        }
    }

    public synchronized T loadObject(String id) {
        T object = null;
        if (id != null) {
            SoftReference<T> ref = cache.get(id);
            if (ref != null) {
                object = ref.get();
            }
            if (object == null) {
                String name = getName(id);
                object = load(name);
                if (object instanceof IDModel) {
                    cache.put(((IDModel) object).getId(), new SoftReference<T>(object));
                }
            }
        }
        return object;
    }

    public synchronized List<T> loadAll() {
        List<T> list = new ArrayList<>();
        File file = new File(root);
        if (file.isDirectory()) {
            for (String id : file.list()) {
                T obj = loadObject(id);
                if (obj != null) {
                    if (obj instanceof IDModel && ((IDModel) obj).isDelete()) {
                        continue;
                    }
                    list.add(obj);
                }
            }
        }
        return list;
    }

    public synchronized void clear() {
        File file = new File(root);
        if (file.isDirectory()) {
            for (File cfile : file.listFiles()) {
                cfile.delete();
            }
        }
        file.delete();
        cache.clear();
    }

    public synchronized void delete(String id) {
        if(StringUtils.isNotEmpty(id)) {
            buildFile(id).delete();
            cache.remove(id);
        }
    }

    public void fuse() throws IOException {
        for (SoftReference<T> ref : new ArrayList<>(cache.values())) {
            if (ref != null) {
                T t = ref.get();
                if (t != null) {
                    if (t instanceof IDModel && !((IDModel) t).isDelete()) {
                        update(t, ((IDModel) t).getId(), true);
                    }
                }
            }
        }
    }

    public void close() {
        cache.clear();
    }

    @Override
    public synchronized void run() {
        try {
            Reference<? extends T> poll = queue.poll();
            if (poll != null) {
                for (String id : cache.keySet()) {
                    SoftReference<T> reference = cache.get(id);
                    if (reference == null || reference.get() == null) {
                        cache.remove(id);
                    }
                }
            }
        }catch (Exception e){
            //Do nothing
            e.printStackTrace();
        }
    }

    public int size() {
        File file = new File(root);
        return file.isDirectory() ? file.list().length : 0;
    }

    public List<T> loadLimit(int start, int row, Index<T> index, Comparator<T> comparator) throws IOException, ClassNotFoundException {
        int realStart = start;
        List<T> objects = loadAll();
        if (comparator != null) {
            Collections.sort(objects, comparator);
        }
        if (index != null) {
            int match = 0;
            for (int i = 0; i < objects.size() && match < start; i++) {
                if (index.match(objects.get(i))) {
                    match++;
                    realStart = i + 1;
                }
            }
        }
        List<T> ts = new ArrayList<T>(row);
        for (int i = realStart; i < objects.size() && ts.size() < row; i++) {
            T t = objects.get(i);
            if (index == null || index.match(t)) {
                ts.add(t);
            }
        }
        return ts;
    }

    public List<String> loadIds() {
        List<String> ids = new ArrayList<>();
        String[] list = new File(root).list();
        if (list != null) {
            Collections.addAll(ids, list);
        }
        return ids;
    }

    public List<File> listFile(){
        List<File> files = new ArrayList<>();
        File[] list = new File(root).listFiles();
        if (list != null) {
            Collections.addAll(files, list);
        }
        return files;
    }

    public List<T> removeExpire(long deathTime) {
        List<T> deleted = new ArrayList<T>();
        try {
            File[] files = new File(root).listFiles();
            if (files != null) {
                for (File file : files) {
                    BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    if (System.currentTimeMillis() - attr.creationTime().toMillis() > deathTime) {
                        T e = loadEntry(file);
                        deleted.add(e);
                        if (e instanceof IDModel) {
                            cache.remove(((IDModel) e).getId());
                        }
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            //Do nothing
            e.printStackTrace();
        }
        return deleted;
    }

    private synchronized void saveObject(T object, File entry) throws IOException {
        if(object instanceof IDModel && ((IDModel) object).isDelete()){
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(entry))) {
            oos.writeObject(object);
            oos.flush();
        }
    }

    private String getName(String id) {
        return id;
    }

    private T load(String id) {
        File entry = buildFile(id);
        return loadEntry(entry);
    }

    private synchronized T loadEntry(File entry) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(entry))) {
            Object o = ois.readObject();
            return table.cast(o);
        } catch(InvalidClassException e){
            e.printStackTrace();
            entry.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private File buildFile(String id) {
        return new File(root, id);
    }
}
