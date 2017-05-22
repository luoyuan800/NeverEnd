package cn.luo.yuan.maze.server.persistence.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 11/28/2016.
 */
public class ObjectDB<T> {
    private Class<T> type;

    public ObjectDB(Class<T> type) {
        File root = new File(type.getName());
        this.type = type;
    }

    public synchronized String save(Serializable object, String id) {
        if (object.getClass().getName().equals(type.getName())) {
            try {
                String path = getName(id);
                new File(path).deleteOnExit();
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
                oos.writeObject(object);
                oos.flush();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public String save(Serializable object) {
        return save(object, UUID.randomUUID().toString());
    }

    public synchronized T loadObject(String id) {
        String name = getName(id);
        T o = load(name);
        if (o != null) return o;
        return null;
    }

    public List<T> loadAll() {
        List<T> list = new ArrayList<>();
        for (String id : new File(type.getName()).list()) {
            T object = load(getName(id));
            if (object != null) {
                list.add(object);
            }
        }
        return list;
    }

    public synchronized void clear() {
        for (String id : new File(type.getName()).list()) {
            new File(getName(id)).deleteOnExit();
        }

    }

    public void delete(String id) {
        new File(getName(id)).deleteOnExit();
    }

    private String getName(String id) {
        return type.getName() + "/" + id;
    }

    private T load(String name) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(name));
            Object o = ois.readObject();
            ois.close();
            return type.cast(o);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
