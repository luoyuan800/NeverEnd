package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.Field;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class MeetRateEffect extends cn.luo.yuan.maze.model.effect.original.MeetRateEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public float getMeetRate() {
        return getValue();
    }

    public void setMeetRate(float meetRate) {
        this.setValue(meetRate);
    }

    public cn.luo.yuan.maze.model.effect.original.MeetRateEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.MeetRateEffect effect = new cn.luo.yuan.maze.model.effect.original.MeetRateEffect();
        effect.setMeetRate(getMeetRate());
        return effect;
    }
}
