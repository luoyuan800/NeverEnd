package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.NameObject;

import java.io.Serializable;

/**
 * Created by luoyuan on 2017/3/18.
 */
public interface Effect extends Serializable, NameObject,Cloneable {
    boolean isEnable();

    Effect clone();

    void setEnable(boolean enable);

    @Override
    default String getDisplayName() {
        return "<font color='" + (isEnable()? Data.ENABLE_COLOR : Data.DISABLE_COLOR) + "'>" + toString() + "</font>";
    }

    @Override
    default String getName() {
        return getClass().getSimpleName();
    }
    Number getValue();

    default Effect covertToOriginal(){
        return null;
    }

    void setElementControl(boolean control);
    boolean isElementControl();
}
