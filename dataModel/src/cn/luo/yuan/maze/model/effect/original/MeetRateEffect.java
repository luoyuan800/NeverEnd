package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class MeetRateEffect implements FloatValueEffect{
    private float meetRate;
    private boolean enable = false;

    private boolean elementControl;
    @Override
    public boolean isElementControl() {
        return elementControl;
    }

    @Override
    public void setElementControl(boolean elementControl) {
        this.elementControl = elementControl;
    }
    public MeetRateEffect() {
    }

    public float getMeetRate() {
        return meetRate;
    }

    public void setMeetRate(float meetRate) {
        this.meetRate = meetRate;
    }

    @Override
    public Float getValue() {
        return getMeetRate();
    }

    @Override
    public void setValue(float value) {
        setMeetRate(value);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public Effect clone(){
        try {
            return (Effect) super.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }
}
