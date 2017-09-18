package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.utils.Field

/**
 * Created by luoyuan on 2017/9/4.
 */
class Evolution: Goods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    override var desc: String = "可以让伊布在亲密度不满的情况下也可以进化"
    override var name: String ="进化石"
    override var price: Long = 1000000L
    override fun canLocalSell(): Boolean {
        return false
    }
}