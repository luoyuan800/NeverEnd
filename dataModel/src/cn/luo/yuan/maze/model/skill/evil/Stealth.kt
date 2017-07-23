package cn.luo.yuan.maze.model.skill.evil

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.effect.original.MeetRateEffect
import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.DonothingResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/7/19.
 */
class Stealth():PropertySkill(),UpgradeAble {
    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    private var level = 1L
    private val model = EvilModel(this)
    var percent = 5F
    override fun getName(): String {
        return "隐身 X $level"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("你看我不到，你看我不到，你看我不到……<br>")
        builder.append("降低")
        builder.append(StringUtils.formatPercentage(percent)).append("的遇怪概率。")
        return builder.toString()
    }

    override fun upgrade(parameter: SkillParameter?): Boolean {
        model.upgrade(parameter)
        level ++
        val context:InfoControlInterface = parameter!![SkillParameter.CONTEXT]
        val maze = context.maze
        maze.meetRate -= percent
        percent += 0.5f
        maze.meetRate += percent
        return true
    }

    override fun canUpgrade(parameter: SkillParameter?): Boolean {
        return percent < Data.RATE_MAX/2 && isUpgradePointEnough(parameter)
    }

    override fun disable(parameter: SkillParameter?) {
        val context:InfoControlInterface = parameter!![SkillParameter.CONTEXT]
        val maze = context.maze
        maze.meetRate -= percent
        isEnable = false
    }

    override fun getLevel(): Long {
        return level
    }

    override fun enable(parameter: SkillParameter?) {
        if(model.canEnable(parameter)){
            val context:InfoControlInterface = parameter!![SkillParameter.CONTEXT]
            val maze = context.maze
            maze.meetRate += percent
            isEnable = true
        }
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }
}