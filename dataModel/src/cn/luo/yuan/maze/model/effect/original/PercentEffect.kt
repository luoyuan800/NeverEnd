package cn.luo.yuan.maze.model.effect.original

import cn.luo.yuan.maze.model.effect.Effect
import cn.luo.yuan.maze.model.effect.FloatValueEffect
import cn.luo.yuan.maze.model.effect.LongValueEffect
import cn.luo.yuan.maze.utils.MathUtils

/**
 * Created by luoyuan on 2017/7/19.
 */
abstract class PercentEffect: LongValueEffect {
    var percent = 0F
    var additionValue = 0L
    private var enable = true
    private var elementControl: Boolean = false
    override fun isElementControl(): Boolean {
        return elementControl
    }

    override fun setElementControl(elementControl: Boolean) {
        this.elementControl = elementControl
    }
    override fun getValue(): Long {
        return additionValue
    }

    override fun setValue(value: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setValue(base:Long, percent:Float){
        this.percent = percent
        this.additionValue = (base * (percent/100f)).toLong()
    }

    override fun isEnable(): Boolean {
        return enable
    }

    override fun setEnable(enable: Boolean) {
        this.enable = enable
    }


}