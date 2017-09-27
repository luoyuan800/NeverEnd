package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.maze.model.Monster
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/8/2017.
 */
class MonsterDLC : DLC , Cloneable {

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

    val monsters = mutableListOf<Monster>()
    val image = mutableListOf<ByteArray>()
    override var desc = StringUtils.EMPTY_STRING
    override var title = StringUtils.EMPTY_STRING
    override var debrisCost = 0

    override fun clone(): MonsterDLC {
        return super.clone() as MonsterDLC
    }
}
