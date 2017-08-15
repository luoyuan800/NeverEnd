package cn.luo.yuan.maze.persistence.serialize;

import android.content.Context;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.serialize.ObjectTable;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.File;
import java.io.IOException;
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
    private ObjectTable<T> db;
    private Class<T> clazz;
    private Context context;
    private int index;

    public SerializeLoader(Class<T> type, Context context, int heroIndex) {
        db = new ObjectTable<T>(type, context.getDir(String.valueOf(heroIndex), Context.MODE_PRIVATE));
        clazz = type;
        this.context = context;
        this.index = heroIndex;
    }

    public ObjectTable<T> getDb() {
        return db;
    }

    public T load(String id) {
        return db.loadObject(id);
    }

    public void update(T object) {
        try {
            if (object instanceof IDModel) {
                db.delete(((IDModel) object).getId());
                db.save(object, ((IDModel) object).getId());
            } else {
                db.save(object);
            }
        } catch (Exception e) {
            LogHelper.logException(e, "update");
        }
    }

    public String save(T object) {
        try {
            String id = db.save(object);
            if (object instanceof IDModel) {
                ((IDModel) object).setId(id);
            }
            return id;
        } catch (Exception e) {
            LogHelper.logException(e, "save");
        }
        return StringUtils.EMPTY_STRING;
    }

    public void save(T object, String id) {
        try {
            db.save(object, id);
        } catch (IOException e) {
            LogHelper.logException(e, "save : " + id);
        }
    }

    public void delete(String id) {
        db.delete(id);
    }

    public List<T> loadAll() {
        return db.loadAll();
    }

    public List<T> loadLimit(int start, int row, Index<T> index, Comparator<T> comparator) {
        if (row < 0) {
            row = db.size();
        }
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
            if (t!=null && (index == null || index.match(t))) {
                ts.add(t);
            }
        }
        return ts;
    }

    public void fuse() {
        try {
            db.fuse();
        } catch (IOException e) {
            LogHelper.logException(e, "fuse");
        }
    }

    public int size() {
        return db.size();
    }

    public List<String> getAllID(){
        return db.loadIds();
    }

}
