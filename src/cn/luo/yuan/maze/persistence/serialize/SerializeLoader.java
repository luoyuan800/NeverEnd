package cn.luo.yuan.maze.persistence.serialize;

import cn.luo.yuan.maze.model.IDModel;

import java.io.Serializable;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class SerializeLoader<T extends Serializable> {
    private static String sdPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/neverend/data";
    private ObjectDB db;
    private Class<T> clazz;
    public SerializeLoader(Class<T> type){
        db = new ObjectDB(sdPath + "/" + type.getSimpleName());
    }

    public T load(String id){
        return db.loadObject(clazz, id);
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

    public void delete(String id){
        db.delete(clazz.getSimpleName(), id);
    }
}
