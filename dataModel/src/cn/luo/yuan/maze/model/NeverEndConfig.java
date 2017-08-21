package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gluo on 6/30/2017.
 */
public class NeverEndConfig implements IDModel, Serializable {
    private String id;
    private boolean delete;
    private int theme = 0;
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private String version;
    private String head;
    private String sign;
    private boolean exception;
    private boolean longKiller;
    private boolean elementer;
    private Set<Integer> catchedMonsterIndex;
    private boolean petGift;


    public synchronized boolean isMonsterCatched(int index){
        if(catchedMonsterIndex == null){
            catchedMonsterIndex = new HashSet<>();
        }
        return catchedMonsterIndex.contains(index);
    }

    public synchronized  void addMonsterCatch(int index){
        if(catchedMonsterIndex == null){
            catchedMonsterIndex = new HashSet<>();
        }
        catchedMonsterIndex.add(index);
    }

    public int getCatchedCount(){
        if(catchedMonsterIndex == null){
            catchedMonsterIndex = new HashSet<>();
        }
        return catchedMonsterIndex.size();
    }

    private float PET_RATE_REDUCE = Data.PET_RATE_REDUCE; //宠物捕获率修正系数，越大率越低
    private float EGG_RATE_REDUCE = Data.EGG_RATE_REDUCE; //宠物生蛋率修正系数，越大率越低

    private  long MATERIAL_LIMIT = Data.MATERIAL_LIMIT;//如果携带超过这个数量的锻造，就增加商店的价格和怪物的攻击

    public void setMATERIAL_LIMIT(long limit){
        MATERIAL_LIMIT = limit;
        Data.MATERIAL_LIMIT = limit;
    }
    public long getMATERIAL_LIMIT(){
        return MATERIAL_LIMIT;
    }

    public void load(){
        Data.MATERIAL_LIMIT = this.MATERIAL_LIMIT;
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
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }

    public boolean isException() {
        return exception;
    }

    public boolean isLongKiller() {
        return longKiller;
    }

    public void setLongKiller(boolean longKiller) {
        this.longKiller = longKiller;
    }

    public boolean isElementer() {
        return elementer;
    }

    public void setElementer(boolean elementer) {
        this.elementer = elementer;
    }

    public boolean isPetGift() {
        return petGift;
    }

    public void setPetGift(boolean petGift) {
        this.petGift = petGift;
    }

    public float getPET_RATE_REDUCE() {
        return PET_RATE_REDUCE;
    }

    public void setPET_RATE_REDUCE(float PET_RATE_REDUCE) {
        this.PET_RATE_REDUCE = PET_RATE_REDUCE;
        Data.PET_RATE_REDUCE = this.PET_RATE_REDUCE;
    }

    public float getEGG_RATE_REDUCE() {
        return EGG_RATE_REDUCE;
    }

    public void setEGG_RATE_REDUCE(float EGG_RATE_REDUCE) {
        this.EGG_RATE_REDUCE = EGG_RATE_REDUCE;
    }
}
