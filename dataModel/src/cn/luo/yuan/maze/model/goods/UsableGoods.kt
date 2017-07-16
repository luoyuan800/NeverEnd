package cn.luo.yuan.maze.model.goods

/**
 * Created by gluo on 5/5/2017.
 */
abstract class UsableGoods : Goods() {
    override fun use(properties: GoodsProperties): Boolean {
        if(perform(properties)) {
            return super.use(properties)
        }else{
            return false
        }
    }

    abstract fun perform(properties: GoodsProperties):Boolean
}