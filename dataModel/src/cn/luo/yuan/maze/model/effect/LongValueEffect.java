package cn.luo.yuan.maze.model.effect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public interface LongValueEffect extends Effect {
    void setValue(long value);
    Long getValue();
}
