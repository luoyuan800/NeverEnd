package cn.luo.yuan.maze.persistence.serialize;

import android.content.Context;
import cn.luo.yuan.maze.model.IDModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class SerializeLoader<T extends Serializable> {
    private ObjectDB<T> db;
    private Class<T> clazz;
    private Context context;

    public SerializeLoader(Class<T> type, Context context){
        db = new ObjectDB<T>(type, context);
        clazz = type;
        this.context = context;
    }

    public T load(String id){
        return db.loadObject(id);
    }

    public void update(T object){
        if(object instanceof IDModel) {
            db.save(object, ((IDModel) object).getId());
        }else{
            db.save(object);
        }
    }

    public String save(T object){
        String id = db.save(object);
        if(object instanceof IDModel){
            ((IDModel) object).setId(id);
        }
        return id;
    }

    public void save(T object, String id){
        db.save(object, id);
    }

    public void delete(String id){
        db.delete(id);
    }

    public List<T> loadAll(){
        return db.loadAll();
    }
}
