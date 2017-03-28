package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.SecureRAMReader;
import cn.luo.yuan.maze.utils.Version;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import static cn.luo.yuan.maze.utils.EffectHandler.*;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class Maze implements IDModel, Serializable{
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private String id;
    private byte[] maxLevel;
    private byte[] level;
    private SecureRAMReader ramReader;
    private float meetRate = 100f;
    private long streaking;//连胜次数
    private long step;//连续前进步数
    private Set<Effect> effects= new HashSet<>();

    public long getLevel(){
        return ramReader.decodeLong(level);
    }

    public long getMaxLevel(){
        return ramReader.decodeLong(maxLevel);
    }

    public void setLevel(long level){
        this.level = ramReader.encodeLong(level);
    }
    public void setMaxLevel(long maxLevel){
        this.level = ramReader.encodeLong(maxLevel);
    }

    public byte[] getEncodeMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(byte[] maxLevel) {
        this.maxLevel = maxLevel;
    }

    public byte[] getEncodeLevel() {
        return level;
    }

    public void setLevel(byte[] level) {
        this.level = level;
    }

    public SecureRAMReader getRamReader() {
        return ramReader;
    }

    public void setRamReader(SecureRAMReader ramReader) {
        this.ramReader = ramReader;
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

    public void setStreaking(long streaking) {
        this.streaking = streaking;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }
}
