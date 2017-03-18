package cn.luo.yuan.maze.model;

import android.app.Activity;
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
    private List<Effect> effects = new ArrayList<>(5);

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
        return id.hashCode();
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
}
