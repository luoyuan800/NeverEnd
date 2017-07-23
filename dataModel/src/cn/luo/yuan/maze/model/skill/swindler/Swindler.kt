package cn.luo.yuan.maze.model.skill.swindler

import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/7/23.
 */
class Swindler: PropertySkill() {
    private val model = SwindlerModel(this)
    override fun getName(): String {
        return "欺诈师"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("机会还是有的，只要你脸皮厚。<br>每当抛硬币的时候，如果抛出的硬币为反面的话，还可以不要脸的再抛一次。<br>")
        return builder.toString()
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }

    override fun disable(parameter: SkillParameter?) {
        isEnable = false
    }

    override fun getSkillName(): String {
        return model.skillName
    }
}