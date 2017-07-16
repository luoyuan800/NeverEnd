package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.utils.Random;

/**
 * Created by luoyuan on 2017/7/16.
 */
public class BattleServiceBase {

     public static long getHarm(HarmAble atker, HarmAble defender, long minHarm, Random random, BattleMessageInterface battleMessage) {
        long atk = atker.getUpperAtk();
        atk = atk / 3;
        atk = atk * 2 + random.nextLong(atk);
        boolean isHit = atker.isHit(random);
        if (isHit) {
            if (atker instanceof NameObject)
                battleMessage.hit((NameObject) atker);
            atk *= 2;//暴击有效攻击力翻倍
        }
        boolean isParry = defender.isParry(random);
        long defend = defender.getUpperDef();
        defend = defend / 2;
        defend = defend + random.nextLong(defend);
        if (isParry) {
            battleMessage.parry((NameObject)defender);
            //格挡，生效防御力三倍
            defend *= 3;
        }
        long harm = atk - defend;
        if (harm <= 0) {
            harm = random.nextLong(minHarm);
        }
        harm = elementAffectHarm(atker.getElement(), defender.getElement(), harm);
        return harm;
    }

     public static long elementAffectHarm(Element atker, Element defer, long baseHarm) {
        if (atker.restriction(defer) || (defer == Element.NONE && atker != Element.NONE)) {
            baseHarm *= 1.5;
        } else if (defer.restriction(atker) || (atker == Element.NONE && defer != Element.NONE)) {
            baseHarm *= 0.5;
        }
        return baseHarm;
    }
}
