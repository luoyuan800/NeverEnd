package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.FloatValueEffect;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class MeetRateEffect implements FloatValueEffect {
    private float meetRate;

    public float getMeetRate() {
        return meetRate;
    }

    public void setMeetRate(float meetRate) {
        this.meetRate = meetRate;
    }

    @Override
    public float getValue() {
        return getMeetRate();
    }

    @Override
    public void setValue(float value) {
        setMeetRate(value);
    }
}
