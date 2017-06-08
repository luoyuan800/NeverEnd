package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.StringUtils;
import cn.luo.yuan.maze.utils.Field;

/**
 * Created by gluo on 4/25/2017.
 */
public class Pet extends Monster implements IDModel {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private EncodeLong level = new EncodeLong(0);
    private String id = StringUtils.EMPTY_STRING;
    private String tag = StringUtils.EMPTY_STRING;
    private boolean mounted;
    private EncodeLong intimacy = new EncodeLong(0);
    private String ownerId = StringUtils.EMPTY_STRING;
    private String ownerName = StringUtils.EMPTY_STRING;
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
        return getDisplayName() + " X" + getLevel();
    }

}
