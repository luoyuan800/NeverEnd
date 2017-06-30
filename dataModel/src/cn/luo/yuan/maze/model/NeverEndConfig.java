package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;

/**
 * Created by gluo on 6/30/2017.
 */
public class NeverEndConfig implements IDModel, Serializable {
    private String id;
    private boolean delete;
    private int theme = 0;
    private static final long serialVersionUID = Field.SERVER_VERSION;

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
}