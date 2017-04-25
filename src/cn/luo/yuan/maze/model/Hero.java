package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Version;
import cn.luo.yuan.maze.utils.annotation.LongValue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static cn.luo.yuan.maze.utils.EffectHandler.AGI;
import static cn.luo.yuan.maze.utils.EffectHandler.ATK;
import static cn.luo.yuan.maze.utils.EffectHandler.DEF;
import static cn.luo.yuan.maze.utils.EffectHandler.HP;
import static cn.luo.yuan.maze.utils.EffectHandler.STR;
import static cn.luo.yuan.maze.utils.EffectHandler.getEffectAdditionLongValue;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Hero implements Serializable, IDModel {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private int index;//存档编号
    private String name;//名字
    @LongValue
    private EncodeLong maxHp = new EncodeLong(0);//血上限
    @LongValue
    private EncodeLong hp = new EncodeLong(0);//当前血量
    @LongValue
    private EncodeLong atk = new EncodeLong(0);//基础攻击
    @LongValue
    private EncodeLong def = new EncodeLong(0);//基础防御
    @LongValue
    private EncodeLong agi = new EncodeLong(0);//敏捷
    @LongValue
    private EncodeLong str = new EncodeLong(0);//力量
    @LongValue
    private EncodeLong hpGrow = new EncodeLong(0);//血量成长（每点力量）
    @LongValue
    private EncodeLong defGrow = new EncodeLong(0);//防御成长（每点敏捷）
    @LongValue
    private EncodeLong atkGrow = new EncodeLong(0);//攻击成长（每点力量）
    private long birthDay;//生日
    private EncodeLong reincarnate = new EncodeLong(0);//转生次数
    private EncodeLong material = new EncodeLong(0);//锻造点（货币）
    transient private Set<Effect> effects = new HashSet<>(3);//附加的效果
    transient private Set<Accessory> accessories = new HashSet<>(3);//装备
    private Element element;//五行元素
    private String id;
    private EncodeLong point = new EncodeLong(0);
    private String gift;
    private EncodeLong click = new EncodeLong(0);

    public Hero() {
    }

    public Set<Accessory> getAccessories() {
        return accessories;
    }

    public long getMaterial() {
        return material.getValue();
    }

    public void setMaterial(long material) {
        this.material.setValue(material);
    }

    public void removeEffect(Effect effect) {
        effects.remove(effect);
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
        effects.add(effect);
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
        return this.hp.getValue() + getEffectAdditionLongValue(HP, effects) + getEffectAdditionLongValue(STR, effects) * getHpGrow();
    }

    public long getHp() {
        return hp.getValue();
    }

    public void setHp(long hp) {
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

    public Set<Effect> getEffects() {
        return effects;
    }

    public long getUpperHp() {
        return getMaxHp() + getEffectAdditionLongValue(HP, effects) + getEffectAdditionLongValue(STR, effects) * getHpGrow();
    }

    public long getUpperAtk() {
        return getAtk() + getEffectAdditionLongValue(ATK, effects) + getEffectAdditionLongValue(STR, effects) * getAtkGrow();
    }

    public long getUpperDef() {
        return getDef() + getEffectAdditionLongValue(DEF, effects) + getEffectAdditionLongValue(AGI, effects) * getDefGrow();
    }

    /**
     * @param accessory mounted
     * @return Accessory that un mount
     */
    public Accessory mountAccessory(Accessory accessory) {
        Accessory uMount = null;
        Iterator<Accessory> iterator = accessories.iterator();
        while (iterator.hasNext()) {
            uMount = iterator.next();
            if (uMount.getType().equals(accessory.getType())) {
                iterator.remove();
                effects.removeAll(uMount.getEffects());
                uMount.setMounted(false);
            }
        }
        if (accessories.add(accessory)) {
            effects.addAll(accessory.getEffects());
            accessory.setMounted(true);
        }
        return uMount;
    }

    public void unMountAccessory(Accessory accessory) {
        accessories.remove(accessory);
        effects.removeAll(accessory.getEffects());
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

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public long getClick() {
        return click.getValue();
    }

    public void setClick(long click) {
        this.click.setValue(click);
    }

}
