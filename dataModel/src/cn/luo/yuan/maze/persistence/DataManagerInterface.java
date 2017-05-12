package cn.luo.yuan.maze.persistence;

import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.model.goods.GoodsType;

/**
 * Created by gluo on 5/12/2017.
 */
public interface DataManagerInterface {
    Goods loadGoods(GoodsType type);
}
