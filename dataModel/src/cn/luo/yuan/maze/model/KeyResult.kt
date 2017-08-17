package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils
import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/16/2017.
 */
class KeyResult : Serializable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    var gift = 0
    var debris =0
    var mate= 0L
    var verify = false
    override fun toString(): String {
        return if(verify) "获得了 锻造 ${StringUtils.formatNumber(mate)} ${if(gift > 0) ", $gift 个礼包" else ""} ${if(debris > 0) ", $debris 个碎片" else ""}" else "该兑换码已经使用过了！";
    }
}