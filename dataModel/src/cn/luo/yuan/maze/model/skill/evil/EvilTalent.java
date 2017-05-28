package cn.luo.yuan.maze.model.skill.evil;

import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/5/28.
 */
public class EvilTalent extends DefSkill implements UpgradeAble {
    private long level;
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
        InfoControlInterface context = parameter.get("context");
        Skill skill = SkillFactory.geSkillByName("HeroHit",context.getDataManager());
        if(skill!=null && skill.isEnable()){
            return false;
        }else {
            return isEnable();
        }
    }

    @Override
    public boolean upgrade(SkillParameter parameter) {
        return false;
    }

    @Override
    public long getLevel() {
        return 0;
    }

    @Override
    public SkillResult invoke(SkillParameter parameter) {
        return null;
    }

    @Override
    public void enable(SkillParameter parameter) {

    }

    public void setLevel(long level) {
        this.level = level;
    }
}
