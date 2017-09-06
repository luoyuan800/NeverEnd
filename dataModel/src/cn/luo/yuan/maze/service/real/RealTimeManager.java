package cn.luo.yuan.maze.service.real;

import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.real.RealTimeState;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.Skill;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/6/2017.
 */
public interface RealTimeManager {
    void ready();
    void atkAction();
    RealTimeState pollState();
    void useGoodsAction(Goods goods);
    void useAtkSkillAction(AtkSkill skill);
    void useDefSkillAction(DefSkill skill);
}
