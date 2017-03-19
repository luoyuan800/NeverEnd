package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.Version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Accessory implements Serializable, IDModel {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private String name;
    private String id;
    private String type;
    private boolean mounted;
    private List<Effect> effects = new ArrayList<>(5);
    private String desc;

    public List<Effect> getEffects(){
        return effects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int hashCode(){
        return (id + type).hashCode();
    }

    public boolean equals(Object other){
        if(other == this){
            return true;
        }
        if(other instanceof Accessory){
            return ((Accessory) other).getId().equals(id);
        }
        return false;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public boolean isMounted() {
        return mounted;
    }

    public void setMounted(boolean mounted) {
        this.mounted = mounted;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
