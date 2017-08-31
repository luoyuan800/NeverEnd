package cn.luo.yuan.maze.model.skill.pet

import cn.luo.yuan.maze.model.Parameter
import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter
import cn.luo.yuan.maze.service.InfoControlInterface

/**
 * Created by luoyuan on 2017/8/21.
 */
class Trainer : PropertySkill() {
    private val model = PetModel(this)
    override fun getName(): String {
        return "驯兽师"
    }

    override fun getDisplayName(): String {
        return "激活后增加捕获宠物的几率,，降低生蛋概率。不可和培育家一起激活!"
    }

    override fun enable(parameter: SkillParameter) {
        if(!isEnable) {
            val context: InfoControlInterface = parameter.get(Parameter.CONTEXT)
            val config = context.dataManager.loadConfig()
            config.peT_RATE_REDUCE -= 0.5f
            config.egG_RATE_REDUCE += 0.5f
            context.dataManager.save(config)
            isEnable = true;
        }
    }

    override fun disable(parameter: SkillParameter) {
        if(isEnable) {
            val context: InfoControlInterface = parameter.get(Parameter.CONTEXT)
            val config = context.dataManager.loadConfig()
            config.peT_RATE_REDUCE += 0.5f
            config.egG_RATE_REDUCE -= 0.5f
            context.dataManager.save(config)
            isEnable = false
        }
    }

    override fun canEnable(parameter: SkillParameter?): Boolean {
        return model.canEnable(parameter)&& !model.isSkillEnable("Foster", parameter?.get(Parameter.CONTEXT))
    }

    override fun getSkillName(): String {
        return model.skillName
    }
}