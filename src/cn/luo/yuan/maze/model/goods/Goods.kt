package cn.luo.yuan.maze.model.goods

import java.io.Serializable

/**
 * Created by gluo on 5/5/2017.
 */
interface Goods : Serializable {
    var count:Int;
    var desc : String
    var name : String
    fun use(properties: GoodsProperties): Boolean {
        if(count > 0) {
            count--;
            return true
        }else {
            return false;
        }
    }
    fun load(properties: GoodsProperties)
}