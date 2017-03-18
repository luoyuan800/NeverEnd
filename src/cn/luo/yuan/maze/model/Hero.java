package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.SecureRAMReader;
import cn.luo.yuan.maze.utils.Version;
import cn.luo.yuan.maze.utils.annotation.LongValue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class Hero implements Serializable {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private int index;//存档编号
    private SecureRAMReader ramReader;
    private String name;
    @LongValue
    private byte[] level;
    @LongValue
    private byte[] maxHp;
    @LongValue
    private byte[] hp;
    @LongValue
    private byte[] atk;
    @LongValue
    private byte[] def;
    @LongValue private byte[] agi;
    @LongValue private byte[] str;
    @LongValue private byte[] hpGrow;
    @LongValue private byte[] defGrow;
    @LongValue private byte[] atkGrow;
    private Set<Effect> effects = new HashSet<>(3);
    private Set<Accessory> accessories = new HashSet<>(3);

    public void removeEffect(Effect effect){
        effects.remove(effect);
    }
    public long getEffectAdditionHP(){
        long hp = 0;
        for(Effect effect : effects){
            if(effect instanceof HpEffect) {
                hp += ((HpEffect) effect).getHp();
            }
        }
        return hp;
    }
    public long getEffectAdditionAtk(){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof AtkEffect) {
                value += ((AtkEffect) effect).getAtk();
            }
        }
        return value;
    }
    public long getEffectAdditionDef(){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof DefEffect) {
                value += ((DefEffect) effect).getDef();
            }
        }
        return value;
    }
    public long getEffectAdditionAgi(){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof AgiEffect) {
                value += ((AgiEffect) effect).getAgi();
            }
        }
        return value;
    }
    public long getEffectAdditionStr(){
        long value = 0;
        for(Effect effect : effects){
            if(effect instanceof StrEffect) {
                value += ((StrEffect) effect).getStr();
            }
        }
        return value;
    }

    public long getHpGrow(){
        return ramReader.decodeLong(hpGrow);
    }
    public void setHpGrow(long grow){
        this.hpGrow = ramReader.encodeLong(grow);
    }

    public long getDefGrow(){
        return ramReader.decodeLong(defGrow);
    }
    public void setDefGrow(long grow){
        this.defGrow = ramReader.encodeLong(grow);
    }

    public long getAtkGrow(){
        return ramReader.decodeLong(atkGrow);
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

    public void setAgi(long agi){
        this.agi = ramReader.encodeLong(agi);
    }

    public long getStr(){
        return ramReader.decodeLong(str);
    }

    public void setStr(long str){
        this.str = ramReader.encodeLong(str);
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

    public long getLevel() {
        return ramReader.decodeLong(level);
    }

    public void setLevel(long level) {
        this.level = ramReader.encodeLong(level);
    }

    public long getMaxHp() {
        return ramReader.decodeLong(maxHp);
    }

    public void setMaxHp(long maxHp) {
        this.maxHp = ramReader.encodeLong(maxHp);
    }

    public long getHp() {
        return ramReader.decodeLong(hp) + getEffectAdditionHP() + getEffectAdditionStr() * getHpGrow();
    }

    public void setHp(long hp) {
        this.hp = ramReader.encodeLong(hp);
    }

    public long getAtk() {
        return ramReader.decodeLong(atk);
    }

    public void setAtk(long atk) {
        this.atk = ramReader.encodeLong(atk);
    }

    public long getDef() {
        return ramReader.decodeLong(def);
    }

    public void setDef(long def) {
        this.def = ramReader.encodeLong(def);
    }

    public Set<Effect> getEffects() {
        return effects;
    }

    public long getUpperHp(){
        return getMaxHp() + getEffectAdditionHP() + getEffectAdditionStr() * getHpGrow();
    }

    public long getUpperAtk(){
        return getAtk() + getEffectAdditionAtk() + getEffectAdditionStr() * getAtkGrow();
    }

    public long getUpperDef(){
        return getDef() + getEffectAdditionAtk() + getEffectAdditionAgi() * getDefGrow();
    }

    public void mountAccessory(Accessory accessory){
        if(accessories.add(accessory)){
            effects.addAll(accessory.getEffects());
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
