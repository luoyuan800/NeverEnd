package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.MathUtils;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/21/2017.
 */
public class HPPercentEffect extends PercentEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public String toString(){
        return "增加基础生命：" + StringUtils.formatPercentage(getValue());
    }
    @Override
    public boolean isReject(Class<? extends Effect> effectClass) {
        return effectClass == AgiEffect.class || effectClass == AtkPercentEffect.class || effectClass == AtkEffect.class || effectClass == DefEffect.class || effectClass == DefPercentEffect.class;
    }
}
