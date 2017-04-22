package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.utils.NormalRAMReader;
import cn.luo.yuan.maze.utils.Version;
import cn.luo.yuan.maze.utils.annotation.LongValue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static cn.luo.yuan.maze.utils.EffectHandler.*;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Hero implements Serializable, IDModel {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private int index;//存档编号
    private NormalRAMReader ramReader;
    private String name;//名字
    @LongValue
    private long maxHp;//血上限
    @LongValue
    private long hp;//当前血量
    @LongValue
    private long atk;//基础攻击
    @LongValue
    private long def;//基础防御
    @LongValue private long agi;//敏捷
    @LongValue private long str;//力量
    @LongValue private long hpGrow;//血量成长（每点力量）
    @LongValue private long defGrow;//防御成长（每点敏捷）
    @LongValue private long atkGrow;//攻击成长（每点力量）
    private long birthDay;//生日
    private long reincarnate;//转生次数
    private long material;//锻造点（货币）
    private Set<Effect> effects = new HashSet<>(3);//附加的效果
    private Set<Accessory> accessories = new HashSet<>(3);//装备
    private Element element;//五行元素
    private String id;
    private long point;
    private String gift;
    private long click;

    public Set<Accessory> getAccessories(){
        return accessories;
    }
    public Hero(){
    }
    public long getMaterial(){
        return ramReader.decodeLong(material);
    }
    public long getEncodeMaterial(){
        return material;
    }
    public void setMaterial(long material, boolean encode){
        if(encode) {
            this.material = ramReader.encodeLong(material);
        }else{
            this.material = material;
        }
    }
    public void removeEffect(Effect effect){
        effects.remove(effect);
    }

    public long getHpGrow(){
        return ramReader.decodeLong(hpGrow);
    }
    public long getEncodeHpGrow(){
        return hpGrow;
    }
    public void setHpGrow(long grow, boolean encode){
        if(encode) {
            this.hpGrow = ramReader.encodeLong(grow);
        }else{
            this.hpGrow = grow;
        }
    }

    public long getDefGrow(){
        return ramReader.decodeLong(defGrow);
    }

    public long getEncodeDefGrow(){
        return defGrow;
    }
    public void setDefGrow(long grow, boolean encode){
        if(encode) {
            this.defGrow = ramReader.encodeLong(grow);
        }else{
            this.defGrow = grow;
        }
    }

    public long getAtkGrow(){
        return ramReader.decodeLong(atkGrow);
    }
    public long getEncodeAtkGrow(){
        return atkGrow;
    }
    public void setAtkGrow(long grow, boolean encode){
        if(encode) {
            this.atkGrow = ramReader.encodeLong(grow);
        }else{
            this.atkGrow = grow;
        }
    }

    public void addEffect(Effect effect){
        effects.add(effect);
    }

    public long getAgi(){
        return ramReader.decodeLong(agi);
    }
    public long getEncodeAgi(){
        return agi;
    }

    public void setAgi(long agi, boolean encode){
        if(encode) {
            this.agi = ramReader.encodeLong(agi);
        }else{
            this.agi = agi;
        }
    }
    public void setAgi(long agi){
        this.agi = agi;
    }

    public long getStr(){
        return ramReader.decodeLong(str);
    }
    public long getEncodeStr(){
        return str;
    }

    public void setStr(long str, boolean encode){
        if(encode) {
            this.str = ramReader.encodeLong(str);
        }else{
            this.str = str;
        }
    }
    public void setStr(long str){
        this.str = str;
    }

    public NormalRAMReader getRamReader() {
        return ramReader;
    }

    public void setRamReader(NormalRAMReader ramReader) {
        this.ramReader = ramReader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxHp() {
        return ramReader.decodeLong(maxHp);
    }
    public long getEncodeMaxHp() {
        return maxHp;
    }

    public void setMaxHp(long maxHp, boolean encode) {
        if(encode) {
            this.maxHp = ramReader.encodeLong(maxHp);
        }else{
            this.maxHp = maxHp;
        }
    }

    public long getCurrentHp() {
        return ramReader.decodeLong(hp) + getEffectAdditionLongValue(HP, effects) + getEffectAdditionLongValue(STR, effects) * getHpGrow();
    }

    public long getHp(){
        return ramReader.decodeLong(hp);
    }
    public long getEncodeHp(){
        return hp;
    }

    public void setHp(long hp, boolean encode) {
        if(encode) {
            this.hp = ramReader.encodeLong(hp);
        }else{
            this.hp = hp;
        }
    }

    public long getAtk() {
        return ramReader.decodeLong(atk);
    }
    public long getEncodeAtk() {
        return atk;
    }

    public void setAtk(long atk, boolean encode) {
        if(encode) {
            this.atk = ramReader.encodeLong(atk);
        }else{
            this.atk = atk;
        }
    }

    public long getDef() {
        return ramReader.decodeLong(def);
    }
    public long getEncodeDef() {
        return def;
    }

    public void setDef(long def, boolean encode) {
        if(encode) {
            this.def = ramReader.encodeLong(def);
        }else{
            this.def = def;
        }
    }

    public Set<Effect> getEffects() {
        return effects;
    }

    public long getUpperHp(){
        return getMaxHp() + getEffectAdditionLongValue(HP,effects)+ getEffectAdditionLongValue(STR, effects) * getHpGrow();
    }

    public long getUpperAtk(){
        return getAtk() + getEffectAdditionLongValue(ATK, effects) + getEffectAdditionLongValue(STR, effects) * getAtkGrow();
    }

    public long getUpperDef(){
        return getDef() + getEffectAdditionLongValue(DEF, effects) + getEffectAdditionLongValue(AGI, effects) * getDefGrow();
    }

    public void mountAccessory(Accessory accessory){
        Iterator<Accessory> iterator = accessories.iterator();
        while (iterator.hasNext()){
            Accessory acc = iterator.next();
            if(acc.getType().equals(accessory.getType())){
                iterator.remove();
                effects.removeAll(acc.getEffects());
            }
        }
        if(accessories.add(accessory)){
            effects.addAll(accessory.getEffects());
        }
    }

    public void unMountAccessory(Accessory accessory){
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
        return reincarnate;
    }

    public void setReincarnate(long reincarnate) {
        this.reincarnate = reincarnate;
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

    public void setMaxHp(long maxHp) {
        this.maxHp = maxHp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public void setAtk(long atk) {
        this.atk = atk;
    }

    public void setDef(long def) {
        this.def = def;
    }

    public void setHpGrow(long hpGrow) {
        this.hpGrow = hpGrow;
    }

    public void setDefGrow(long defGrow) {
        this.defGrow = defGrow;
    }

    public void setAtkGrow(long atkGrow) {
        this.atkGrow = atkGrow;
    }

    public void setMaterial(long material) {
        this.material = material;
    }

    public String getDisplayName() {
        return "<font color=\"#800080\">" + getName() + "</font>(" + getElement() + ")";
    }

    public long getEncodePoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public long getPoint() {
        return ramReader.decodeLong(point);
    }

    public void setPoint(long point, boolean encode) {
        if(encode) {
            this.point = ramReader.encodeLong(point);
        }else{
            this.point = point;
        }
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public long getClick() {
        return click;
    }

    public void setClick(long click) {
        this.click = click;
    }

}
