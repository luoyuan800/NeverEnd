package cn.luo.yuan.serialize;

import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.Index;
import cn.luo.yuan.maze.persistence.DatabaseConnection;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.*;
import java.sql.*;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SQLObjectTable<T extends Serializable> implements ObjectTable<T> {
    private DatabaseConnection database;
    private Class<T> clazz;
    public SQLObjectTable(Class<T> clazz, DatabaseConnection con){
        this.database = con;
        this.clazz = clazz;
        createTableIfNotExisted();
    }

    private void createTableIfNotExisted(){
        try (Connection con = database.getConnection()){
            Statement sta = con.createStatement();
            sta.execute("CREATE TABLE IF NOT EXISTS `" + clazz.getSimpleName() + "` ( `id` VARCHAR(100) NOT NULL , `update` TIMESTAMP, `create` TIMESTAMP, `data` BLOB, PRIMARY KEY (`id`))");
            sta.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public String save(T object, String id) throws Exception {
        if(object instanceof IDModel){
            ((IDModel) object).setId(id);
        }
        try(Connection con = database.getConnection()){
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery("select count(*) from `" + clazz.getSimpleName() + "` where id = '" + id + "'");
            if(rs.next()){
                if(rs.getInt(1) > 0){
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public String update(T object, String id, boolean addToCache) throws Exception {
        return null;
    }

    @Override
    public String save(T object) throws Exception {
        String id;
        if(object instanceof IDModel){
            if(StringUtils.isEmpty(((IDModel) object).getId())){
                ((IDModel) object).setId(UUID.randomUUID().toString());
            }
            id = ((IDModel) object).getId();
        }else{
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
        return null;
    }

    @Override
    public List<T> loadAll() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void fuse() throws IOException {

    }

    @Override
    public void close() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<T> loadLimit(int start, int row, Index<T> index, Comparator<T> comparator) throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public List<String> loadIds() {
        return null;
    }

    @Override
    public List<T> removeExpire(long deathTime) {
        return null;
    }

    @Override
    public void run() {

    }
}
