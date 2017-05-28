package cn.luo.yuan.maze.model.skill.click;

import cn.gavin.BaseObject;
import cn.gavin.Hero;
import cn.gavin.R;
import cn.gavin.activity.BaseContext;
import cn.gavin.activity.MainGameActivity;

/**
 * Created by luoyuan on 2016/7/4.
 */
public class XvWu extends ClickSkill {
    private long time;
    private long start;
    private boolean effect = true;
    @Override
    public String getName() {
        return "虚无";
    }

    public int getImageResource(){
        if(isUsable()) {
            return R.drawable.xvwu;
        }else{
            return R.drawable.xvwu_d;
        }
    }

    @Override
    public void perform(Hero hero, BaseObject monster, BaseContext context) {
        Long material = hero.getMaterial();
        if(material <5000000){
            hero.addMaterial(-material);
            setTime((material / 5000000) * 60 * 1000);
            start = System.currentTimeMillis();
            effect = false;
        }
        hero.setXvWu(this);
        context.addMessage("激活" + getName());
    }

    public long perform(long harm, Hero hero){
        if(effect){
            if(hero.getMaterial() > harm/100){
                hero.addMaterial(-harm/100);
                if(MainGameActivity.context!=null){
                    MainGameActivity.context.addMessage("因为" + getName() + "效果，用锻造点抵消了伤害。");
                }
                return 0;
            }else{
                hero.setXvWu(null);
                MainGameActivity.context.addMessage(getName() + "的效果消失了。");
                return harm;
            }
        }else{
            if(System.currentTimeMillis() - start >= time || hero.getMaterial() >= 5000000){
                hero.setXvWu(null);
                MainGameActivity.context.addMessage(getName() + "的效果消失了。");
            }else{
                if(harm/1000 + hero.getMaterial() > 5000000)
                hero.addMaterial(5000000 - hero.getMaterial());
                if(MainGameActivity.context!=null){
                    MainGameActivity.context.addMessage("因为" + getName() + "效果，获得了锻造点。");
                }
            }
            return harm;
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
