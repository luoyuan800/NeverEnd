package cn.luo.yuan.maze.model.skill.click;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.service.InfoControlInterface;

import java.util.concurrent.TimeUnit;

/**
 * Created by luoyuan on 2017/7/9.
 */
public class Material extends ClickSkill {
    transient private ClickSkillCheckThread checkThread;
    @Override
    public int getImageResource() {
        if (isUsable()) {
            return R.drawable.xvwu;
        } else {
            return R.drawable.xvwu_d;
        }
    }

    @Override
    public void perform(Hero hero, HarmAble monster, InfoControlInterface context) {
        long value = 1 + EffectHandler.getEffectAdditionLongValue(EffectHandler.CLICK_MATERIAL, hero.getEffects(), hero);
        hero.setMaterial(hero.getMaterial() + value);
        if(checkThread == null){
            checkThread = new ClickSkillCheckThread(this, context, 45);
            context.getExecutor().scheduleAtFixedRate(checkThread, 10000, 10000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public String getName() {
        return "点金";
    }

    public Material(){
        setDuration(300);
    }
}
