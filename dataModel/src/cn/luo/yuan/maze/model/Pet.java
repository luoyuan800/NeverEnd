package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.EncodeLong;
import cn.luo.yuan.maze.utils.Version;

import java.io.Serializable;

/**
 * Created by gluo on 4/25/2017.
 */
public class Pet extends Monster implements IDModel, Serializable {
    private static final long serialVersionUID = Version.SERVER_VERSION;
    private EncodeLong level = new EncodeLong(0);
    private String id;
    private String tag;
    private boolean mounted;
    private EncodeLong intimacy = new EncodeLong(0);
    private String ownerId;
    private String ownerName;
    private String mother;
    private String farther;

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
}
