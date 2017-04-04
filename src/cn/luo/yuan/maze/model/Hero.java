package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.utils.SecureRAMReader;
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
    private SecureRAMReader ramReader;
    private String name;//名字
    @LongValue
    private byte[] maxHp;//血上限
    @LongValue
    private byte[] hp;//当前血量
    @LongValue
    private byte[] atk;//基础攻击
    @LongValue
    private byte[] def;//基础防御
    @LongValue private byte[] agi;//敏捷
    @LongValue private byte[] str;//力量
    @LongValue private byte[] hpGrow;//血量成长（每点力量）
    @LongValue private byte[] defGrow;//防御成长（每点敏捷）
    @LongValue private byte[] atkGrow;//攻击成长（每点力量）
    private long birthDay;//生日
    private long reincarnate;//转生次数
    private byte[] material;//锻造点（货币）
    private Set<Effect> effects = new HashSet<>(3);//附加的效果
    private Set<Accessory> accessories = new HashSet<>(3);//装备
    private Element element;//五行元素
    private String id;
    private byte[] point;
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
    public byte[] getEncodeMaterial(){
        return material;
    }
    public void setMaterial(long material){
        this.material = ramReader.encodeLong(material);
    }
    public void removeEffect(Effect effect){
        effects.remove(effect);
    }

    public long getHpGrow(){
        return ramReader.decodeLong(hpGrow);
    }
    public byte[] getEncodeHpGrow(){
        return hpGrow;
    }
    public void setHpGrow(long grow){
        this.hpGrow = ramReader.encodeLong(grow);
    }

    public long getDefGrow(){
        return ramReader.decodeLong(defGrow);
    }

    public byte[] getEncodeDefGrow(){
        return defGrow;
    }
    public void setDefGrow(long grow){
        this.defGrow = ramReader.encodeLong(grow);
    }

    public long getAtkGrow(){
        return ramReader.decodeLong(atkGrow);
    }
    public byte[] getEncodeAtkGrow(){
        return atkGrow;
    }
    public void setAtkGrow(long grow){
        this.atkGrow = ramReader.encodeLong(grow);
    }

    public void addEffect(Effect effect){
        effects.add(effect);
    }

    public long getAgi(){
        return ramReader.decodeLong(agi);
    }
    public byte[] getEncodeAgi(){
        return agi;
    }

    public void setAgi(long agi){
        this.agi = ramReader.encodeLong(agi);
    }
    public void setAgi(byte[] agi){
        this.agi = agi;
    }

    public long getStr(){
        return ramReader.decodeLong(str);
    }
    public byte[] getEncodeStr(){
        return str;
    }

    public void setStr(long str){
        this.str = ramReader.encodeLong(str);
    }
    public void setStr(byte[] str){
        this.str = str;
    }

    public SecureRAMReader getRamReader() {
        return ramReader;
    }

    public void setRamReader(SecureRAMReader ramReader) {
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
    public byte[] getEncodeMaxHp() {
        return maxHp;
    }

    public void setMaxHp(long maxHp) {
        this.maxHp = ramReader.encodeLong(maxHp);
    }

    public long getCurrentHp() {
        return ramReader.decodeLong(hp) + getEffectAdditionLongValue(HP, effects) + getEffectAdditionLongValue(STR, effects) * getHpGrow();
    }

    public long getHp(){
        return ramReader.decodeLong(hp);
    }
    public byte[] getEncodeHp(){
        return hp;
    }

    public void setHp(long hp) {
        this.hp = ramReader.encodeLong(hp);
    }

    public long getAtk() {
        return ramReader.decodeLong(atk);
    }
    public byte[] getEncodeAtk() {
        return atk;
    }

    public void setAtk(long atk) {
        this.atk = ramReader.encodeLong(atk);
    }

    public long getDef() {
        return ramReader.decodeLong(def);
    }
    public byte[] getEncodeDef() {
        return def;
    }

    public void setDef(long def) {
        this.def = ramReader.encodeLong(def);
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

    public void setMaxHp(byte[] maxHp) {
        this.maxHp = maxHp;
    }

    public void setHp(byte[] hp) {
        this.hp = hp;
    }

    public void setAtk(byte[] atk) {
        this.atk = atk;
    }

    public void setDef(byte[] def) {
        this.def = def;
    }

    public void setHpGrow(byte[] hpGrow) {
        this.hpGrow = hpGrow;
    }

    public void setDefGrow(byte[] defGrow) {
        this.defGrow = defGrow;
    }

    public void setAtkGrow(byte[] atkGrow) {
        this.atkGrow = atkGrow;
    }

    public void setMaterial(byte[] material) {
        this.material = material;
    }

    public String getDisplayName() {
        return "<font color=\"#800080\">" + getName() + "</font>(" + getElement() + ")";
    }

    public byte[] getEncodePoint() {
        return point;
    }

    public void setPoint(byte[] point) {
        this.point = point;
    }

    public long getPoint() {
        return ramReader.decodeLong(point);
    }

    public void setPoint(long point) {
        this.point = ramReader.encodeLong(point);
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
