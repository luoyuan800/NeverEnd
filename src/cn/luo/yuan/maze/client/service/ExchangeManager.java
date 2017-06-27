package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.model.OwnedAble;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.utils.StringUtils;

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
        server = new RestConnection(Field.SERVER_URL, context.getVersion());
    }

    public boolean submitExchange(Serializable object) {
        if(object instanceof Accessory){
            object = context.convertToServerObject(object);
        }
        if(object instanceof OwnedAble){
            if(StringUtils.isEmpty(((OwnedAble)object).getOwnerId())){
                ((OwnedAble)object).setOwnerId(context.getHero().getId());
                ((OwnedAble)object).setOwnerName(context.getHero().getName());
            }
        }
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

    public <T> T unBox(ExchangeObject eo){
        IDModel model = eo.getExchange();
        if(model instanceof Accessory){
            context.covertAccessoryToLocal((Accessory) model);
        }
        if(model instanceof OwnedAble){
            ((OwnedAble)model).setKeeperId(context.getHero().getId());
            ((OwnedAble)model).setKeeperName(context.getHero().getName());
        }
        return (T)model;
    }

    public Object getBackMyExchange(String id) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/get_back_exchange", RestConnection.POST);
            connection.addRequestProperty(Field.EXCHANGE_ID_FIELD, id);
            Object object = server.connect(connection);

            return object;
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

    public IDModel queryMyAvaiableItemForExchange(int expectedType, String expectedKeyWord) {
        return null;
    }

    public <T> T acknowledge(ExchangeObject eo) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/acknowledge_my_exchange", RestConnection.POST);
            connection.addRequestProperty(Field.EXCHANGE_ID_FIELD, eo.getId());
            if(Field.RESPONSE_RESULT_OK.equals(server.connect(connection))){
                unBox(eo);
            }
        } catch (Exception e) {
            LogHelper.logException(e, "");
        }
        return null;
    }
}
