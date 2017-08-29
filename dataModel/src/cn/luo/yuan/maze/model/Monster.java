package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.names.FirstName;
import cn.luo.yuan.maze.model.names.SecondName;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 4/1/2017.
 */
public class Monster extends HarmObject implements IDModel, SilentAbleObject, NameObject, SkillAbleObject,Cloneable, Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private int next;
    private FirstName firstName;
    private SecondName secondName;
    private String type;
    private EncodeLong atk = new EncodeLong(0);
    private EncodeLong hp= new EncodeLong(0);
    private EncodeLong material= new EncodeLong(0);
    private EncodeLong maxHP= new EncodeLong(0);
    private float hitRate = 100.0f;
    private Element element;
    private float silent;
    private float eggRate = 0;
    private float petRate = 0;
    private int index;
    private EncodeLong def= new EncodeLong(0);
    private String color;
    private int sex = -1;
    private Race race;
    private Skill skill;
    private int rank;
    private float elementRate = 0.5f;
    private List<Effect> containsEffects = new ArrayList<>(2);
    private long atkAddition;
    private long hpAddition;
    private String desc;
    private boolean delete;

    public long getUpperAtk(){
        return getAtk();
    }

    public FirstName getFirstName() {
        return firstName;
    }

    public void setFirstName(FirstName firstName) {
        if(this.firstName!=null){
            setAtk(this.firstName.getReducedAtk(atk.getValue()));
            setMaxHp(this.firstName.getReducedHp(maxHP.getValue()));
            setHp(maxHP.getValue());
            silent -= this.firstName.getSilent();
            eggRate -= this.firstName.getEggRate();

        }
        this.firstName = firstName;
        if(this.firstName != null){
            setAtk(atk.getValue() + firstName.getAtkAddition(getAtk()));
            setMaxHp(maxHP.getValue() + firstName.getHPAddition(getMaxHp()));
            setHp(maxHP.getValue());
            silent += firstName.getSilent();
            eggRate += firstName.getEggRate();
        }
    }

    public SecondName getSecondName() {
        return secondName;
    }

    public void setSecondName(SecondName secondName) {
        if(this.secondName!=null){
            setAtk(this.secondName.getReducedAtk(atk.getValue()));
            setMaxHp(this.secondName.getReducedHp(maxHP.getValue()));
            setHp(maxHP.getValue());
            silent -= this.secondName.getSilent();
            petRate -= this.secondName.getPetRate();
        }
        this.secondName = secondName;
        if(this.secondName!=null){
            setAtk(atk.getValue() + secondName.getAtkAddition(atk.getValue()));
            setMaxHp( maxHP.getValue() + secondName.getHpAddition(maxHP.getValue()));
            setHp(maxHP.getValue());
            silent += secondName.getSilent();
            petRate += secondName.getPetRate();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAtk() {
        return atk.getValue();
    }

    public void setAtk(long atk) {
        this.atk.setValue(atk);
    }

    public long getHp() {
        return hp.getValue();
    }

    public void setHp(long hp) {
        this.hp.setValue(hp);
    }

    public long getMaterial() {
        return material.getValue();
    }

    public void setMaterial(long material) {
        this.material.setValue(material);
    }

    public long getMaxHp() {
        return maxHP.getValue();
    }

    public void setMaxHp(long maxHP) {
        this.maxHP.setValue(maxHP);
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
        return def.getValue();
    }

    public void setDef(long def) {
        this.def.setValue(def);
    }

    public String getDisplayName() {
        return "<font color='" + color + "'>" + firstName.getName() + "的" + secondName.getName() + type + "(" + element.getCn() + ")" + StringUtils.formatSex(getSex()) + "</font>";
    }

    public String getName(){
        return firstName.getName() + "的" + secondName.getName() + type;
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
            Monster clone =  (Monster) super.clone();
            clone.hp = new EncodeLong(clone.getHp());
            clone.maxHP = new EncodeLong(clone.getMaxHp());
            clone.atk = new EncodeLong(clone.getAtk());
            clone.def = new EncodeLong(clone.getDef());
            clone.material = new EncodeLong(clone.getMaterial());
            return clone;
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Skill getSkill(){
        return  skill;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public float getElementRate() {
        return elementRate;
    }

    public void setElementRate(float elementRate) {
        this.elementRate = elementRate;
    }

    public long getUpperDef() {
        return getDef();
    }

    public List<Effect> getContainsEffects() {
        synchronized (this) {
            if (containsEffects == null) {
                containsEffects = new ArrayList<>(2);
            }
        }

        return containsEffects;
    }

    public void setContainsEffects(List<Effect> containsEffects){
        this.containsEffects = containsEffects;
    }

    public long getAtkAddition() {
        return atkAddition;
    }

    public void setAtkAddition(long atkAddition) {
        this.atkAddition = atkAddition;
    }

    public long getHpAddition() {
        return hpAddition;
    }

    public void setHpAddition(long hpAddition) {
        this.hpAddition = hpAddition;
    }

    public String toString(){
        return "atk: " + StringUtils.formatNumber(getUpperAtk(), false) + ", def: " + StringUtils.formatNumber(getUpperDef(), false) + ", Hp: " + StringUtils.formatNumber(getUpperHp(), false);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean isDelete() {
        return delete;
    }

    @Override
    public void markDelete() {
        this.delete = true;
    }

    @Override
    public String getId() {
        return String.valueOf(index);
    }

    @Override
    public void setId(String id) {
        this.index = Integer.parseInt(id);
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
