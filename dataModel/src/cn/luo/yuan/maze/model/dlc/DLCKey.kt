package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils
import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/8/2017.
 */
class DLCKey:Serializable {
    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    var id = StringUtils.EMPTY_STRING
    var type = StringUtils.EMPTY_STRING
    var cost:Int = 0

    override fun toString(): String {
        val typeStr = when(type){
            MonsterDLC::class.java.simpleName -> "怪物扩展包"
            SkillDLC::class.java.simpleName -> "技能扩展包"
            else -> ""
        }
        return "($typeStr) $id - ${StringUtils.formatNumber(cost)}碎片"
    }
}