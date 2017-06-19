package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;
import sun.rmi.log.LogHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Accessory implements Serializable, IDModel, NameObject, OwnedAble, Cloneable {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private int heroIndex;
    private String name;
    private String id;
    private String type;
    private EncodeLong level = new EncodeLong(0);
    private String color = Data.DEFAULT_QUALITY_COLOR;
    private boolean mounted;
    private List<Effect> effects = new ArrayList<>(5);
    private String desc;
    private String author;
    private long price;
    private Element element;
    private String ownerId = StringUtils.EMPTY_STRING;
    private String ownerName = StringUtils.EMPTY_STRING;
    private String keeperId = StringUtils.EMPTY_STRING;
    private String keeperName = StringUtils.EMPTY_STRING;

    public List<Effect> getEffects(){
        return effects;
    }

    public String getDisplayName(){
        return "<font color='" + color + "'>" + name + "</font>" + "(" + type + ") "  + element.getCn() + (level.getValue()>0?(" + " + level):"") + (isMounted() ? "√" : "");
    }

    public String toString(){
        return "<font color='" + color + "'>" + name + "</font>("  + element.getCn() + (level.getValue()>0?(") + " + level):")");
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
        return level.getValue();
    }

    public void setLevel(long level) {
        this.level.setValue(level);
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

    public void resetElementEffectEnable(){
        for(Effect effect : getEffects()){
            if(effect.isElementControl()) {
                effect.setEnable(false);
            }
        }
    }

    public void elementEffectEnable(){
        for(Effect effect : getEffects()){
            if(effect.isElementControl()) {
                effect.setEnable(true);
            }
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getKeeperId() {
        return keeperId;
    }

    public void setKeeperId(String keeperId) {
        this.keeperId = keeperId;
    }

    public String getKeeperName() {
        return keeperName;
    }

    public void setKeeperName(String keeperName) {
        this.keeperName = keeperName;
    }

    public Accessory clone(){
        try {
            Accessory a = (Accessory) super.clone();
            List<Effect> effects = new ArrayList<>(a.getEffects());
            a.getEffects().clear();
            for(Effect e : effects){
                a.getEffects().add(e.clone());
            }
            return a;
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }

    @Override
    public int getHeroIndex() {
        return heroIndex;
    }

    @Override
    public void setHeroIndex(int heroIndex) {
        this.heroIndex = heroIndex;
    }
}
