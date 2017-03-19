package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Date;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class HeroIndex {
    private int index;
    private long lastUpdated;
    private long created;
    private long maxLevel;
    private long level;
    private String name;
    private Element element;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(long maxLevel) {
        this.maxLevel = maxLevel;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return "<b>" + name + "</b>(" + element + ")"+
                "<br>" + Resource.getString(R.string.level) + StringUtils.formatNumber(level) + "/" + StringUtils.formatNumber(maxLevel)
                + "<br>" + Resource.getString(R.string.created) + new Date(created)
                + "<br>" + Resource.getString(R.string.last_update) + new Date(getLastUpdated());

    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
