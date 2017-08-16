package cn.luo.yuan.maze.model.skill;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Parameter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Created by gluo on 4/27/2017.
 */
public class SkillParameter extends Parameter {
    public static final String TARGET = "target";;
    public static final String CONTEXT = "context";;
    public static final String COUNT = "count";
    private SkillAbleObject owner;
    private HashMap<String, Object> parameter;
    public static final String RANDOM = "random";
    public static final String MESSAGE = "message";
    public static final String MINHARM = "minHarm";
    @Nullable
    public static final String ATKER = "atker";
    public static final String DEFENDER = "defender";

    public SkillParameter(SkillAbleObject owner) {
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

    public SkillAbleObject getOwner() {
        return owner;
    }
}
