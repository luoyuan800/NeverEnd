package cn.luo.yuan.maze.model;

/**
 * Created by luoyuan on 2017/6/29.
 */
public class Egg extends Pet {
    public long step;

    public int getIndex(){
        return step > 0 ? 0 : super.getIndex();
    }
    public String getDisplayName(){
        return "蛋";
    }
    public String getName(){
        return "蛋";
    }
    public String getType(){
        return  step > 0 ? "蛋" : super.getType();
    }
    public int getSex(){
        return step > 0 ? -1 : super.getSex();
    }
}
