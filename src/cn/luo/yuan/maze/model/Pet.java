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
}