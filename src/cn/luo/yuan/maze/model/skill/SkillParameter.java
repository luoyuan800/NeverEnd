package cn.luo.yuan.maze.model.skill;

import android.util.ArrayMap;
import android.util.TypedValue;
import cn.luo.yuan.maze.model.Hero;

import java.util.Map;

/**
 * Created by gluo on 4/27/2017.
 */
public class SkillParameter {
    private Hero owner;
    private ArrayMap<String, Object> parameter;
    public SkillParameter(Hero owner){
        this.owner = owner;
        parameter = new ArrayMap<>();
    }
    public <T> T get(String key){
        try {
            Object value = parameter.get(key);
            return (T) value;
        }catch (Exception e){
            return null;
        }
    }

    public Object set(String key, Object value){
        return parameter.put(key, value);
    }

    public Hero getOwner() {
        return owner;
    }
}
