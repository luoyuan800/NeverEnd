package cn.luo.yuan.maze.persistence.serialize;

import android.content.Context;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class SerializeLoader<T extends Serializable> {
    public static final int DESC = 0, ASC = 1;
    private ObjectDB<T> db;
    private Class<T> clazz;
    private Context context;

    public SerializeLoader(Class<T> type, Context context) {
        db = new ObjectDB<T>(type, context);
        clazz = type;
        this.context = context;
    }

    public T load(String id) {
        return db.loadObject(id);
    }

    public void update(T object) {
        if (object instanceof IDModel) {
            db.save(object, ((IDModel) object).getId());
        } else {
            db.save(object);
        }
    }

    public String save(T object) {
        String id = db.save(object);
        if (object instanceof IDModel) {
            ((IDModel) object).setId(id);
        }
        return id;
    }

    public void save(T object, String id) {
        db.save(object, id);
    }

    public void delete(String id) {
        db.delete(id);
    }

    public List<T> loadAll() {
        return db.loadAll();
    }

    public List<T> loadLimit(int strat, int row, Index<T> index, Comparator<T> comparator) {
        int realStart = strat;
        List<T> objects = loadAll();
        if(comparator!=null){
            Collections.sort(objects, comparator);
        }
        if(index!=null) {
            int match = 0;
            for (int i = 0; i < objects.size() && match < strat; i++) {
                if (index.match(objects.get(i))) {
                    match++;
                    realStart = i;
                }
            }
        }
        List<T> ts = new ArrayList<T>(row);
        for (int i = realStart; i < objects.size() && ts.size() < row; i++) {
            ts.add(objects.get(i));
        }
        return ts;
    }

    public void fluse() {
        db.fuse();
    }

}
