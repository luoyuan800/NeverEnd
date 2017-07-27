package cn.luo.yuan.maze.model.skill.elementalist

import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.utils.Field

/**
 * Created by luoyuan on 2017/7/20.
 */
class Elementalist() : PropertySkill() {
    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    private val model = ElementModel(this)
    override fun getName(): String {
        return "元素使"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("是时候展现你的风度了！激活这个技能，你的五行相生相克效果翻倍！不可以和勇者、魔王系技能同时激活。")
        return builder.toString()
    }

    override fun disable(parameter: SkillParameter?) {
        val hero = parameter!!.owner
        if (hero is Hero) {
            hero.elementRate /= 2
        }
        isEnable = false
    }

    override fun enable(parameter: SkillParameter?) {
        val hero = parameter!!.owner
        if (hero is Hero) {
            hero.elementRate *= 2
        }
        isEnable = true
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }
}