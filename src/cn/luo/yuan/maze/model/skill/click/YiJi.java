package cn.luo.yuan.maze.model.skill.click;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.service.InfoControlInterface;

/**
 * Created by luoyuan on 2016/7/4.
 */
public class YiJi extends ClickSkill {

    @Override
    public int getImageResource() {
        if(isUsable()) {
            return R.drawable.yiji;
        }else{
            return R.drawable.yiji_d;
        }
    }

    @Override
    public String getName() {
        return "一击";
    }

    public void perform(Hero hero, HarmAble monster, InfoControlInterface context){
        monster.setHp(1);
        context.addMessage("使用" + getName() + "将" + (monster instanceof NameObject ? ((NameObject) monster).getDisplayName() : "") + "的生命值调整为1");
    }
}
