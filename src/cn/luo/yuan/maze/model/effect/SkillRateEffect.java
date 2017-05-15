package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/15/2017.
 */
public class SkillRateEffect extends cn.luo.yuan.maze.model.effect.original.SkillRateEffect {
    public String toString(){
        return Resource.getString(R.string.skill_rate_effect) + StringUtils.DecimalFormatRound(getSkillRate(),2) + "%";
    }
}