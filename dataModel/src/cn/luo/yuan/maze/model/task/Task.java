package cn.luo.yuan.maze.model.task;

import cn.luo.yuan.maze.model.IDModel;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/1/2017.
 */
public abstract class Task implements Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    private String desc;
    private boolean finished;
    private boolean start;
    private String id;
    private String preTaskId;
    public abstract void invoke(InfoControlInterface context);

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String toString(){
        return desc + "<br>" + getAward();
    }

    public abstract String getAward();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getPreTaskId() {
        return preTaskId;
    }

    public void setPreTaskId(String preTaskId) {
        this.preTaskId = preTaskId;
    }

    public abstract List<IDModel> predecessorItems();

}
