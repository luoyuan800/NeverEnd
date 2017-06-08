package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Accessory implements Serializable, IDModel, NameObject {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private String name;
    private String id;
    private String type;
    private long level;
    private String color = Data.DEFAULT_QUALITY_COLOR;
    private boolean mounted;
    private List<Effect> effects = new ArrayList<>(5);
    private String desc;
    private String author;
    private long price;
    private Element element;

    public List<Effect> getEffects(){
        return effects;
    }

    public String getDisplayName(){
        return "<font color='" + color + "'>" + name + "</font>" + (level>0?(" + " + level):"");
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

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getPrice() {
        return price;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void resetEffectEnable(){
        for(Effect effect : getEffects()){
            effect.setEnable(false);
        }
    }

    public void effectEnable(){
        for(Effect effect : getEffects()){
            effect.setEnable(true);
        }
    }
}
