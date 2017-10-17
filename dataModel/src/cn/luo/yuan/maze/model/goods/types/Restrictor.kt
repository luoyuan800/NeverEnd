package cn.luo.yuan.maze.model.goods.types

import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/9/4.
 */
class Restrictor: UsableGoods(), InfoControlInterface.InputListener {

    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    override fun input(input: String?, context: InfoControlInterface) {
        val config = context.dataManager.loadConfig();
        if(StringUtils.isNotEmpty(input) && config!=null){
            config.catchRestrictor = input
            config.catchRestrictor = StringUtils.EMPTY_STRING
            context.dataManager.save(config)
        }
    }

    override fun perform(properties: GoodsProperties): Boolean {
        val context: InfoControlInterface? = properties[Parameter.CONTEXT] as InfoControlInterface
        if (context != null) {
            context.showInputPopup(this, "输入限定条件")
            return true;

        }
        return false
    }

    override var desc: String = "限定只捕获符合条件的宠物（和过滤器相反，并且会覆盖过滤器的作用）。" +
            "比如你只想抓‘龙’，那么输入条件‘龙’，你就只会捕获名字里面带有龙的怪物。如果要取消限定，需要再使用一个限定器，将限定条件置空。"
    override var name: String = "限定器"
    override var price: Long = 500000L

}