package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.utils.Field
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
    var verify = false
    override fun toString(): String {
        return if(verify) "获得了 $gift 个礼包，和 $debris 个碎片" else "校验失败";
    }
}