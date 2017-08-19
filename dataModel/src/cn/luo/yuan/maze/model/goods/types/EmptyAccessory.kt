package cn.luo.yuan.maze.model.goods.types
import cn.luo.yuan.maze.model.goods.GoodsProperties
import cn.luo.yuan.maze.model.goods.UsableGoods
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.service.RangePropertiesHelper
import cn.luo.yuan.maze.utils.Field
import java.util.*
import javax.swing.text.EditorKit

/**
 *
 * Created by luoyuan on 2017/7/16.
 */
class EmptyAccessory() : UsableGoods() {
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    override fun perform(properties: GoodsProperties): Boolean {
        if (getCount() > 0) {
            val context = properties["context"] as InfoControlInterface
            val config = context.dataManager.loadConfig()
            context.showEmptyAccessoryDialog();
            return true
        }
        return false
    }

    override var desc: String = "使用后可以获得一个空白无属性的，由你命名的装备。你可以通过升级空白装备来实现装备的属性自定义！"
    override var name: String = "装备卡"
    override var price = 330000L
}