package cn.luo.yuan.maze.model.skill.pet

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.SpecialSkill
import cn.luo.yuan.maze.model.skill.UpgradeAble
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn
import cn.luo.yuan.maze.service.BattleMessage
import cn.luo.yuan.maze.service.BattleServiceBase
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

/**
 * Copyright @Luo
 * Created by Gavin Luo on 9/30/2017.
 */
class Mass : AtkSkill(), UpgradeAble, SpecialSkill {
    init {
        rate = 1f
    }
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    private var level: Long = 1
    override fun canUpgrade(parameter: SkillParameter?): Boolean {
        return model.canUpgrade(parameter)
    }

    override fun upgrade(parameter: SkillParameter?): Boolean {
        if (rate < Data.RATE_MAX / 10) {
            level++
            rate += 0.2f
            return true;
        }
        return false
    }

    override fun getLevel(): Long {
        return level
    }

    private val model = PetModel(this)
    override fun getName(): String {
        return "群殴 X $level"
    }

    override fun enable(parameter: SkillParameter?) {
        isEnable = true
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
        return model.canMount(parameter)
    }

    override fun invoke(parameter: SkillParameter?): SkillResult {
        val context = parameter?.get<InfoControlInterface>(Parameter.CONTEXT)
        val hero = parameter?.owner
        val msg = parameter?.get<BattleMessage>(Parameter.MESSAGE)
        val target = parameter?.get<HarmAble>(Parameter.TARGET)
        if (hero is Hero && target != null && context != null) {
            for (pet in hero.pets) {
                if(pet.intimacy > 15 && pet.currentHp > 0) {
                    val harm = BattleServiceBase.getHarm(pet, target, 1, context.random, msg)
                    if (target is NameObject) {
                        msg?.harm(pet, target, harm)
                    }
                    target.hp -= harm
                }else{
                    msg?.rowMessage(pet.displayNameWithLevel + "不想听你的指挥")
                }
            }
        }
        return SkipThisTurn()
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun getDisplayName(): String {

        val builder = StringBuilder()
        builder.append("命令队伍中的所有宠物攻击敌人。")
        builder.append(StringUtils.formatPercentage(rate))
        builder.append("的概率释放。")
        builder.append("<br>已经使用：${StringUtils.formatNumber(useTime)}次")
        return builder.toString()
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }
}