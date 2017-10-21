package cn.luo.yuan.maze.model.skill.pet

import cn.luo.yuan.maze.model.*
import cn.luo.yuan.maze.model.effect.AtkEffect
import cn.luo.yuan.maze.model.effect.DefEffect
import cn.luo.yuan.maze.model.effect.HpEffect
import cn.luo.yuan.maze.model.skill.AtkSkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.model.skill.SpecialSkill
import cn.luo.yuan.maze.model.skill.result.SkillResult
import cn.luo.yuan.maze.model.skill.result.SkipThisTurn
import cn.luo.yuan.maze.service.InfoControlInterface
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.Random
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Copyright @Luo
 * Created by Gavin Luo on 10/21/2017.
 */
class Zoarium : AtkSkill(), SpecialSkill {
    override fun getName(): String {
        return "鬼宠附身"
    }
    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    override fun enable(parameter: SkillParameter?) {
        if (model.canEnable(parameter)) {
            isEnable = true
        }
    }

    override fun canMount(parameter: SkillParameter?): Boolean {
       return model.canMount(parameter)
    }

    override fun invokeAble(parameter: SkillParameter?): Boolean {
        val owner = parameter?.owner
        return (owner is PetOwner && owner.pets.isNotEmpty()) && super.invokeAble(parameter)
    }

    override fun invoke(parameter: SkillParameter?): SkillResult {
        val owner = parameter?.owner
        val result = SkipThisTurn()
        if (owner is PetOwner && owner.pets.isNotEmpty()) {
            val random = parameter!!.get<Random>(Parameter.RANDOM)
            val pet = random.randomItem(owner.pets.toList())
            val context = parameter.get<InfoControlInterface>(Parameter.CONTEXT)
            if (pet != null && context != null && pet.race == Race.Ghosr) {
                if (owner is Hero) {
                    //移除已经存在的附身效果
                    for (effect in ArrayList(owner.effects)) {
                        if (Zoarium::class.java.simpleName.equals(effect.tag)) {
                            owner.effects.remove(effect);
                        }
                    }
                }
                result.addMessage("${pet.displayNameWithLevel}附身了${if (owner is NameObject) owner.displayName else owner.toString()}")
                var atk = pet.upperAtk * (pet.level + 1) / 100
                if (atk > Int.MAX_VALUE / 10) {
                    atk = (Int.MAX_VALUE / 10).toLong()
                }
                var def = pet.upperDef * (pet.level + 1) / 100
                if (def > Int.MAX_VALUE / 10) {
                    def = (Int.MAX_VALUE / 10).toLong()
                }
                var hp = pet.upperHp * (pet.level + 1) / 100
                if (hp > Int.MAX_VALUE / 10) {
                    hp = (Int.MAX_VALUE / 10).toLong()
                }
                val atkE = AtkEffect()
                atkE.tag = this.javaClass.simpleName
                atkE.isEnable = true
                atkE.value = atk
                if (owner is Hero) {
                    owner.effects.add(atkE)
                }
                val defE = DefEffect()
                defE.tag = this.javaClass.simpleName
                defE.isEnable = true
                defE.value = def
                if (owner is Hero) {
                    owner.effects.add(defE)
                }
                val hpE = HpEffect()
                hpE.tag = this.javaClass.simpleName
                hpE.isEnable = true
                hpE.value = hp
                if (owner is Hero) {
                    owner.effects.add(hpE)
                }
                var delay = pet.intimacy * 1000
                context.executor.schedule({
                    try {
                        if (owner is Hero) {
                            context.addMessage("${pet.displayNameWithLevel}的附身效果解除了")
                            context.diszoarium()
                            for (effect in ArrayList(owner.effects)) {
                                if (Zoarium::class.java.simpleName.equals(effect.tag)) {
                                    owner.effects.remove(effect);
                                }
                            }
                        }
                    }catch (e:Exception){
                        //Do nothing
                    }
                }, delay, TimeUnit.MILLISECONDS)
                context.zoarium(pet)
            } else {
                result.addMessage("没有随机到鬼族宠物附身")
            }
        } else {
            result.addMessage("没有宠物可以附身")
        }
        return result
    }

    override fun getSkillName(): String {
        return model.skillName
    }

    override fun getDisplayName(): String {
        return "魑魅魍魉，皆为我用！<br>技能释放的时候从当前出战的宠物中随机选择一个宠物，如果这个宠物是鬼族的话，宠物属性按比例附身到人物身上。这个比例根据宠物等级提高，宠物亲密度越高附身时间越长。<br>只有激活了宠物大师后才能使用这个技能。"
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)
    }

    private val model = PetModel(this)
}
