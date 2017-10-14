package cn.luo.yuan.serialize;

import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.persistence.DatabaseConnection;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.*;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class SQLObjectTable<T extends Serializable> implements ObjectTable<T> {
    private DatabaseConnection database;
    private Class<T> clazz;
    private Map<String, SoftReference<T>> cache = Collections.synchronizedMap(new HashMap<>());
    private ReferenceQueue<T> queue = new ReferenceQueue<>();

    public SQLObjectTable(Class<T> clazz, DatabaseConnection con) {
        this.database = con;
        this.clazz = clazz;
        createTableIfNotExisted();
    }

    @Override
    public String save(T object, String id) throws Exception {
        if (object instanceof IDModel) {
            ((IDModel) object).setId(id);
        }
        try (Connection con = database.getConnection()) {
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery("select count(*) from `" + clazz.getSimpleName() + "` where id = '" + id + "'");
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    rs.close();
                    stat.close();
                    return update(object, id, true);
                }
            }
            PreparedStatement ps = con.prepareStatement("insert into `" + clazz.getSimpleName() + "` (id, data, create, update) values (?,?,?,?)");
            ps.setString(1, id);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            ps.setBytes(2, bos.toByteArray());
            Date date = new Date(System.currentTimeMillis());
            ps.setDate(3, date);
            ps.setDate(4, date);
            ps.execute();
            oos.close();
            addToCache(object, id);
        }
        return id;
    }

    @Override
    public String update(T object, String id, boolean addToCache) throws Exception {
        try (Connection con = database.getConnection()) {
            PreparedStatement ps = con.prepareStatement("update ? set data = ?, update=? where id = ?");
            ps.setString(1, clazz.getSimpleName());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            ps.setBytes(2, bos.toByteArray());
            ps.setDate(3, new Date(System.currentTimeMillis()));
            ps.setString(4, id);
            ps.execute();
            oos.close();
            addToCache(object, id);
            return id;
        }
    }

    @Override
    public String save(T object) throws Exception {
        String id;
        if (object instanceof IDModel) {
            if (StringUtils.isEmpty(((IDModel) object).getId())) {
                ((IDModel) object).setId(UUID.randomUUID().toString());
            }
            id = ((IDModel) object).getId();
        } else {
            id = UUID.randomUUID().toString();
        }
        try {
            return save(object, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T loadObject(String id) {
        try (Connection con = database.getConnection()) {
            Statement sts = con.createStatement();
            ResultSet rs = sts.executeQuery("select data from `" + clazz.getSimpleName() + "` where id = '" + id + "'");
            if (rs.next()) {
                T o = buildObject(id, rs);
                if (o != null) return o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> loadAll() {
        ArrayList<T> list = new ArrayList<>();
        for (String id : loadIds()) {
            SoftReference<T> ref = cache.get(id);
            if (ref != null) {
                T t = ref.get();
                if (t == null) {
                    t = loadObject(id);
                    addToCache(t, id);
                }
                if (t != null) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    @Override
    public void clear() {
        try (Connection con = database.getConnection()) {
            Statement ta = con.createStatement();
            ta.execute("delete from '" + clazz.getSimpleName() + "'");
            ta.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        try (Connection con = database.getConnection()) {
            Statement sta = con.createStatement();
            sta.execute("delete from '" + clazz.getSimpleName() + "' where id = '" + id + "'");
            sta.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fuse() throws IOException {
        for (SoftReference<T> ref : new ArrayList<>(cache.values())) {
            if (ref != null) {
                T t = ref.get();
                if (t != null) {
                    if (t instanceof IDModel && !((IDModel) t).isDelete()) {
                        try {
                            update(t, ((IDModel) t).getId(), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        cache.clear();
    }

    @Override
    public int size() {
        try (Connection con = database.getConnection()) {
            Statement sta = con.createStatement();
            ResultSet rs = sta.executeQuery("select count(id) from '" + clazz.getSimpleName() + "'");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<T> loadLimit(int start, int row, Index<T> index, Comparator<T> comparator) throws IOException, ClassNotFoundException {
        List<T> all = loadAll();
        all.sort(comparator);
        ArrayList<T> result = new ArrayList<>(all.size());
        for(int i = start; i<all.size() && result.size()<row; i++){
            T t = all.get(i);
            if(t!=null && index.match(t)){
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public List<String> loadIds() {
        try (Connection con = database.getConnection()) {
            ArrayList<String> ids = new ArrayList<>();
            Statement sta = con.createStatement();
            ResultSet rs = sta.executeQuery("select id from '" + clazz.getSimpleName() + "'");
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
            return ids;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<T> removeExpire(long deathTime) {
        return null;
    }

    @Override
    public void run() {
        try {
            Reference<? extends T> poll = queue.poll();
            if (poll != null) {
                for (String id : cache.keySet()) {
                    SoftReference<T> reference = cache.get(id);
                    if (reference == null || reference.get() == null) {
                        cache.remove(id);
                    }
                }
            }
        } catch (Exception e) {
            //Do nothing
            e.printStackTrace();
        }
    }

    private void addToCache(T object, String id) {
        cache.put(id, new SoftReference<T>(object, queue));
    }

    private void createTableIfNotExisted() {
        try (Connection con = database.getConnection()) {
            Statement sta = con.createStatement();
            sta.execute("CREATE TABLE IF NOT EXISTS `" + clazz.getSimpleName() + "` ( `id` VARCHAR(100) NOT NULL , `update` TIMESTAMP, `create` TIMESTAMP, `data` BLOB, PRIMARY KEY (`id`))");
            sta.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private T buildObject(String id, ResultSet rs) throws SQLException {
        byte[] data = rs.getBytes(1);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            if (o != null) {
                return (T) o;
            }
        } catch (InvalidClassException e) {
            delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
