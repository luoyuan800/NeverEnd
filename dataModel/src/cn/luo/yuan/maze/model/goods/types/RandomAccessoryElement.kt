package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods

/**
 * Created by luoyuan on 2017/9/4.
 */
class RandomAccessoryElement: UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var desc: String = "选择一件装备，随机改变该装备的五行属性"
    override var name: String = "五行珠"
    override var price: Long = 900000
    override fun canLocalSell(): Boolean {
        return false
    }
}