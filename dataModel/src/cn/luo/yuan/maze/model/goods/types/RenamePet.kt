package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods

/**
 * Created by luoyuan on 2017/9/4.
 */
class RenamePet : UsableGoods() {
    override fun perform(properties: GoodsProperties): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var desc: String = "使用后选择一只宠物自定义前缀名。"
    override var name: String = "宠物前缀"
    override var price: Long = 5000000
    override fun canLocalSell(): Boolean {
        return false
    }
}