package cn.luo.yuan.maze.model.skill.elementalist

import cn.luo.yuan.maze.model.Data
import cn.luo.yuan.maze.model.HarmAble
import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillAbleObject
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.DoNoThingResult
import cn.luo.yuan.maze.model.skill.result.HarmResult
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/21/2017.
 */
class ElementBomb :AtkSkill(),UpgradeAble {
     fun setLevel(level: Long) {
        this.level = level
    }

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
    private val model = ElementModel(this)
    private var level = 1L
    override fun getName(): String {
        return "元素星爆 X $level"
    }

    override fun getDisplayName(): String {
        val builder = StringBuilder()
        builder.append("用你的优雅，亮瞎敌人的双眼。当敌人的属性被你克制时，秒杀敌人！<br>")
        builder.append(StringUtils.formatPercentage(rate)).append("概率释放<br>")
        builder.append("已经使用：${StringUtils.formatNumber(useTime)}次")
        return builder.toString()
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun invoke(parameter: SkillParameter): SkillResult {
        val hrs = HarmResult()
        val monster:HarmAble = parameter[SkillParameter.TARGET]
        val hero : SkillAbleObject = parameter.owner
        if(hero is HarmAble){
            if(hero.element.restriction(monster.element)){
                hrs.harm = monster.currentHp
                hrs.addMessage("$name 生效！")
            }else{
                val rs = DoNoThingResult()
                rs.messages.add("$name 无效")
                return rs
            }
        }
        return hrs
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun upgrade(parameter: SkillParameter?): Boolean {
        if(rate + 1 < Data.RATE_MAX/5){
            rate += 1
            level ++
            return true
        }
        return false
    }

    override fun getLevel(): Long {
        return level
    }

    override fun canUpgrade(parameter: SkillParameter?): Boolean {
        return rate + 1 < Data.RATE_MAX/5 && model.canUpgrade(parameter)
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }
}