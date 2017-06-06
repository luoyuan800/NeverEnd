package cn.luo.yuan.maze.model.goods

import cn.luo.yuan.maze.model.IDModel
import java.io.Serializable

/**
 * Created by gluo on 5/5/2017.
 */
interface Goods : Serializable, IDModel {

    var count:Int
    var desc : String
    var name : String
    var price: Long
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