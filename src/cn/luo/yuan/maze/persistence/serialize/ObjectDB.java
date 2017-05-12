package cn.luo.yuan.maze.persistence.serialize;

import android.content.Context;

import java.io.File;
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
    private Context context;
    private Class<T> type;

    public ObjectDB(Class<T> type, Context context) {
        this.context = context;
        this.type = type;
    }

    public synchronized String save(Serializable object, String id) {
        try {
            String path = object.getClass().getName() + "@" + id;
            context.deleteFile(path);
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(path, Context.MODE_PRIVATE));
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

    public synchronized T loadObject(String id) {
        String name = getName(id);
        T o = load(name);
        if (o != null) return o;
        return null;
    }

    public List<T> loadAll(){
        List<T> list = new ArrayList<>();
        for(String file : context.fileList()){
            if(file.startsWith(type.getName() + "@")){
                T object = load(file);
                if(object!=null){
                    list.add(object);
                }
            }
        }
        return list;
    }

    public synchronized void clear() {
        for (String file : context.fileList()) {
            if (file.startsWith(type.getName())) {
                context.deleteFile(file);
            }
        }

    }

    public void delete(String id) {
        context.deleteFile(getName(id));
    }

    private String getName(String id) {
        return type.getName() + "@" + id;
    }

    private T load(String name) {
        try {
            ObjectInputStream ois = new ObjectInputStream(context.openFileInput(name));
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
