package cn.luo.yuan.maze.model;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

public class SellItem implements Serializable {
    private static final long serialVersionUID = Field.SERVER_VERSION;

        public String author;
        public String name;
        public int count;
        public long price;
        public Object instance;
        public boolean special;
        public String type;
        public List<Effect> effects;
        public String desc;
        public String color;
    @NotNull
    public String id;

    public String toString() {
            if (effects == null)
                return name + " * " + count + (special ? " 特价" : " 价格") + " : " + StringUtils.formatNumber(price) + "<br>" + desc;
            return "<font color='" + color + "'>" + name + "</font>"+ " * "
                    + count + (special ? " 特价" : " 价格") + " : " + StringUtils.formatNumber(price)
                    + (StringUtils.isNotEmpty(author) ? "<br>" + author : "")
                    + (StringUtils.isNotEmpty(desc) ? "<br>" + desc : "")
                    + "<br>" + StringUtils.formatEffectsAsHtml(effects);
        }
}