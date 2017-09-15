package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.Path;
import cn.luo.yuan.maze.client.display.activity.PalaceActivity;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.real.RealState;
import cn.luo.yuan.maze.model.real.RealTimeState;
import cn.luo.yuan.maze.model.real.action.AtkAction;
import cn.luo.yuan.maze.model.real.action.AtkSkillAction;
import cn.luo.yuan.maze.model.real.action.DefSkillAction;
import cn.luo.yuan.maze.model.real.action.RealTimeAction;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.service.real.RealTimeManager;
import cn.luo.yuan.maze.utils.Field;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/12/2017.
 */
public class RemoteRealTimeManager implements RealTimeManager {
    private NeverEnd context;
    private RestConnection server;
    private List<String> actionId = new ArrayList<>();
    private List<RealTimeAction> actions = new ArrayList<>();
    private int msgIndex = 0;
    private String id;
    private RealState currentState;
    public RemoteRealTimeManager(RestConnection server, NeverEnd context){
        this.server =server;
        this.context = context;
    }
    @Override
    public void ready() {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.REAL_BATTLE_READY, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            server.connect(connection);
        } catch (IOException e) {
            LogHelper.logException(e, "Ready battle.");
        }
    }

    @Override
    public void atkAction() {
        AtkAction atkAction = new AtkAction(UUID.randomUUID().toString(), context.getHero().getId());
        actionWithRetry(atkAction, 3);
    }

    public void actionWithRetry(RealTimeAction atkAction, int retryTime) {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.REAL_BATTLE_ACTION, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            Object o = server.connect(atkAction, connection);
            if(o.toString().equals(Field.RESPONSE_RESULT_OK)){
                actionId.add(atkAction.getId());
                actions.add(atkAction);
            }
        } catch (IOException e) {
            LogHelper.logException(e, "Remote action failed! " + (retryTime > 0? "Will retry!":"Will not retry!"));
            if(retryTime > 0){
                actionWithRetry(atkAction, retryTime - 1);
            }
        }
    }

    @Override
    public RealState pollState() {
        try {
            HttpURLConnection con = server.getHttpURLConnection(Path.POLL_REAL_BATTLE_STATE, RestConnection.POST);
            con.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            con.addRequestProperty(Field.INDEX, String.valueOf(msgIndex));
            con.addRequestProperty(Field.REAL_MSG_ID, getId());
            Object o = server.connect(currentState.newEmptyInstance(), con);
            if(o instanceof RealState){
                if(o instanceof RealTimeState) {
                    msgIndex += ((RealTimeState) o).getMsg().size();
                }
                currentState = (RealState) o;
                return (RealState) o;
            }
        } catch (IOException e) {
            LogHelper.logException(e, "Poll remote real state");
        }
        return null;
    }

    @Override
    public void useGoodsAction(Goods goods) {

    }

    @Override
    public void useAtkSkillAction(AtkSkill skill) {
        AtkSkillAction action = new AtkSkillAction(UUID.randomUUID().toString(),context.getHero().getId(), skill);
        actionWithRetry(action, 3);
    }

    @Override
    public void useDefSkillAction(DefSkill skill) {
        DefSkillAction action = new DefSkillAction(UUID.randomUUID().toString(), context.getHero().getId(), skill);
        actionWithRetry(action, 3);
    }

    @Override
    public void quit() {
        try {
            HttpURLConnection connection = server.getHttpURLConnection(Path.REAL_BATTLE_QUIT, RestConnection.POST);
            connection.addRequestProperty(Field.OWNER_ID_FIELD, context.getHero().getId());
            server.connect(connection);
        } catch (IOException e) {
            LogHelper.logException(e, "Quit remote battle");
        }
        if(context.getContext() instanceof PalaceActivity){
            ((PalaceActivity) context.getContext()).updateLevel();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
