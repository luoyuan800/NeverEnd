package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;

/**
 * Created by gluo on 4/1/2017.
 */
public class Monster implements HarmAble, SilentAbleObject, NameObject, SkillAbleObject,Cloneable {
    private String firstName;
    private String secondName;
    private String type;
    private long atk;
    private long hp;
    private long material;
    private long maxHP;
    private float hitRate = 100.0f;
    private Element element;
    private float silent;
    private float eggRate = 0;
    private float petRate = 0;
    private int index;
    private long def;
    private String color;
    private int sex = -1;
    private Race race;
    private Skill skill;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAtk() {
        return atk;
    }

    public void setAtk(long atk) {
        this.atk = atk;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public long getMaterial() {
        return material;
    }

    public void setMaterial(long material) {
        this.material = material;
    }

    public long getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(long maxHP) {
        this.maxHP = maxHP;
    }

    public float getHitRate() {
        return hitRate;
    }

    public void setHitRate(float hitRate) {
        this.hitRate = hitRate;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public float getSilent() {
        return silent;
    }

    public void setSilent(float silent) {
        this.silent = silent;
    }

    public float getPetRate() {
        return petRate;
    }

    public void setPetRate(float petRate) {
        this.petRate = petRate;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public Race getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = Race.getByIndex(race);
    }

    public void setRace(Race race){
        this.race = race;
    }

    public long getDef() {
        return def;
    }

    public void setDef(long def) {
        this.def = def;
    }

    public String getDisplayName() {
        return "<font color='" + color + "'>" + firstName + "的" + secondName + type + "(" + element.getCn() + ")" + (sex == 0 ? "♂" : "♀") + "</font>";
    }

    public String getName(){
        return firstName + "的" + secondName + type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public float getEggRate() {
        return eggRate;
    }

    public void setEggRate(float eggRate) {
        this.eggRate = eggRate;
    }

    @Override
    public Skill[] getSkills() {
        return new Skill[]{skill};
    }

    public Monster clone() {
        try {
            return (Monster) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
