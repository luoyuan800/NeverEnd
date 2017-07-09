package cn.luo.yuan.maze.persistence.serialize;

import android.content.Context;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;

import java.io.InvalidClassException;
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
    private int index;

    public ObjectDB<T> getDb(){
        return db;
    }

    public SerializeLoader(Class<T> type, Context context, int heroIndex) {
        db = new ObjectDB<T>(type, context);
        clazz = type;
        this.context = context;
        this.index = heroIndex;
    }

    public T load(String id) {
        try {
            return db.loadObject(id);
        } catch (InvalidClassException e) {
            db.delete(id);
            return null;
        }
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
        try {
            return db.loadAll();
        } catch (InvalidClassException e) {
            db.clear();
            return Collections.emptyList();
        }
    }

    public List<T> loadLimit(int start, int row, Index<T> index, Comparator<T> comparator) {
        if(row < 0){
            row = db.size();
        }
        int realStart = start;
        List<T> objects = loadAll();
        if(comparator!=null){
            Collections.sort(objects, comparator);
        }
        if(index!=null) {
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
            if(index == null || index.match(t)) {
                ts.add(t);
            }
        }
        return ts;
    }

    public void fuse() {
        db.fuse();
    }

    public int size() {
        return db.size();
    }
}
