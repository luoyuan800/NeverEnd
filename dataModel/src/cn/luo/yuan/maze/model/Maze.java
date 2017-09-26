package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static cn.luo.yuan.maze.service.EffectHandler.MEET_RATE;
import static cn.luo.yuan.maze.service.EffectHandler.getEffectAdditionFloatValue;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class Maze implements IDModel, Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private boolean delete;
    private String id;
    private EncodeLong maxLevel = new EncodeLong(1);
    private EncodeLong level = new EncodeLong(1);
    private float meetRate = Data.MONSTER_MEET_RATE;
    private long streaking;//连胜次数
    private long die;//死亡次数
    private long step;//连续前进步数
    private Set<Effect> effects = new HashSet<>();

    @Override
    public boolean isDelete() {
        return delete;
    }

    public void markDelete() {
        delete = true;
    }

    public long getLevel() {
        return level.getValue();
    }

    public void setLevel(long level) {
        this.level.setValue(level);
    }

    public long getMaxLevel() {
        return maxLevel.getValue();
    }

    public void setMaxLevel(long maxLevel) {
        this.maxLevel.setValue(maxLevel);
    }


    public float getMeetRate() {
        return meetRate + getEffectAdditionFloatValue(MEET_RATE, effects);
    }

    public void setMeetRate(float meetRate) {
        this.meetRate = meetRate;
    }

    public long getStreaking() {
        return streaking;
    }

    public void setStreaking(long streaking) {
        this.streaking = streaking;
    }

    public Set<Effect> getEffects() {
        return effects;
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public long getDie() {
        return die;
    }

    public void setDie(long die) {
        this.die = die;
    }
}
