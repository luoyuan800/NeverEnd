package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.model.goods.Goods
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils
import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/16/2017.
 */
class RangeAward: Serializable {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    var id:String = StringUtils.EMPTY_STRING
    var range = 0
    var goods = mutableListOf<Goods>()
    var mate = 0L
    var gift = 0;
    var debris = 0

    override fun toString(): String {
        return "<b>恭喜获得层数排名第 $range</b>位<br>" +
                "获得奖励：" + (if(mate > 0) "锻造 ${StringUtils.formatNumber(mate)}" else StringUtils.EMPTY_STRING) +
                (if(gift > 0) "， 礼包 ${StringUtils.formatNumber(gift)}" else StringUtils.EMPTY_STRING) +
                (if(debris > 0) "， 碎片 ${StringUtils.formatNumber(debris)}" else StringUtils.EMPTY_STRING) +
                (if(goods.size > 0) "， 物品 ${goods}" else StringUtils.EMPTY_STRING)
    }
}