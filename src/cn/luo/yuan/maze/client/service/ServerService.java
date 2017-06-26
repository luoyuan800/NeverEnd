package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class ServerService {
    private RestConnection server;
    public ServerService(GameContext context){
        server = new RestConnection(Field.SERVER_URL, context.getVersion());
    }


    public ServerService(String version){
        server = new RestConnection(Field.SERVER_URL, version);
    }
    public ServerService(String url, String version){
        server = new RestConnection(url, version);
    }

    public ServerData queryOnlineHeroData(GameContext gameContext) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/query_hero_data", RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
            return (ServerData)server.connect(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean uploadHero(ServerData uploaddData) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/submit_hero", RestConnection.POST);
            return Field.RESPONSE_RESULT_SUCCESS.equals(server.connect(uploaddData, connection));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String postSingleBattleMsg(GameContext context) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/pool_battle_msg", RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            connection.addRequestProperty(Field.COUNT,"1");
            return server.connect(connection).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY_STRING;
    }

    public String queryAwardString(GameContext gameContext) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/query_battle_award", RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
            return server.connect(connection).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY_STRING;
    }

    public ServerData getBackHero(GameContext gameContext) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection("/get_back_hero", RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
            return (ServerData) server.connect(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
