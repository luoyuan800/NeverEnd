package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class MeetRateEffect extends cn.luo.yuan.maze.model.effect.original.MeetRateEffect {
    private float meetRate;

    public float getMeetRate() {
        return meetRate;
    }

    public void setMeetRate(float meetRate) {
        this.meetRate = meetRate;
    }

    public String toString(){
        return Resource.getString(R.string.meet_effect) + StringUtils.DecimalFormatRound(meetRate,2) + "%";
    }

    @Override
    public void setValue(float value) {
        setMeetRate(value);
    }

    @Override
    public Float getValue() {
        return getMeetRate();
    }
}
