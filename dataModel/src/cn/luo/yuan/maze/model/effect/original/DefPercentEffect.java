package cn.luo.yuan.maze.model.effect.original;

import cn.luo.yuan.maze.model.effect.Effect;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/21/2017.
 */
public class DefPercentEffect extends PercentEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;
    public String toString(){
        return "增加基础防御：" + StringUtils.formatPercentage(getValue());
    }

    @Override
    public boolean isReject(Class<? extends Effect> effectClass) {
        return effectClass == StrEffect.class || effectClass == HPPercentEffect.class || effectClass == HpEffect.class || effectClass == AtkEffect.class || effectClass == AtkPercentEffect.class;
    }
}
