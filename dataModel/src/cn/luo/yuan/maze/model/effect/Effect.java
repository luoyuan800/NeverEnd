package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.NameObject;
import sun.rmi.log.LogHandler;

import java.io.Serializable;

/**
 * Created by luoyuan on 2017/3/18.
 */
public abstract class Effect implements Serializable, NameObject,Cloneable {
    public abstract boolean isEnable();
    public abstract void setTag(String tag);
    public abstract String getTag();
    public boolean isReject(Class<? extends Effect> effectClass){
        return false;
    }

    public abstract void setEnable(boolean enable);

    @Override
    public String getDisplayName() {
        return "<font color='" + (isEnable()? Data.ENABLE_COLOR : Data.DISABLE_COLOR) + "'>" + toString() + "</font>";
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
    public abstract Number getValue();

    public Effect covertToOriginal(){
        return this;
    }

    public abstract void setElementControl(boolean control);
    public abstract boolean isElementControl();

    public Effect clone(){
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}
