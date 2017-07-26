package cn.luo.yuan.maze.model.effect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public abstract class LongValueEffect extends Effect {
    public abstract void setValue(long value);
    public abstract Long getValue();
}
