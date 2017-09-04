package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods

/**
 * Created by luoyuan on 2017/9/4.
 */
class RandomPortal: UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var desc: String = "使用后随机传送。传送范围为（当前层数40%）至（最高层数80%）之间"
    override var name: String = "随机传送"
    override var price: Long = 300000L
}