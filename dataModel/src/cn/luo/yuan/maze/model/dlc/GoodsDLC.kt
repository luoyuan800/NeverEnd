package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.`object`.IDModel
import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/15/2017.
 */
class GoodsDLC : SingleItemDLC, Cloneable {
    override fun getItem(): IDModel? {
        return goods
    }

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }

    var goods: Goods? = null
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
        return super.clone() as GoodsDLC
    }

}
