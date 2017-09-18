package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.utils.Field

/**
 * Created by luoyuan on 2017/9/4.
 */
class Mirror: Goods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    override var desc: String = "爱美人士必备，爬楼再辛苦也要保持形象哦。"
    override var name: String = "镜子"
    override var price: Long = 10000000L
    override fun canLocalSell(): Boolean {
        return false
    }
}