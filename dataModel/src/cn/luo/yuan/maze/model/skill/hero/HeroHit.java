package cn.luo.yuan.maze.model.skill.hero;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.skill.*;
import cn.luo.yuan.maze.model.skill.result.HarmResult;
import cn.luo.yuan.maze.model.skill.result.SkillResult;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.service.SkillHelper;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;


/**
 * Created by gluo on 4/27/2017.
 */
public class HeroHit extends AtkSkill implements UpgradeAble {
    private long minHarm = 50;
    private long maxHarm = 300;
    private long level = 1;
    private HeroModel model = new HeroModel(this);

    @Override
    public boolean canMount(SkillParameter parameter) {
        return model.canMount(parameter);
    }

    public boolean canEnable(SkillParameter parameter){
        return model.canEnable(parameter);
    }

    @Override
    public SkillResult invoke(SkillParameter parameter) {
        Random random = parameter.get("random");
        if(random == null){
            random = new Random(System.currentTimeMillis());
        }
        long harm = minHarm + random.nextLong(maxHarm - minHarm);

        HarmAble target = parameter.get(SkillParameter.TARGET);
        if(target!=null){
            harm -= target.getDef();
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
    public boolean canUpgrade(SkillParameter parameter) {
        return isEnable() && parameter.getOwner() instanceof Hero && minHarm + level > 0
                && maxHarm * level > 0 && isUpgradePointEnough(parameter);
    }



    @Override
    public boolean upgrade(SkillParameter parameter) {
        if(minHarm + level < 0 || maxHarm*level < 0){
            return false;
        }
        if(parameter.getOwner() instanceof Hero) {
            level++;
            minHarm += level;
            maxHarm *= level;
            if(getRate() < 25) {
                setRate(getRate() + 3.1f);
            }
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
        return "勇者之击 X " + getLevel();
    }

    public String getSkillName(){
        return model.getSkillName();
    }

    @Override
    public String getDisplayName() {
        return "勇者的基本技能，学会了才能踏上征途。<br>" +
                StringUtils.DecimalFormatRound(getRate(), 2) + "%概率释放<br>" +
                "造成额外的" + StringUtils.formatNumber(minHarm) + " - " +
                StringUtils.formatNumber(maxHarm) + "伤害" + "<br>" +
                "不可与魔王、元素使技能同时激活";
    }
}
