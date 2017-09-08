package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Monster;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.real.RealTimeState;
import cn.luo.yuan.maze.model.real.action.AtkAction;
import cn.luo.yuan.maze.model.real.action.AtkSkillAction;
import cn.luo.yuan.maze.model.real.action.DefSkillAction;
import cn.luo.yuan.maze.model.real.action.RealTimeAction;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.service.real.RealTimeBattle;
import cn.luo.yuan.maze.service.real.RealTimeManager;

import java.util.UUID;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/4/2017.
 */
public class LocalRealTimeManager implements RealTimeManager{
    private NeverEnd context;
    private HarmAble target;
    private RealTimeBattle realTimeBattle;
    private int msgIndex = 0;

    public LocalRealTimeManager(NeverEnd context, HarmAble target) {
        this.context = context;
        this.target = target;
        realTimeBattle = new RealTimeBattle(context.getHero().clone(), target, 0, (target instanceof Monster ? ((Monster) target).getMaterial() : 0), context.getRandom());
        realTimeBattle.setTimeLimit(-1);
        realTimeBattle.setP1Head(context.getDataManager().loadConfig().getHead());
        realTimeBattle.setP2Head(target.getId());
        if(context.getHero().getPets().size() > 0){
            realTimeBattle.setP1Pet(context.getHero().getPets().getFirst().getIndex());
        }
        realTimeBattle.start();//Monster ready
    }

    public void ready() {
        realTimeBattle.start();//Hero ready
    }

    public synchronized void atkAction() {
        try {
            RealTimeAction action = new AtkAction(UUID.randomUUID().toString(), context.getHero().getId());
            realTimeBattle.action(action);
        }catch (Exception e){
            LogHelper.logException(e, "Atk action");
        }
    }

    public synchronized RealTimeState pollState() {
        try {
            RealTimeState state = realTimeBattle.pollState(msgIndex);
            HarmAble actioner = state.getActioner();
            if (actioner != null) {
                if (target.getId().equals(actioner.getId())) {
                    monsterAction();
                }
                state = realTimeBattle.pollState(msgIndex);
            }
            msgIndex += state.getMsg().size();
            return state;
        }catch (Exception e){
            LogHelper.logException(e, "Poll State");
        }
        return new RealTimeState();
    }

    public synchronized void useGoodsAction(Goods goods) {

    }

    public synchronized void useAtkSkillAction(AtkSkill skill) {
        if (!realTimeBattle.action(new AtkSkillAction(UUID.randomUUID().toString(), context.getHero().getId(), skill))) {
            context.showPopup("行动失败，请确认有足够的行动点数后重试");
        }
    }

    public synchronized void useDefSkillAction(DefSkill skill) {
        if (!realTimeBattle.action(new DefSkillAction(UUID.randomUUID().toString(), context.getHero().getId(), skill))) {
            context.showPopup("行动失败，请确认有足够的行动点数后重试");
        }
    }

    @Override
    public void quit() {

    }

    public synchronized void monsterAction(){
        realTimeBattle.action(new AtkAction(UUID.randomUUID().toString(), target.getId()));
    }

}
