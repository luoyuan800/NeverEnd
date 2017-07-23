package cn.luo.yuan.maze.model.skill.pet

import cn.luo.yuan.maze.model.skill.PropertySkill
import cn.luo.yuan.maze.model.skill.SkillParameter

/**
 * Created by luoyuan on 2017/7/23.
 */
class PetMonster:PropertySkill() {
    override fun getName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDisplayName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enable(parameter: SkillParameter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disable(parameter: SkillParameter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSkillName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val model = PetModel(this)
}