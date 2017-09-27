package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.`object`.IDModel
import cn.luo.yuan.maze.model.NPCLevelRecord
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/22/2017.
 */
class NPCDLC : SingleItemDLC, Cloneable {
    override fun getItem(): IDModel? {
        return npc
    }

    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }

    private var delete: Boolean = false

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }

    override var desc = StringUtils.EMPTY_STRING
    override var title = StringUtils.EMPTY_STRING
    override var debrisCost = 0
    var npc:NPCLevelRecord? = null

    override fun clone(): NPCDLC {
        return super.clone() as NPCDLC
    }
}