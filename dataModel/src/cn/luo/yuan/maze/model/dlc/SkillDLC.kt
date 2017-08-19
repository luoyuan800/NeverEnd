package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.maze.model.IDModel
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/15/2017.
 */
class SkillDLC : SingleItemDLC, Cloneable {
    override fun getItem(): IDModel? {
        return skill
    }

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }

    var skill: Skill? = null
    private var delete = false
    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }

    override var title: String = StringUtils.EMPTY_STRING

    override var desc: String = StringUtils.EMPTY_STRING

    override var debrisCost: Int = 0

    override fun clone(): DLC {
        return super.clone() as SkillDLC
    }

}
