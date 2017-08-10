package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.maze.model.IDModel
import cn.luo.yuan.maze.model.Monster
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/8/2017.
 */
class MonsterDLC : DLC, Serializable, IDModel, Cloneable {
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
    var desc = StringUtils.EMPTY_STRING
    var title = StringUtils.EMPTY_STRING
    var debrisCost = 0

    override fun getId(): String {
        return title
    }

    override fun setId(id: String) {
        title = id
    }

    override fun clone(): MonsterDLC {
        return super.clone() as MonsterDLC
    }
}
