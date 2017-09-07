package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.EncodeFloat;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class MeetRateEffect extends cn.luo.yuan.maze.model.effect.original.MeetRateEffect {

    public float getMeetRate() {
        return getValue();
    }

    public void setMeetRate(float meetRate) {
        this.setValue(meetRate);
    }

    public String toString() {
        return Resource.getString(R.string.meet_effect) + StringUtils.DecimalFormatRound(getValue(), 2) + "%";
    }

    public cn.luo.yuan.maze.model.effect.original.MeetRateEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.MeetRateEffect effect = new cn.luo.yuan.maze.model.effect.original.MeetRateEffect();
        effect.setMeetRate(getMeetRate());
        return effect;
    }
}
