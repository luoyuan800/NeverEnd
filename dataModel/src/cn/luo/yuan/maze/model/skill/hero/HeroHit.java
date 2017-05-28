package cn.luo.yuan.maze.model.skill.hero;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.HarmResult;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.model.skill.SkillResult;
import cn.luo.yuan.maze.model.skill.UpgradeAble;
import cn.luo.yuan.maze.utils.Random;


/**
 * Created by gluo on 4/27/2017.
 */
public class HeroHit extends AtkSkill implements UpgradeAble {
    private long minHarm = 50;
    private long maxHarm = 300;
    private long level = 0;
    @Override
    public boolean canMount(SkillParameter parameter) {
        return true;
    }

    @Override
    public SkillResult invoke(SkillParameter parameter) {
        Random random = parameter.get("random");
        if(random == null){
            random = new Random(System.currentTimeMillis());
        }
        long harm = minHarm + random.nextLong(maxHarm - minHarm);
        HarmAble target = parameter.get("target");
        if(target!=null){
            harm -= target.getAtk();
            if(harm <= 0){
                harm = minHarm;
            }
        }

        harm += parameter.getOwner() instanceof Hero ? SkillHelper.getSkillBaseHarm((Hero)parameter.getOwner(),random) : 0;
        HarmResult result = new HarmResult();
        result.setHarm(harm);
        return result;
    }

    @Override
    public void enable(SkillParameter parameter) {
        setEnable(true);
    }

    @Override
    public boolean canEnable(SkillParameter parameter) {
        if(parameter.getOwner() instanceof Hero) {
            return ((Hero) parameter.getOwner()).getPoint() > Data.SKILL_ENABLE_COST;
        }
        return false;
    }

    @Override
    public boolean canUpgrade(SkillParameter parameter) {
        if(parameter.getOwner() instanceof Hero) {
            return minHarm + level > 0 && maxHarm * level > 0 && ((Hero) parameter.getOwner()).getPoint() > level * Data.SKILL_ENABLE_COST;
        }
        return false;
    }

    @Override
    public boolean upgrade(SkillParameter parameter) {
        if(minHarm + level < 0 || maxHarm*level < 0){
            return false;
        }
        if(parameter.getOwner() instanceof Hero) {
            ((Hero) parameter.getOwner()).setPoint(((Hero) parameter.getOwner()).getPoint() - level * Data.SKILL_ENABLE_COST);
            level++;
            minHarm += level;
            maxHarm *= level;
            return true;
        }
        return false;
    }

    @Override
    public long getLevel() {
        return level;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }
}
