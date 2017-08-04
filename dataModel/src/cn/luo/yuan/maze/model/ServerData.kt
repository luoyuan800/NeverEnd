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
            for (accessory in awardAccessories!!) {
                acces.append(accessory.displayName).append("<br>")
            }
        } else {
            acces.append("无")
        }
        val petb = StringBuilder()
        if (awardPets != null && awardPets!!.isNotEmpty()) {
            for (pet in awardPets!!) {
                petb.append(pet.displayName).append("<br>")
            }
        } else {
            petb.append("无")
        }
        return "锻造点：" + StringUtils.formatNumber(material, false) + "<br>" + "装备：<br>"+petb+"<br>宠物：<br>"+petb
    }

    companion object {
        private const val serialVersionUID = Field.SERVER_VERSION
    }
}
