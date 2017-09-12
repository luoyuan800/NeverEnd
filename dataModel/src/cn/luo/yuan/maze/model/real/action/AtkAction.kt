package cn.luo.yuan.maze.model.real.action

import cn.luo.yuan.maze.utils.Field

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/29/2017.
 */
class AtkAction(override var id: String, override var ownerId: String) : RealTimeAction {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
}