package cn.luo.yuan.maze.model.index;

import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.Race;

/**
 * Created by luoyuan on 2017/3/19.
 */
public class HeroIndex {
    private Integer index;
    private Long lastUpdated;
    private Long created;
    private Long maxLevel;
    private Long level;
    private String name;
    private Element element;
    private String id;
    private Race race;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(Long maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public int hashCode(){
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }
}
