package cn.luo.yuan.maze.client.service;

import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.real.RealTimeState;
import cn.luo.yuan.maze.model.real.action.AtkAction;
import cn.luo.yuan.maze.model.real.action.AtkSkillAction;
import cn.luo.yuan.maze.model.real.action.DefSkillAction;
import cn.luo.yuan.maze.model.real.action.RealTimeAction;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.service.real.RealTimeBattle;
import cn.luo.yuan.maze.service.real.RealTimeManager;
import cn.luo.yuan.maze.utils.Field;

import java.util.UUID;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/4/2017.
 */
public class LocalRealTimeManager implements RealTimeManager {
    private NeverEnd context;
    private HarmAble target;
    private RealTimeBattle realTimeBattle;
    private int msgIndex = 0;
    public LocalRealTimeManager(NeverEnd context, HarmAble target){
        this.context = context;
        this.target = target;
        realTimeBattle = new RealTimeBattle(context.getHero(),target, context.getRandom());
        realTimeBattle.start();//Monster ready
    }

    public void ready(){
        realTimeBattle.start();//Hero ready
    }

    public void atkAction(){
        RealTimeAction action = new AtkAction(UUID.randomUUID().toString(),context.getHero().getId());
        realTimeBattle.action(action);
    }

    public RealTimeState pollState(){
        RealTimeState state = realTimeBattle.pollState(msgIndex);
        msgIndex += state.getMsg().size();
        return state;
    }

    public void useGoodsAction(Goods goods){

    }

    public void useAtkSkillAction(AtkSkill skill){
        if(!realTimeBattle.action(new AtkSkillAction(UUID.randomUUID().toString(), context.getHero().getId(), skill))){
            context.showPopup("行动失败，请确认有足够的行动点数后重试");
        }
    }

    public void useDefSkillAction(DefSkill skill){
        if(!realTimeBattle.action(new DefSkillAction(UUID.randomUUID().toString(), context.getHero().getId(), skill))){
            context.showPopup("行动失败，请确认有足够的行动点数后重试");
        }
    }


}
