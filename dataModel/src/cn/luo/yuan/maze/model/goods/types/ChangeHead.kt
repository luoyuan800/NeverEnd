package cn.luo.yuan.maze.model.goods.types
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.service.RangePropertiesHelper
import cn.luo.yuan.maze.utils.Field
import java.util.*

/**
 *
 * Created by luoyuan on 2017/7/16.
 */
class ChangeHead() : UsableGoods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    override fun perform(properties: GoodsProperties): Boolean {
        if (getCount() > 0) {
            val context = properties["context"] as InfoControlInterface
            val config = context.dataManager.loadConfig()
            config.head = context.random.randomItem(Arrays.asList("Actor1_1.png", "Actor1_4.png", "Actor1_6.png",
                    "Actor2_1.png","Actor1_3.png", "Actor2_2.png", "Actor2_5.png", "Actor2_6.png",
                    "Actor1_2.png", "Actor3_1.png", "Actor3_6.png", "Actor3_2.png", "Actor2_4.png"))
            context.dataManager.save(config);
            context.refreshHead();
            return true
        }
        return false
    }

    override var desc: String = "使用后更换一个随机形象。"
    override var name: String = "造型"
    override var price = 330000L
}