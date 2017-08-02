package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by luoyuan on 2017/8/2.
 */
public class RestoreHPPercentEffect extends PercentEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public String toString(){
        return "增加生命恢复：" + StringUtils.formatPercentage(getValue());
    }
}
