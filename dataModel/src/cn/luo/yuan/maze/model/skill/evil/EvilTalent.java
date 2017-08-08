package cn.luo.yuan.maze.model.skill.evil;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/5/28.
 */
public class EvilTalent extends DefSkill implements UpgradeAble {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long level = 1;
    private EvilModel model = new EvilModel(this);

    @Override
    public String getName() {
        return "魔王天赋 X " + getLevel();
    }

    @Override
    public String getDisplayName() {
        return "魔本天性，吾欲入魔，谁人可挡。<br>" +
                "被攻击的时候有" + getRate() +
                "%概率将伤害转换为HP恢复。" +
                "不可与勇者、元素使技能同时激活";
    }

    @Override
    public boolean canMount(SkillParameter parameter) {
        return model.canMount(parameter);
    }

    public boolean canEnable(SkillParameter parameter){
        return model.canEnable(parameter);
    }

    @Override
    public boolean upgrade(SkillParameter parameter) {
        if (getRate() < 15) {
            setRate(getRate() + 1.8f);
            level++;
            return true;
        }
        return false;
    }

    @Override
    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    @Override
    public SkillResult invoke(SkillParameter parameter) {
        SkipThisTurn result = new SkipThisTurn();
        HarmAble monster = parameter.get("target");
        HarmAble hero = (HarmAble) parameter.getOwner();
        long harm = monster.getAtk() - hero.getDef();
        if (harm < 0) {
            harm = 1;
        }
        hero.setHp(hero.getHp() + harm);
        result.addMessage((monster instanceof NameObject ? ((NameObject) monster).getDisplayName() : "") + "攻击了" + (hero instanceof NameObject ? ((NameObject) hero).getDisplayName() : ""));
        result.addMessage((hero instanceof NameObject ? ((NameObject) hero).getDisplayName() : "") + "使用了技能" + getName() + "将" + StringUtils.formatNumber(harm, false) + "点伤害转化为生命");
        result.setSkip(true);
        return result;
    }

    @Override
    public void enable(SkillParameter parameter) {
        setEnable(true);
    }

    public boolean canUpgrade(SkillParameter parameter) {
        return isEnable() && getRate() < Data.RATE_MAX/4 && model.isUpgradePointEnough(parameter);
    }

    public String getSkillName(){
        return model.getSkillName();
    }

}
