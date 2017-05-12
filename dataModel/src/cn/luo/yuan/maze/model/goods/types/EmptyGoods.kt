package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.model.goods.GoodsProperties

/**
 * Created by gluo on 5/9/2017.
 */
class EmptyGoods : Goods {
    override var price: Long
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var count: Int
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var desc: String
        get() = throw UnsupportedOperationException()
        set(value) {
        }
    override var name: String
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    override fun load(properties: GoodsProperties) {
        throw UnsupportedOperationException()
    }
}