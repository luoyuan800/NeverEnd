package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

import static cn.luo.yuan.maze.Path.*;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class ServerService {
    private RestConnection server;

    public ServerService(NeverEnd context) {
        server = new RestConnection(Field.SERVER_URL, context.getVersion(), Resource.getSingInfo());
    }


    public ServerService(String version) {
        server = new RestConnection(Field.SERVER_URL, version,Resource.getSingInfo() );
    }

    public ServerService(String url, String version) {
        server = new RestConnection(url, version,Resource.getSingInfo() );
    }

    public ServerData queryOnlineHeroData(NeverEnd gameContext) throws IOException {
        HttpURLConnection connection = server.getHttpURLConnection(QUERY_HERO_DATA, RestConnection.POST);
        connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
        Object obj = server.connect(connection);
        if (obj instanceof ServerData) {
            return (ServerData) obj;
        } else {
            return null;
        }
    }

    public boolean uploadHero(ServerData uploaddData) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(SUBMIT_HERO, RestConnection.POST);
            return Field.RESPONSE_RESULT_SUCCESS.equals(server.connect(uploaddData, connection));
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->45");
        }
        return false;
    }

    public String postOnlineData(NeverEnd context) throws IOException {
        return postOnlineData(context.getHero().getId());
    }

    public String postOnlineRange(NeverEnd context) throws IOException {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(HERO_RANGE, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postOnlineRange");
            if (e instanceof IOException) {
                throw e;
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public String postOnlineData(String id) throws IOException {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(POOL_ONLINE_DATA_MSG, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, id);
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->postOnlineData");
            if (e instanceof IOException) {
                throw e;
            }
        }
        return StringUtils.EMPTY_STRING;
    }

    public String postSingleBattleMsg(NeverEnd context) {
        return postBattleMsg(context.getHero().getId(), 1);
    }

    public String postBattleMsg(String id, int count) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(POOL_BATTLE_MSG, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, id);
            connection.addRequestProperty(Field.COUNT, String.valueOf(count));
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->76");
        }
        return StringUtils.EMPTY_STRING;
    }

    public String queryAwardString(NeverEnd gameContext) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(QUERY_BATTLE_AWARD, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->88");
        }
        return StringUtils.EMPTY_STRING;
    }

    public ServerData getBackHero(NeverEnd gameContext) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(GET_BACK_HERO, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
            return (ServerData) server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->98");
        }
        return null;
    }

    public Object openOnlineGift(NeverEnd context) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(ONLINE_GIFT_OPEN, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return server.connect(connection);
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->98");
        }
        return null;
    }

    public String postOnlineGiftCount(NeverEnd context){
        try {
            HttpURLConnection connection = server.getHttpURLConnection(GET_GIFT_COUNT, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            return server.connect(connection).toString();
        } catch (Exception e) {
            LogHelper.logException(e, "ServiceService->98");
        }
        return null;
    }
}
