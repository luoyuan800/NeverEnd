package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.StringUtils;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by gluo on 4/25/2017.
 */
public class Pet extends Monster implements IDModel, OwnedAble {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private EncodeLong level = new EncodeLong(0);
    private String id = StringUtils.EMPTY_STRING;
    private String tag = StringUtils.EMPTY_STRING;
    private int heroIndex;
    private boolean mounted;
    private EncodeLong intimacy = new EncodeLong(0);
    private String ownerId = StringUtils.EMPTY_STRING;
    private String ownerName = StringUtils.EMPTY_STRING;
    private String keeperId = StringUtils.EMPTY_STRING;
    private String keeperName = StringUtils.EMPTY_STRING;
    private String mother = StringUtils.EMPTY_STRING;
    private String farther = StringUtils.EMPTY_STRING;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public long getLevel() {
        return level.getValue();
    }

    public void setLevel(long level) {
        this.level.setValue(level);
    }

    public String getTag() {
        return tag;
    }


    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isMounted() {
        return mounted;
    }

    public void setMounted(boolean mounted) {
        this.mounted = mounted;
    }

    public long getIntimacy() {
        return intimacy.getValue();
    }

    public void setIntimacy(long intimacy) {
        this.intimacy.setValue(intimacy);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getFarther() {
        return farther;
    }

    public void setFarther(String farther) {
        this.farther = farther;
    }

    public String getDisplayNameWithLevel() {
        return (getHp() <= 0 ? "<font color='#b4a6b0'>" : "") + getDisplayName() + " X" + getLevel() + (getHp() <= 0 ? "</font>" : "");
    }

    @Override
    public String getKeeperId() {
        return keeperId;
    }

    public void setKeeperId(String keeperId) {
        this.keeperId = keeperId;
    }

    public String getKeeperName() {
        return keeperName;
    }

    public void setKeeperName(String keeperName) {
        this.keeperName = keeperName;
    }

    @Override
    public int getHeroIndex() {
        return heroIndex;
    }

    @Override
    public void setHeroIndex(int heroIndex) {
        this.heroIndex = heroIndex;
    }
}
