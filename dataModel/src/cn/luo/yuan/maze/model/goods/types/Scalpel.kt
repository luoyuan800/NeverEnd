package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods

/**
 * Created by luoyuan on 2017/9/4.
 */
class Scalpel: UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var desc: String = "使用后选择一个宠物转换性别！无法选择出战中、仓库中或者未孵化的宠物。"
    override var name: String = "手术刀"
    override var price: Long = 700000
}