package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.Serializable;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 10/17/2017.
 */
public class CDKey implements Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private long mate;
    private long debris;
    private String id;
    private long used;
    private boolean single;

    public long getDebris() {
        return debris;
    }

    public void setDebris(long debris) {
        this.debris = debris;
    }

    public long getMate() {
        return mate;
    }

    public void setMate(long mate) {
        this.mate = mate;
    }


    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder(id);
        if(debris > 0){
            builder.append(", ");
            builder.append("碎片：").append(StringUtils.formatNumber(debris));
        }
        if(mate > 0){
            builder.append(", ");
            builder.append("锻造：").append(StringUtils.formatNumber(mate));
        }
        if(used > 0){
            builder.append(", 已使用");
        }
        return builder.toString();
    }
}
