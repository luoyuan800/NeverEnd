package cn.luo.yuan.maze.persistence.serialize;

import android.content.Context;
import android.util.ArrayMap;
import cn.luo.yuan.object.IDModel;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by gluo on 11/28/2016.
 */
public class ObjectDB<T extends Serializable> implements Runnable {
    private Context context;
    private Class<T> type;
    private ArrayMap<String, SoftReference<T>> cache;
    private ReferenceQueue<T> queue;

    public ObjectDB(Class<T> type, Context context) {
        this.context = context;
        this.type = type;
        cache = new ArrayMap<>();
        queue = new ReferenceQueue<T>();
    }

    public synchronized String save(T object, String id) {
        if(object instanceof IDModel){
            if(((IDModel) object).isDelete()){
                return id;
            }
        }
        String path = getName(id);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(context.openFileOutput(path, Context.MODE_PRIVATE));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            LogHelper.logException(e,"ObjectDb->save{" + object + ", " + id + "}");
        }
        cache.put(id, new SoftReference<T>(object));
        return id;
    }

    public String save(T object) {
        if(object instanceof IDModel){
            if(StringUtils.isEmpty(((IDModel) object).getId())){
                ((IDModel) object).setId(UUID.randomUUID().toString());
            }
            if(((IDModel) object).isDelete()){
                return ((IDModel) object).getId();
            }
        }
        return save(object, object instanceof IDModel? ((IDModel) object).getId() : UUID.randomUUID().toString());
    }

    public synchronized T loadObject(String id) throws InvalidClassException {
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

    public List<T> loadAll() throws InvalidClassException {
        List<T> list = new ArrayList<>();
        for(String file : context.fileList()){
            if(file.startsWith(prefix())){
                String id = retrieveId(file);
                if(StringUtils.isNotEmpty(id)){
                    SoftReference<T> ref = cache.get(id);
                    if(ref!=null){
                        T o = ref.get();
                        if(o!=null) {
                            list.add(o);
                            continue;
                        }
                    }
                    T o = load(file);
                    cache.put(id, new SoftReference<T>(o, queue));
                    list.add(o);
                }
            }
        }
        return list;
    }

    private String retrieveId(String fileName){
        return fileName.replaceFirst(prefix(), "");
    }

    @NotNull
    private String prefix() {
        return type.getName() + "@";
    }

    public int size(){
        int size = 0;
        for(String file : context.fileList()){
            if(file.startsWith(prefix())){
                size ++;
            }
        }
        return size;
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
            T t = ref.get();
            if(ref!=null && t !=null){
                saveInten(t);
            }
        }
    }

    private String getName(String id) {
        return prefix() + id;
    }

    private T load(String name) throws InvalidClassException {
        try (ObjectInputStream ois = new ObjectInputStream(context.openFileInput(name))){
            Object o = ois.readObject();
            ois.close();
            return type.cast(o);
        } catch (InvalidClassException ice){
            throw ice;
        }catch (IOException | ClassNotFoundException e) {
            //LogHelper.logException(e,"Sqlite->load{" + name + "}");
        }
        return null;
    }

    public void run(){
        try {
            Reference<? extends T> ref = null;
            while ((ref = queue.poll()) != null) {
                T t = ref.get();
                saveInten(t);
            }
        }catch (Exception e){
            LogHelper.logException(e, "DB-clear");
        }
    }

    private void saveInten(T t) {
        if(t instanceof IDModel && !((IDModel) t).isDelete()){
            save(t,((IDModel) t).getId());
        } else{
            if(t instanceof IDModel && ((IDModel) t).isDelete()){
                delete(((IDModel) t).getId());
            }
        }
    }

}
