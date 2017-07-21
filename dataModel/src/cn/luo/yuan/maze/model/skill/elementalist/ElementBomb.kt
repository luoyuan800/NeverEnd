package cn.luo.yuan.maze.model.skill.elementalist

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/21/2017.
 */
class ElementBomb():AtkSkill(),UpgradeAble {
    private val model = ElementModel(this)
    private var level = 1L
    override fun getName(): String {
        return "元素星爆 X $level"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("用你的优雅，亮瞎敌人的双眼。当敌人的属性被你克制时，秒杀敌人！<br>")
        builder.append(StringUtils.formatPercentage(rate)).append("概率释放<br>")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun invoke(parameter: SkillParameter?): SkillResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun upgrade(parameter: SkillParameter?): Boolean {
        if(rate + 0.5 < Data.RATE_MAX/10){
            rate += 0.5f
            level ++
            return true
        }
        return false
    }

    override fun getLevel(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canUpgrade(parameter: SkillParameter?): Boolean {
        return rate + 0.5 < Data.RATE_MAX/10 && model.canUpgrade(parameter)
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }
}