package cn.luo.yuan.maze.model.skill.click;


import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2016/7/4.
 */
public class BaoZha extends ClickSkill {

    public BaoZha(){
        setDuration(5 * 60 * 1000);
    }

    @Override
    public int getImageResource() {
        if (isUsable()) {
            return R.drawable.baoza;
        } else {
            return R.drawable.baoza_d;
        }
    }

    @Override
    public void perform(Hero hero, HarmAble monster, InfoControlInterface context) {
        if (!hero.getPets().isEmpty()) {
            Pet pet = hero.getPets().iterator().next();
            hero.getPets().remove(pet);
            //pet.releasePet(hero, context);
            long harm = (long) (monster.getMaxHp() * 0.9);
            monster.setHp(monster.getHp() - harm);
            context.addMessage("使用技能" + getName() + "损失了宠物" + pet.getDisplayName() + "， 对" + (monster instanceof NameObject ? ((NameObject) monster).getDisplayName() : "") + "造成了" + harm + "点伤害。");
            /* GoodsType grill = GoodsType.Grill;
           if(monster.getHp() <= 0){
                grill.setCount(grill.getCount() + 2);
                context.addMessage("获得了两块烤肉");
            }else{
                grill.setCount(grill.getCount() + 1);
                context.addMessage("获得了一块烤肉");
            }
            grill.save();
            context.getHandler().sendEmptyMessage(152);*/
        }
    }

    @Override
    public String getName() {
        return "宠爆";
    }
}
