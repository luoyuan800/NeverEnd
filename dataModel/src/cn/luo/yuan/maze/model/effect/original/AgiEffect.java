package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.LongValueEffect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class AgiEffect extends LongValueEffect{
    private String tag;
    private static final long serialVersionUID = Field.SERVER_VERSION;
    private boolean enable = false;

    private boolean elementControl;
    @Override
    public boolean isElementControl() {
        return elementControl;
    }

    @Override
    public void setElementControl(boolean elementControl) {
        this.elementControl = elementControl;
    }
    public long getAgi() {
        return getValue();
    }

    public void setAgi(long agi) {
        setValue(agi);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }




    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "增加敏捷：" + StringUtils.formatNumber(getAgi(), false);
    }
}
