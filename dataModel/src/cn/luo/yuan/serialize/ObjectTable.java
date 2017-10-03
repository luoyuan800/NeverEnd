package cn.luo.yuan.serialize;

import cn.luo.yuan.maze.model.Index;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public interface ObjectTable<T extends Serializable> extends Runnable {
    String save(T object, String id) throws Exception;

    String update(T object, String id, boolean addToCache) throws Exception;

    String save(T object) throws Exception;

    T loadObject(String id);

    List<T> loadAll();

    void clear();

    void delete(String id);

    void fuse() throws IOException;

    void close();

    int size();

    List<T> loadLimit(int start, int row, Index<T> index, Comparator<T> comparator) throws IOException, ClassNotFoundException;

    List<String> loadIds();

    List<T> removeExpire(long deathTime);
}
