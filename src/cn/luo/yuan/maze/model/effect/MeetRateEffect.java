package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.EncodeFloat;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class MeetRateEffect extends cn.luo.yuan.maze.model.effect.original.MeetRateEffect {
    private EncodeFloat meetRate = new EncodeFloat(0f);

    public float getMeetRate() {
        return meetRate.getValue();
    }

    public void setMeetRate(float meetRate) {
        this.meetRate.setValue(meetRate);
    }

    public String toString() {
        return Resource.getString(R.string.meet_effect) + StringUtils.DecimalFormatRound(meetRate.getValue(), 2) + "%";
    }

    @Override
    public Float getValue() {
        return getMeetRate();
    }

    @Override
    public void setValue(float value) {
        setMeetRate(value);
    }

    public cn.luo.yuan.maze.model.effect.original.MeetRateEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.MeetRateEffect effect = new cn.luo.yuan.maze.model.effect.original.MeetRateEffect();
        effect.setMeetRate(getMeetRate());
        return effect;
    }
    public Effect clone(){
        return (Effect) super.clone();
    }
}
