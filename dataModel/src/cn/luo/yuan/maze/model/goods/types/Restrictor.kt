package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods

/**
 * Created by luoyuan on 2017/9/4.
 */
class Restrictor: UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var desc: String = "限定只捕获符合条件的宠物（和过滤器相反，并且会覆盖过滤器的作用）。" +
            "比如你只想抓‘龙’，那么输入条件‘龙’，你就只会捕获名字里面带有龙的怪物。如果要取消限定，需要再使用一个限定器，将限定条件置空。"
    override var name: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var price: Long
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}