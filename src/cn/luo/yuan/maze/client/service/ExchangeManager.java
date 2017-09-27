package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.ExchangeObject;
import cn.luo.yuan.object.IDModel;
import cn.luo.yuan.maze.model.OwnedAble;
import cn.luo.yuan.maze.model.skill.MountAble;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static cn.luo.yuan.maze.Path.ACKNOWLEDGE_MY_EXCHANGE;
import static cn.luo.yuan.maze.Path.EXCHANGE_ACCESSORY_LIST;
import static cn.luo.yuan.maze.Path.EXCHANGE_GOODS_LIST;
import static cn.luo.yuan.maze.Path.EXCHANGE_PET_LIST;
import static cn.luo.yuan.maze.Path.GET_BACK_EXCHANGE;
import static cn.luo.yuan.maze.Path.QUERY_MY_EXCHANGE;
import static cn.luo.yuan.maze.Path.REQUEST_EXCHANGE;
import static cn.luo.yuan.maze.Path.SUBMIT_EXCHANGE;

/**
 * Created by gluo on 6/8/2017.
 */
public class ExchangeManager {
    private RestConnection server;
    private NeverEnd context;

    public ExchangeManager(NeverEnd context) {
        this.context = context;
        server = new RestConnection(Field.SERVER_URL, context.getVersion(), Resource.getSingInfo());
    }

    public boolean submitExchange(Serializable object, String limit, int expectType) {
        if(object instanceof MountAble && ((MountAble) object).isMounted()){
            return false;
        }
        if (object instanceof Accessory) {
            object = context.convertToServerObject(object);
        }
        if (object instanceof OwnedAble) {
            if (StringUtils.isEmpty(((OwnedAble) object).getOwnerId())) {
                ((OwnedAble) object).setOwnerId(context.getHero().getId());
                ((OwnedAble) object).setOwnerName(context.getHero().getName());
            }
            if (StringUtils.isEmpty(((OwnedAble) object).getKeeperId())) {
                ((OwnedAble) object).setKeeperId(context.getHero().getId());
                ((OwnedAble) object).setKeeperName(context.getHero().getName());
            }
        }
        try {
            HttpURLConnection connection = server.getHttpURLConnection(SUBMIT_EXCHANGE, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            connection.addRequestProperty(Field.EXPECT_TYPE, String.valueOf(expectType));

            connection.addRequestProperty(Field.LIMIT_STRING, URLEncoder.encode(limit, "utf-8"));
            String result = server.connect(object, connection).toString();
            if (Field.RESPONSE_RESULT_OK.equals(result)) {
                return true;
            }
        } catch (IOException e) {
            LogHelper.logException(e, "ExchanmgManager->submitExchange");
        }
        return false;
    }

    public List<ExchangeObject> querySubmittedExchangeOfMine() {
        try {
            HttpURLConnection urlConnection = server.getHttpURLConnection(QUERY_MY_EXCHANGE, RestConnection.POST);
            urlConnection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return (List<ExchangeObject>) server.connect(urlConnection);
        } catch (Exception e) {
            LogHelper.logException(e, "ExchangeManager->querySubmittedExchangeOfMine");
        }
        return null;
    }

    public <T> T unBox(ExchangeObject eo) {
        IDModel model = eo.getExchange();
        if (model instanceof Accessory) {
            context.covertAccessoryToLocal((Accessory) model);
        }
        if (model instanceof OwnedAble) {
            ((OwnedAble) model).setKeeperId(context.getHero().getId());
            ((OwnedAble) model).setKeeperName(context.getHero().getName());
        }
        return (T) model;
    }

    public Object getBackMyExchange(String id) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(GET_BACK_EXCHANGE, RestConnection.POST);
            connection.addRequestProperty(Field.ITEM_ID_FIELD, id);
            Object object = server.connect(connection);
            return object;
        } catch (Exception e) {
            LogHelper.logException(e, "ExchanmgManager->getBackMyExchange");
        }
        return null;
    }

    public List<ExchangeObject> queryAvailableExchanges(int limitType, String limitKey) {
        List<ExchangeObject> eos = new ArrayList<>();
        limitKey = limitKey == null ? StringUtils.EMPTY_STRING : limitKey;
        try {
            HttpURLConnection connection = null;
            if (limitType == Field.PET_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection(EXCHANGE_PET_LIST, RestConnection.POST);
            }
            if (limitType == Field.ACCESSORY_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection(EXCHANGE_ACCESSORY_LIST, RestConnection.POST);
            }
            if (limitType == Field.GOODS_TYPE || limitType == -1) {
                connection = server.getHttpURLConnection(EXCHANGE_GOODS_LIST, RestConnection.POST);
            }
            if (connection != null) {
                connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
                connection.addRequestProperty(Field.LIMIT_STRING, URLEncoder.encode(limitKey, "utf-8"));
                Object connect = server.connect(connection);
                if(connect instanceof List) {
                    eos.addAll((List<ExchangeObject>) connect);
                }

            }

        } catch (Exception e) {
            LogHelper.logException(e, "ExchanmgManager->queryAvailableExchanges");
        }
        return eos;
    }

    public boolean requestExchange(Serializable myObject, ExchangeObject targetExchange) {
        try {
            HttpURLConnection conn = server.getHttpURLConnection(REQUEST_EXCHANGE, RestConnection.POST);
            conn.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            conn.addRequestProperty(Field.ITEM_ID_FIELD, targetExchange.getId());
            String result = server.connect(context.convertToServerObject(myObject), conn).toString();
            if (Field.RESPONSE_RESULT_OK.equals(result)) {
                Object obj = targetExchange.getExchange();
                if (obj instanceof OwnedAble) {
                    ((OwnedAble) obj).setKeeperName(context.getHero().getName());
                    ((OwnedAble) obj).setKeeperId(context.getHero().getId());
                }
                return true;
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ExchanmgManager->requestExchange");
        }
        return false;
    }

    public IDModel queryMyAvaiableItemForExchange(int expectedType, String expectedKeyWord) {
        return null;
    }

    public <T> T acknowledge(ExchangeObject eo) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(ACKNOWLEDGE_MY_EXCHANGE, RestConnection.POST);
            connection.addRequestProperty(Field.ITEM_ID_FIELD, eo.getId());
            if (Field.RESPONSE_RESULT_OK.equals(server.connect(connection))) {
                return unBox(eo.getChanged());
            }
        } catch (Exception e) {
            LogHelper.logException(e, "ExchanmgManager->acknowledge");
        }
        return null;
    }
}
