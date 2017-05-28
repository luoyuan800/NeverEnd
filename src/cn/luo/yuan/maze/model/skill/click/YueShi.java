package cn.luo.yuan.maze.model.skill.click;

import cn.gavin.BaseObject;
import cn.gavin.Hero;
import cn.gavin.R;
import cn.gavin.activity.BaseContext;
import cn.gavin.log.LogHelper;
import cn.gavin.monster.Monster;
import cn.gavin.monster.Race;
import cn.gavin.story.NPC;

/**
 * Created by luoyuan on 2016/7/4.
 */
public class YueShi extends ClickSkill {

    public String getName() {
        return "月蚀";
    }

    public int getImageResource() {
        if(isUsable()) {
            return R.drawable.yueshi;
        }else{
            return R.drawable.yueshi_d;
        }
    }

    @Override
    public void perform(final Hero hero, BaseObject monster, BaseContext context) {
        context.addMessage("使用了技能" + getName());
        if (monster instanceof Monster) {
            if (((Monster) monster).getRace() == Race.Orger.ordinal()) {
                hero.setOnSkill(true);
                context.addMessage(hero.getFormatName() + "攻击力翻倍");
                hero.setSkillAdditionAtk(hero.getUpperAtk());
                context.addMessage(hero.getFormatName() + "生命值减半");
                hero.setSkillAdditionHp(-hero.getUpperHp() / 2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long sleep = hero.getRandom().nextInt(2 * 60 * 1000);
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            LogHelper.logException(e, false);
                        }
                        hero.setOnSkill(false);
                    }
                }).start();
                return;
            }
            ((Monster) monster).setAtk(((Monster) monster).getAtk()/2);
        }else{
            if(monster instanceof NPC){
                ((NPC) monster).setAttackValue(monster.getAttackValue()/2);
            }
        }
        monster.addHp(monster.getHp() / 2);
        context.addMessage(monster.getFormatName() + "攻击力、生命值减半。");
    }
}
