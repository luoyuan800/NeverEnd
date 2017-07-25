package cn.luo.yuan.maze.service;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/25/2017.
 */
public class RangePropertiesHelper {
    public static void addStr(long strPoint, InfoControlInterface context) {
        context.getHero().setStr(context.getHero().getStr() + strPoint);
        long hpG = context.getHero().getHpGrow() * strPoint;
        context.getHero().setMaxHp(context.getHero().getMaxHp() + hpG);
        context.getHero().setHp(context.getHero().getHp() + hpG);
        context.getHero().setAtk(context.getHero().getAtk() + context.getHero().getAtkGrow() * strPoint);
    }

    public static void addAgi(long agiPoint, InfoControlInterface context) {
        context.getHero().setAgi(context.getHero().getAgi() + agiPoint);
        context.getHero().setDef(context.getHero().getDef() + context.getHero().getDefGrow() * agiPoint);
    }

}
