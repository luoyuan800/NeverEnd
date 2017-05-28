package cn.luo.yuan.maze.model.skill.click;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Version;

import java.io.Serializable;

/**
 * Created by luoyuan on 2016/7/3.
 */
public abstract class ClickSkill implements Serializable {
    private static final long serialVersionUID = Version.SERVER_VERSION;

    private long click;
    private long lastUseTime;
    private int index;
    private long duration;

    public void use(Hero hero, HarmAble monster, InfoControlInterface context) {
            click++;
            this.lastUseTime = System.currentTimeMillis();
            perform(hero, monster, context);
    }

    public long getLastDuration() {
        return System.currentTimeMillis() - lastUseTime;
    }

    public long getNextTime() {
        return duration - getLastDuration();
    }

    public boolean isUsable() {
        return getNextTime() <= 0;
    }

    public long getClick() {
        return click;
    }

    public void setClick(long click) {
        this.click = click;
    }

    public long getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(long lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public abstract int getImageResource();

    public abstract void perform(Hero hero, HarmAble monster, InfoControlInterface context);



    public abstract String getName();

}
