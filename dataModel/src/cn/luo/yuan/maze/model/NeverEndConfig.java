package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gluo on 6/30/2017.
 */
public class NeverEndConfig implements IDModel, Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private String id;
    private boolean delete;
    private int theme = 0;
    private String version;
    private String currentVersion;
    private String head;
    private String sign;
    private boolean exception;
    private boolean longKiller;
    private boolean elementer;
    private Set<Integer> catchedMonsterIndex;
    private boolean petGift;
    private String catchFilter;
    private String catchRestrictor;
    private EncodeLong debris;
    private EncodeLong gift;
    private float PET_RATE_REDUCE = Data.PET_RATE_REDUCE; //宠物捕获率修正系数，越大率越低
    private float EGG_RATE_REDUCE = Data.EGG_RATE_REDUCE; //宠物生蛋率修正系数，越大率越低
    private long MATERIAL_LIMIT = Data.MATERIAL_LIMIT;//如果携带超过这个数量的锻造，就增加商店的价格和怪物的攻击

    public long getDebris() {
        if (debris == null) {
            debris = new EncodeLong(0);
        }
        return debris.getValue();
    }

    public void setDebris(long value) {
        if (debris == null) {
            debris = new EncodeLong(0);
        }
        debris.setValue(value);
    }

    public long getGift() {
        if (gift == null) {
            gift = new EncodeLong(0);
        }
        return gift.getValue();
    }

    public void setGift(long value) {
        if (gift == null) {
            gift = new EncodeLong(0);
        }
        gift.setValue(value);
    }

    public synchronized boolean isMonsterCatched(int index) {
        if (catchedMonsterIndex == null) {
            catchedMonsterIndex = new HashSet<>();
        }
        return catchedMonsterIndex.contains(index);
    }

    public synchronized void addMonsterCatch(int index) {
        if (catchedMonsterIndex == null) {
            catchedMonsterIndex = new HashSet<>();
        }
        catchedMonsterIndex.add(index);
    }

    public int getCatchedCount() {
        if (catchedMonsterIndex == null) {
            catchedMonsterIndex = new HashSet<>();
        }
        return catchedMonsterIndex.size();
    }

    public long getMATERIAL_LIMIT() {
        return MATERIAL_LIMIT;
    }

    public void setMATERIAL_LIMIT(long limit) {
        MATERIAL_LIMIT = limit;
        Data.MATERIAL_LIMIT = limit;
    }

    public void load() {
        setMATERIAL_LIMIT(getMATERIAL_LIMIT());
        setEGG_RATE_REDUCE(this.getEGG_RATE_REDUCE());
        setPET_RATE_REDUCE(this.getPET_RATE_REDUCE());
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
        setCurrentVersion(version);
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

    public boolean isException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
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
        Data.EGG_RATE_REDUCE = this.EGG_RATE_REDUCE;
    }

    public String getCatchFilter() {
        return catchFilter;
    }

    public void setCatchFilter(String catchFilter) {
        this.catchFilter = catchFilter;
    }

    public String getCatchRestrictor() {
        return catchRestrictor;
    }

    public void setCatchRestrictor(String catchRestrictor) {
        this.catchRestrictor = catchRestrictor;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
