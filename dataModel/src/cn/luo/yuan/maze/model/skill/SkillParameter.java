package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Hero;

import java.util.HashMap;

/**
 * Created by gluo on 4/27/2017.
 */
public class SkillParameter {
    private Hero owner;
    private HashMap<String, Object> parameter;

    public SkillParameter(Hero owner) {
        this.owner = owner;
        parameter = new HashMap<>();
    }

    public <T> T get(String key) {
        try {
            Object value = parameter.get(key);
            return (T) value;
        } catch (Exception e) {
            return null;
        }
    }

    public Object set(String key, Object value) {
        return parameter.put(key, value);
    }

    public Hero getOwner() {
        return owner;
    }
}
