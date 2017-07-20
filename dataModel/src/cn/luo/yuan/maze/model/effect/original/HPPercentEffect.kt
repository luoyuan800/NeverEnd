package cn.luo.yuan.maze.model.effect.original

import cn.luo.yuan.maze.model.effect.Effect

/**
 * Created by luoyuan on 2017/7/19.
 */
class HPPercentEffect():PercentEffect(),Cloneable {

    override fun clone(): Effect {
        try {
            return super.clone() as Effect
        } catch (e: CloneNotSupportedException) {
            return this
        }

    }
}