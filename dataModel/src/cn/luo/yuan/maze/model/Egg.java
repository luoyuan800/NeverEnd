package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/6/29.
 */
public class Egg extends Pet {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public long step;

    public int getIndex(){
        return step > 0 ? 0 : super.getIndex();
    }
    public String getDisplayName(){
        return step > 0 ? "蛋" : super.getDisplayName();
    }
    public String getDisplayNameWithLevel(){
        return getDisplayName();
    }
    public String getName(){
        return step > 0 ? "蛋" : super.getName();
    }
    public String getType(){
        return  step > 0 ? "蛋" : super.getType();
    }
    public int getSex(){
        return step > 0 ? -1 : super.getSex();
    }

    @Override
    public long getMaxHp() {
        return step > 0 ? 0 : super.getMaxHp();
    }

    @Override
    public long getAtk() {
        return step  > 0 ? 0 : super.getAtk();
    }

    @Override
    public long getHp() {
        return step > 0 ? 0 : super.getHp();
    }

    @Override
    public long getDef() {
        return step > 0 ? 0 : super.getDef();
    }
}
