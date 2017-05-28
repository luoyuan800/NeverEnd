package cn.luo.yuan.maze.model.skill.click;

import cn.gavin.BaseObject;
import cn.gavin.Hero;
import cn.gavin.R;
import cn.gavin.activity.BaseContext;

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

    public void perform(Hero hero, BaseObject monster, BaseContext context){
        monster.addHp(1 - monster.getHp());
        context.addMessage("使用" + getName() + "将" + monster.getFormatName() + "的生命值调整为1");
    }
}
