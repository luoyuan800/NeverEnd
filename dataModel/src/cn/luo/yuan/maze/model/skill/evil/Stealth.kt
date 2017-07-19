package cn.luo.yuan.maze.model.skill.evil

import cn.luo.yuan.maze.model.Hero
import cn.luo.yuan.maze.model.effect.original.MeetRateEffect
import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.DonothingResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Created by luoyuan on 2017/7/19.
 */
class Stealth():PropertySkill(),UpgradeAble {
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
        percent += 0.5f
        return true
    }

    override fun disable(parameter: SkillParameter?) {
        val hero = parameter!!.owner
        if(hero is Hero){
            for(effect in hero.effects.toList()){
                if(effect is MeetRateEffect && effect.meetRate == percent){
                    hero.effects.remove(effect)
                }
            }
        }
    }

    override fun invoke(parameter: SkillParameter?): SkillResult {
        val hero = parameter!!.owner
        if(hero is Hero){
            val effect = MeetRateEffect()
            effect.meetRate = percent
            hero.effects.add(effect)
        }
        return DonothingResult()
    }

    override fun getLevel(): Long {
        return level
    }

    override fun enable(parameter: SkillParameter?) {
        if(model.canEnable(parameter)){
            isEnable = true
        }
    }

    override fun getSkillName(): String {
        return model.skillName
    }
}