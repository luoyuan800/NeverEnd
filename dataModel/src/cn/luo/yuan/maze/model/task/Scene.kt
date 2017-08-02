package cn.luo.yuan.maze.model.task

import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils
import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/1/2017.
 */
class Scene : Serializable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    var taskId :String = StringUtils.EMPTY_STRING;
    var order = 0
    var leftPic:Array<Byte>? = null
    var rightPic:Array<Byte>? = null
    val msg = mutableListOf<String>()
}