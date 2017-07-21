package cn.luo.yuan.maze.model.skill.click;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2017/7/9.
 */
public class Material extends ClickSkill {
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

    }

    @Override
    public String getName() {
        return "点金";
    }

    public Material(){
        setDuration(100);
    }
}
