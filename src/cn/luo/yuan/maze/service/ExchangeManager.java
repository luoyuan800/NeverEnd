package cn.luo.yuan.maze.service;

import android.content.Context;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.utils.Field;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by gluo on 6/8/2017.
 */
public class ExchangeManager {
    private RestConnection server;
    private GameContext context;

    public ExchangeManager(GameContext context) {
        this.context = context;
        server = new RestConnection(Field.SERVER_URL, getVersion());
    }

    public String getVersion() {
        try {
            String pkName = context.getContext().getPackageName();
            int versionCode = context.getContext().getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return versionCode + "";
        } catch (Exception e) {
            return "0";
        }
    }

    public boolean submitExchange(Serializable object) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/submit_exchange", RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            server.connect(object, connection);
            if (connection.getHeaderField(Field.RESPONSE_CODE).equals(Field.STATE_SUCCESS)) {
                context.getDataManager().delete(object);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ExchangeObject> querySubmittedExchangeOfMine(){
        return null;
    }

    public boolean getBackMyExchange(String id){
        return false;
    }

    public List<ExchangeObject> queryAvailableExchanges(int limitType){
        return null;
    }

    public boolean requestExchange(IDModel myObject, ExchangeObject targetExchange){
        return false;
    }

}
