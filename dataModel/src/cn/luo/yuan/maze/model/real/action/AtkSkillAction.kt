package cn.luo.yuan.maze.model.real.action

import cn.luo.yuan.maze.model.skill.AtkSkill

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/6/2017.
 */
class AtkSkillAction(override var id: String, override var ownerId: String, val skill:AtkSkill) :RealTimeAction {
}