package cn.luo.yuan.maze.model.real.action

import cn.luo.yuan.maze.model.skill.DefSkill
import cn.luo.yuan.maze.utils.Field

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/6/2017.
 */
class DefSkillAction(override var id: String, override var ownerId: String, val skill:DefSkill) :RealTimeAction {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
}