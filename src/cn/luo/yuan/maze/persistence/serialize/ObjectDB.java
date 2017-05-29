package cn.luo.yuan.maze.persistence.serialize;

import android.content.Context;
import android.util.ArrayMap;
import cn.luo.yuan.maze.model.IDModel;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 11/28/2016.
 */
public class ObjectDB<T extends Serializable> {
    private Context context;
    private Class<T> type;
    private ArrayMap<String, SoftReference<T>> cache;

    public ObjectDB(Class<T> type, Context context) {
        this.context = context;
        this.type = type;
        cache = new ArrayMap<>();
    }

    public synchronized String save(T object, String id) {
        String path = object.getClass().getName() + "@" + id;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(path, Context.MODE_PRIVATE));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache.put(id, new SoftReference<T>(object));
        return id;
    }

    public String save(T object) {
        return save(object, UUID.randomUUID().toString());
    }

    public synchronized T loadObject(String id) {
        T object = null;
        SoftReference<T> ref = cache.get(id);
        if(ref!=null){
            object = ref.get();
        }
        if(object == null) {
            String name = getName(id);
            object = load(name);
        }
        return object;
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
        cache.remove(id);
    }

    public void fuse() {
        for(SoftReference<T> ref : cache.values()){
            if(ref!=null && ref.get()!=null){
                if(ref.get() instanceof IDModel){
                    save(ref.get(),((IDModel) ref.get()).getId());
                }
            }
        }
    }

    private String getName(String id) {
        return type.getName() + "@" + id;
    }

    private T load(String name) {
        try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(name))){
            Object o = ois.readObject();
            ois.close();
            return type.cast(o);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
