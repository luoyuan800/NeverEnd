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
    private String version;
    private String head;
    private String sign;
    private boolean exception;
    private boolean longKiller;
    private boolean elementer;

    private  long MATERIAL_LIMIT = 3000000;//如果携带超过这个数量的锻造，就增加商店的价格和怪物的攻击

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
}
