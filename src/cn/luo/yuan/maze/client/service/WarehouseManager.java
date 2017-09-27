package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.object.IDModel;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.RestConnection;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

/**
 * Created by gluo on 6/16/2017.
 */
public class WarehouseManager {
    private NeverEnd context;
    private RestConnection server;
    public WarehouseManager(NeverEnd context){
        server = new RestConnection(Field.SERVER_URL, context.getVersion(), Resource.getSingInfo());
        this.context = context;
    }

    public boolean store(IDModel obj){
        if(obj instanceof MountAble && ((MountAble)obj).isMounted()){
            return false;
        }
        Serializable serverObj = context.convertToServerObject((Serializable) obj);
        try {
            HttpURLConnection connection = server.getHttpURLConnection("store_warehouse",RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD,context.getHero().getId());
            if(server.connect(serverObj,connection).equals(Field.RESPONSE_RESULT_SUCCESS)){
                context.getDataManager().delete((Serializable) obj);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public <T> T retrieve(T t){
        if(t instanceof  IDModel) {
            try {
                HttpURLConnection connection = server.getHttpURLConnection("retrieve_back_warehouse", RestConnection.POST);
                connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
                connection.addRequestProperty(Field.WAREHOUSE_ID_FIELD, ((IDModel) t).getId());
                Object o = server.connect(connection);
                if(o instanceof Serializable) {
                    o = context.convertToServerObject((Serializable) o);
                }
                return (T) o;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Object> retrieveList(){
        try {
            HttpURLConnection connection = server.getHttpURLConnection("retrieve_warehouse", RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            Object o = server.connect(connection);
            return (List<Object>)o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

}
