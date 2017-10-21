package cn.luo.yuan.maze.model.effect;

import cn.luo.yuan.maze.utils.Field;

/**
 * Created by gluo on 5/15/2017.
 */
public class SkillRateEffect extends cn.luo.yuan.maze.model.effect.original.SkillRateEffect {
    private static final long serialVersionUID = Field.SERVER_VERSION;

    public cn.luo.yuan.maze.model.effect.original.SkillRateEffect covertToOriginal() {
        cn.luo.yuan.maze.model.effect.original.SkillRateEffect effect = new cn.luo.yuan.maze.model.effect.original.SkillRateEffect();
        effect.setSkillRate(getSkillRate());
        return effect;
    }
}
