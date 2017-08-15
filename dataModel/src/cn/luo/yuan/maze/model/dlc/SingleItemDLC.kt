package cn.luo.yuan.maze.model.dlc

import cn.luo.yuan.maze.model.IDModel
import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/15/2017.
 */
interface SingleItemDLC:DLC {
    fun getItem():IDModel?
}