package cn.luo.yuan.maze.model.goods

import cn.luo.yuan.maze.utils.Field

/**
 * Created by gluo on 5/5/2017.
 */
abstract class UsableGoods : Goods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    override fun use(properties: GoodsProperties): Boolean {
        if(perform(properties)) {
            return super.use(properties)
        }else{
            return false
        }
    }

    abstract fun perform(properties: GoodsProperties):Boolean
}