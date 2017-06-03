package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Version;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Hero implements Serializable, IDModel, HarmAble, SkillAbleObject, NameObject, PetOwner {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private int index;//存档编号
    private String name;//名字
    private EncodeLong maxHp = new EncodeLong(0);//血上限
    private EncodeLong hp = new EncodeLong(0);//当前血量
    private EncodeLong atk = new EncodeLong(0);//基础攻击
    private EncodeLong def = new EncodeLong(0);//基础防御
    private EncodeLong agi = new EncodeLong(0);//敏捷
    private EncodeLong str = new EncodeLong(0);//力量
    private EncodeLong hpGrow = new EncodeLong(0);//血量成长（每点力量）
    private EncodeLong defGrow = new EncodeLong(0);//防御成长（每点敏捷）
    private EncodeLong atkGrow = new EncodeLong(0);//攻击成长（每点力量）
    private long birthDay;//生日
    private EncodeLong reincarnate = new EncodeLong(0);//转生次数
    private EncodeLong material = new EncodeLong(0);//锻造点（货币）
    transient private HashSet<Effect> effects = new HashSet<>(3);//附加的效果
    transient private HashSet<Accessory> accessories = new HashSet<>(3);//装备
    transient private Skill[] skills = {EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL};//装备
    transient private ConcurrentLinkedDeque<Pet> pets = new ConcurrentLinkedDeque<>();
    transient private HashSet<ClickSkill> clickSkills = new HashSet<>(3);
    private Element element;//五行元素
    private String id;
    private EncodeLong point = new EncodeLong(0);
    private Gift gift;
    private EncodeLong click = new EncodeLong(0);
    private Race race;

    public Hero() {
    }

    public HashSet<Accessory> getAccessories() {
        synchronized (this) {
            if (accessories == null) {
                accessories = new HashSet<>(5);
            }
        }
        return accessories;
    }

    public long getMaterial() {
        return material.getValue();
    }

    public void setMaterial(long material) {
        this.material.setValue(material);
    }

    public void removeEffect(Effect effect) {
        getEffects().remove(effect);
    }

    public long getHpGrow() {
        return hpGrow.getValue();
    }

    public void setHpGrow(long hpGrow) {
        this.hpGrow.setValue(hpGrow);
    }

    public long getDefGrow() {
        return defGrow.getValue();
    }

    public void setDefGrow(long defGrow) {
        this.defGrow.setValue(defGrow);
    }

    public long getAtkGrow() {
        return atkGrow.getValue();
    }

    public void setAtkGrow(long atkGrow) {
        this.atkGrow.setValue(atkGrow);
    }

    public void addEffect(Effect effect) {
        getEffects().add(effect);
    }

    public long getAgi() {
        return agi.getValue();
    }

    public void setAgi(long agi) {
        this.agi.setValue(agi);
    }

    public long getStr() {
        return str.getValue();
    }

    public void setStr(long str) {
        this.str.setValue(str);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxHp() {
        return this.maxHp.getValue();
    }

    public void setMaxHp(long maxHp) {
        this.maxHp.setValue(maxHp);
    }


    public long getCurrentHp() {
        return this.hp.getValue() + EffectHandler.getEffectAdditionLongValue(EffectHandler.HP, getEffects()) + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects()) * getHpGrow();
    }

    public long getHp() {
        return hp.getValue();
    }

    public void setHp(long hp) {
        if(hp > maxHp.getValue()){
            hp = maxHp.getValue();
        }
        this.hp.setValue(hp);
    }


    public long getAtk() {
        return this.atk.getValue();
    }

    public void setAtk(long atk) {
        this.atk.setValue(atk);
    }

    public long getDef() {
        return def.getValue();
    }

    public void setDef(long def) {
        this.def.setValue(def);
    }

    public HashSet<Effect> getEffects() {
        synchronized (this) {
            if (effects == null) {
                effects = new HashSet<>(3);
            }
        }
        return effects;
    }

    public long getUpperHp() {
        return getMaxHp() + EffectHandler.getEffectAdditionLongValue(EffectHandler.HP, getEffects()) + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects()) * getHpGrow();
    }

    public long getUpperAtk() {
        return getAtk() + EffectHandler.getEffectAdditionLongValue(EffectHandler.ATK, getEffects()) + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects()) * getAtkGrow();
    }

    public long getUpperDef() {
        return getDef() + EffectHandler.getEffectAdditionLongValue(EffectHandler.DEF, getEffects()) + EffectHandler.getEffectAdditionLongValue(EffectHandler.AGI, getEffects()) * getDefGrow();
    }



    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getReincarnate() {
        return reincarnate.getValue();
    }

    public void setReincarnate(long reincarnate) {
        this.reincarnate.setValue(reincarnate);
    }

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return "<font color=\"#800080\">" + getName() + "</font>(" + getElement() + ")";
    }

    public long getPoint() {
        return point.getValue();
    }

    public void setPoint(long point) {
        this.point.setValue(point);
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public long getClick() {
        return click.getValue();
    }

    public void setClick(long click) {
        this.click.setValue(click);
    }

    public Collection<Pet> getPets() {
        if (pets == null) {
            synchronized (this) {
                if (pets == null)
                    pets = new ConcurrentLinkedDeque<>();
            }
        }
        return pets;
    }

    public Skill[] getSkills() {
        if (skills == null) {
            synchronized (this) {
                if (skills == null)
                    skills = new Skill[]{EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL};
            }
        }
        return skills;
    }

    public void setSkills(Skill[] skills) {
        this.skills = skills;
    }


    public Race getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = Race.getByIndex(race);
    }

    public HashSet<ClickSkill> getClickSkills() {
        if (clickSkills == null) {
            synchronized (this) {
                if (clickSkills == null)
                    clickSkills = new HashSet<ClickSkill>(3);
            }
        }
        return clickSkills;
    }

    public void setClickSkills(HashSet<ClickSkill> clickSkills) {
        this.clickSkills = clickSkills;
    }
}
