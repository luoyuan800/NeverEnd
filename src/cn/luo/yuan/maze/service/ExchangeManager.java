package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.LogHelper;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
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

    public List<ExchangeObject> querySubmittedExchangeOfMine() {
        try {
            HttpURLConnection urlConnection = server.getHttpURLConnection("/query_my_exchange", RestConnection.POST);
            urlConnection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return (List<ExchangeObject>) server.connect(urlConnection);
        } catch (Exception e) {
            LogHelper.logException(e, "");
        }
        return null;
    }

    public <T> T getBackMyExchange(String id) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/get_back_exchange", RestConnection.POST);
            connection.addRequestProperty(Field.EXCHANGE_ID_FIELD, id);
            return (T) server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "");
        }
        return null;
    }

    public List<ExchangeObject> queryAvailableExchanges(int limitType) {
        List<ExchangeObject> eos = new ArrayList<>();
        try {
            HttpURLConnection connection;
            if (limitType == Field.PET_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection("/exchange_pet_list", RestConnection.POST);
                eos.addAll((List<ExchangeObject>) server.connect(connection));
            }
            if (limitType == Field.ACCESSORY_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection("/exchange_accessory_list", RestConnection.POST);
                eos.addAll((List<ExchangeObject>) server.connect(connection));
            }
            if (limitType == Field.GOODS_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection("/exchange_goods_list", RestConnection.POST);
                eos.addAll((List<ExchangeObject>) server.connect(connection));
            }

        } catch (Exception e) {
            LogHelper.logException(e, "");
        }
        return eos;
    }

    public boolean requestExchange(Serializable myObject, ExchangeObject targetExchange) {
        try {
            HttpURLConnection conn = server.getHttpURLConnection("/request_exchange", RestConnection.POST);
            conn.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            conn.addRequestProperty(Field.EXCHANGE_ID_FIELD, targetExchange.getId());
            server.connect(myObject,conn);
            if(conn.getHeaderField(Field.RESPONSE_CODE).equals(Field.STATE_SUCCESS)){
                return true;
            }
        }catch (Exception e){
            LogHelper.logException(e, "");
        }
        return false;
    }

}
