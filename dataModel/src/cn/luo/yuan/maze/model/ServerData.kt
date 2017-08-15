package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.StringUtils

import java.io.Serializable

/**
 *
 * Created by luoyuan on 2017/6/24.
 */
class ServerData : Serializable {

    var mac: String? = StringUtils.EMPTY_STRING
    var hero: Hero? = null
    var maze: Maze? = null
    var accessories: List<Accessory>? = null
    var awardAccessories: List<Accessory>? = null
    var pets: List<Pet>? = null
    var awardPets: List<Pet>? = null
    var skills: List<Skill>? = null
    var material: Long = 0
    val helloMsg = mutableMapOf("meet" to StringUtils.EMPTY_STRING, "win" to StringUtils.EMPTY_STRING,
            "lost" to StringUtils.EMPTY_STRING, "risk" to StringUtils.EMPTY_STRING,
            "advantage" to StringUtils.EMPTY_STRING, "group" to StringUtils.EMPTY_STRING, "groupDie" to StringUtils.EMPTY_STRING)

    constructor()

    constructor(data: ServerData) {
        awardAccessories = data.awardAccessories
        awardPets = data.awardPets
        material = data.material
    }

    override fun toString(): String {
        val acces = StringBuilder()
        if (awardAccessories != null && awardAccessories!!.isNotEmpty()) {
            acces.append("<br>装备：<br>")
            for (accessory in awardAccessories!!) {
                acces.append(accessory.displayName).append("<br>")
            }
        }
        val petb = StringBuilder()
        if (awardPets != null && awardPets!!.isNotEmpty()) {
            petb.append("<br>宠物：<br>")
            for (pet in awardPets!!) {
                petb.append(pet.displayName).append("<br>")
            }
        }
        return "锻造点：" + StringUtils.formatNumber(material, false) + acces + petb
    }

    fun getId(): String? {
        return hero?.id
    }

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
}
