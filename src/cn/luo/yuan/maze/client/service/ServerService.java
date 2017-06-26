package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.model.ServerData;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.RestConnection;

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

    public ServerData queryOnlineData(GameContext gameContext) {
        return null;
    }

    public boolean uploadHero(ServerData uploaddData) {
        return false;
    }

    public String postSingleBattleMsg(GameContext context) {
        return null;
    }

    public String queryAwardString(GameContext gameContext) {
        return null;
    }

    public ServerData getBackHero(GameContext gameContext) {
        return null;
    }
}
