package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 10/30/2017.
 */
public class RangeObject implements Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    private String head;
    private String detail;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
