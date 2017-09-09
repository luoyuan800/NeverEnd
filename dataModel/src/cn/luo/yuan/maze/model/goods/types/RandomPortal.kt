package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/9/4.
 */
class RandomPortal: UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        val context:InfoControlInterface? = properties[Parameter.CONTEXT] as InfoControlInterface
        if(context!=null){
            val maze = context.maze
            val level = context.random.randomRange((maze.level * 0.4f).toLong(), (maze.maxLevel * 0.8f).toLong()) + 1
            maze.level = level
            context.showPopup("传送到了第 ${StringUtils.formatNumber(level)} 层")
            return true
        }
        return false
    }

    override var desc: String = "使用后随机传送。传送范围为（当前层数40%）至（最高层数80%）之间"
    override var name: String = "随机传送"
    override var price: Long = 300000L
}