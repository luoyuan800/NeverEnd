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
class Filter : UsableGoods(), InfoControlInterface.InputListener {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }
    override fun input(input: String?, context:InfoControlInterface) {
        val config = context.dataManager.loadConfig();
        if(StringUtils.isNotEmpty(input) && config!=null){
            config.catchFilter = input
            config.catchRestrictor = StringUtils.EMPTY_STRING
            context.dataManager.save(config)
        }
    }

    override fun perform(properties: GoodsProperties): Boolean {
        val context: InfoControlInterface? = properties[Parameter.CONTEXT] as InfoControlInterface
        if (context != null) {
            context.showInputPopup(this, "输入过滤条件")
            return true;

        }
        return false
    }

    override var desc: String = "使用后可以设计一个捕捉过滤条件（和限定器相反，并且会覆盖限定器的效果），这样你就不会捕捉符合条件的怪物。" +
            "比如你不想捕捉‘蟑螂’，那么可以在弹出窗口中输入‘蟑螂’之后确定，这样你就不会抓到蟑螂了。" +
            "修改之后永久生效，如果要取消过滤，需要重新使用一个过滤器将条件重置为空。"
    override var name: String = "过滤器"
    override var price: Long = 500000L
}