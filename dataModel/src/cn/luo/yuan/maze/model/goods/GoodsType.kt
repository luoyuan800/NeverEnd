package cn.luo.yuan.maze.model.goods

import cn.luo.yuan.maze.model.goods.types.EmptyGoods
import cn.luo.yuan.maze.persistence.DataManagerInterface

/**
 * Created by gluo on 5/8/2017.
 */
enum class GoodsType(var needLoad: Boolean, var localSell: Boolean) {
    Medallion(false, false);

    var instance: Goods = EmptyGoods()
        fun getInstance(dataManager : DataManagerInterface):Goods {
            if (instance is EmptyGoods) {
                instance = dataManager.loadGoods(this);
            }
            return instance
        }
}