package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.maze.model.Egg
import cn.luo.yuan.`object`.IDModel
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/15/2017.
 */
class EggDLC : SingleItemDLC, Cloneable{
    override fun getItem(): IDModel? {
        return egg
    }

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    override fun isDelete(): Boolean {
        return delete
    }

    override fun markDelete() {
        delete = true
    }

    private var delete: Boolean = false
    override var debrisCost: Int = 0
    override var title:String = StringUtils.EMPTY_STRING
    override var desc:String = StringUtils.EMPTY_STRING
    var egg : Egg?=null
    var image:ByteArray?=null

    override fun clone(): EggDLC {
        return super.clone() as EggDLC
    }



}
