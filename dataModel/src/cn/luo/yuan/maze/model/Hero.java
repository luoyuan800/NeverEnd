package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.model.skill.EmptySkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.model.skill.click.ClickSkill;
import cn.luo.yuan.maze.service.EffectHandler;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.Random;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Hero extends HarmObject implements Serializable, IDModel, SkillAbleObject, NameObject, PetOwner {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private boolean delete;
    private int index;//存档编号
    private String name;//名字
    private EncodeLong maxHp = new EncodeLong(20);//血上限
    private EncodeLong hp = new EncodeLong(20);//当前血量
    private EncodeLong atk = new EncodeLong(15);//基础攻击
    private EncodeLong def = new EncodeLong(5);//基础防御
    private EncodeLong agi = new EncodeLong(0);//敏捷
    private EncodeLong str = new EncodeLong(0);//力量
    private EncodeLong hpGrow = new EncodeLong(2);//血量成长（每点力量）
    private EncodeLong defGrow = new EncodeLong(3);//防御成长（每点敏捷）
    private EncodeLong atkGrow = new EncodeLong(1);//攻击成长（每点力量）
    private long birthDay;//生日
    private EncodeLong reincarnate = new EncodeLong(0);//转生次数
    private EncodeLong material = new EncodeLong(0);//锻造点（货币）
    transient private HashSet<Effect> effects = new HashSet<>(3);//附加的效果
    transient private HashSet<Accessory> accessories = new HashSet<>(3);//装备
    transient private Skill[] skills = {EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL};//装备
    transient private ConcurrentLinkedDeque<Pet> pets = new ConcurrentLinkedDeque<>();
    transient private ArrayList<ClickSkill> clickSkills = new ArrayList<>(3);
    private Element element;//五行元素
    private String id;
    private EncodeLong point = new EncodeLong(0);
    private Gift gift;
    private EncodeLong click = new EncodeLong(0);
    private Race race;
    private float elementRate = 0.5f;
    private int petCount = (int) Data.BASE_PET_COUNT;
    public Hero() {
    }

    @Override
    public boolean isDelete() {
        return delete;
    }

    public void markDelete() {
        delete = true;
    }

    @NotNull
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
        if(hpGrow <= 0){
            hpGrow =5;
        }
        this.hpGrow.setValue(hpGrow);
    }

    public long getDefGrow() {
        return defGrow.getValue();
    }

    public void setDefGrow(long defGrow) {
        if(defGrow <= 0){
            defGrow = 3;
        }
        this.defGrow.setValue(defGrow);
    }

    public long getAtkGrow() {
        return atkGrow.getValue();
    }

    public void setAtkGrow(long atkGrow) {
        if(atkGrow <= 0){
            atkGrow = 1;
        }
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

    public long getMaxAgi() {
        return agi.getValue() + EffectHandler.getEffectAdditionLongValue(EffectHandler.AGI, getEffects(), this);
    }

    public long getStr() {
        return str.getValue();
    }

    public void setStr(long str) {
        this.str.setValue(str);
    }

    public long getMaxStr() {
        return str.getValue() + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects(), this);
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


    public long getCurrentMaxHp() {
        return this.maxHp.getValue() + EffectHandler.getEffectAdditionLongValue(EffectHandler.HP, getEffects(), this) + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects(), this) * getHpGrow();
    }

    public long getCurrentHp() {
        return this.hp.getValue() + EffectHandler.getEffectAdditionLongValue(EffectHandler.HP, getEffects(), this) + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects(), this) * getHpGrow();
    }

    public long getHp() {
        return hp.getValue();
    }

    public void setHp(long hp) {
        if (hp > maxHp.getValue()) {
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
        return getMaxHp() + EffectHandler.getEffectAdditionLongValue(EffectHandler.HP, getEffects(), this) + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects(), this) * getHpGrow();
    }

    @Override
    public boolean isDodge(Random random) {
        float v = random.nextLong(100) + random.nextLong((long) (getAgi() * Data.DODGE_AGI_RATE)) + random.nextFloat(EffectHandler.getEffectAdditionFloatValue(EffectHandler.DOGE, getEffects()));
        if(v > Data.RATE_MAX){
            v = Data.RATE_MAX;
        }
        return v > 97 + random.nextInt(100) + random.nextLong((long) (getStr() * Data.DODGE_STR_RATE));
    }

    @Override
    public boolean isHit(Random random) {
        return random.nextLong(100) + getStr() * Data.HIT_STR_RATE > 97
                + random.nextInt(100) +
                random.nextLong((long) (getAgi() * Data.HIT_AGI_RATE));
    }

    @Override
    public boolean isParry(Random random) {
        float v = random.nextLong(100) + getStr() * Data.PARRY_STR_RATE + random.nextFloat(EffectHandler.getEffectAdditionFloatValue(EffectHandler.PARRY, getEffects()));
        if(v > Data.RATE_MAX){
            v = Data.RATE_MAX;
        }
        return v > 97 + random.nextInt(100) + random.nextLong((long) (getAgi() * Data.PARRY_AGI_RATE));
    }

    public long getUpperAtk() {
        return getAtk() + EffectHandler.getEffectAdditionLongValue(EffectHandler.ATK, getEffects(), this) + EffectHandler.getEffectAdditionLongValue(EffectHandler.STR, getEffects(), this) * getAtkGrow();
    }

    public long getUpperDef() {
        return getDef() + EffectHandler.getEffectAdditionLongValue(EffectHandler.DEF, getEffects(), this) + EffectHandler.getEffectAdditionLongValue(EffectHandler.AGI, getEffects(), this) * getDefGrow();
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
        return "<font color=\"" + (race!=null? race.getColor() : "") + "\">" + "[" + getRace() + "]" + getName() + "(" + getElement() + ")" + "</font> " + (getReincarnate() > 0 ? (" + " + getReincarnate()) : "");
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


    public
    @NotNull
    Collection<Pet> getPets() {
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

    public ArrayList<ClickSkill> getClickSkills() {
        if (clickSkills == null) {
            synchronized (this) {
                if (clickSkills == null)
                    clickSkills = new ArrayList<>(3);
            }
        }
        return clickSkills;
    }

    public void setClickSkills(ArrayList<ClickSkill> clickSkills) {
        this.clickSkills = clickSkills;
    }

    @Override
    public float getElementRate() {
        return elementRate;
    }

    public void setElementRate(float elementRate) {
        this.elementRate = elementRate;
    }

    public int getPetCount() {
        return petCount;
    }

    public void setPetCount(int petCount) {
        this.petCount = petCount;
    }

    @Override
    public String toString() {
        return getDisplayName() + "<br>atk: " + StringUtils.formatNumber(getAtk()) + "<br>" +
                "def: " + StringUtils.formatNumber(getDef()) + "<br>" +
                "hp: " + StringUtils.formatNumber(getMaxHp());
    }
}
