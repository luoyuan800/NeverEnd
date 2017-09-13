package cn.luo.yuan.maze.model

import cn.luo.yuan.maze.model.skill.EmptySkill
import cn.luo.yuan.maze.model.skill.Skill
import cn.luo.yuan.maze.model.skill.SkillAbleObject
import cn.luo.yuan.maze.utils.Field
import cn.luo.yuan.maze.utils.Random
import java.io.Serializable

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/7/2017.
 */
class Group : HarmObject(), SkillAbleObject, NameObject, PetOwner, Serializable {
    override fun setSkills(skills: Array<out Skill>?) {
        //
    }

    override fun getRace(): Race {
        if(currentHero!=null) return currentHero!!.race else return Race.Nonsr;
    }

    override fun getId(): String {
        return if(currentHero !=null) return (currentHero as Hero).id else ""
    }

    override fun getElementRate(): Float {
        return if(currentHero!=null) (currentHero as Hero).elementRate else 0.5f
    }

    companion object {
        private const val serialVersionUID: Long = Field.SERVER_VERSION
    }

    var currentHero: Hero? = null
    val heroes = mutableListOf<Hero>()

    override fun getName(): String {
        return if (currentHero != null) (currentHero as Hero).name else groupName()
    }

    override fun getPets(): MutableCollection<Pet> {
        return if (currentHero != null) (currentHero as Hero).pets else mutableListOf()
    }

    fun groupName(): String {
        return heroes.joinToString {
            it.name
        }
    }

    fun groupDisplayName(): String {
        return heroes.joinToString {
            it.displayName
        }
    }

    override fun getDisplayName(): String {
        return if (currentHero != null) (currentHero as Hero).displayName else groupDisplayName()
    }

    override fun getAtk(): Long {
        return if (currentHero != null) (currentHero as Hero).atk else 0L
    }

    override fun getDef(): Long {
        return if (currentHero != null) (currentHero as Hero).def else 0L
    }

    override fun getHp(): Long {
        return if (currentHero != null) (currentHero as Hero).hp else 0L
    }

    override fun setHp(hp: Long) {
        if (currentHero != null) (currentHero as Hero).hp = hp
    }

    override fun getMaxHp(): Long {
        return if (currentHero != null) (currentHero as Hero).maxHp else 0L
    }

    override fun getElement(): Element {
        return if (currentHero != null) (currentHero as Hero).element else Element.NONE
    }

    override fun getSkills(): Array<Skill> {
        return if (currentHero != null) (currentHero as Hero).skills else arrayOf(EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL, EmptySkill.EMPTY_SKILL)
    }

    override fun getUpperAtk(): Long {
        return if (currentHero != null) (currentHero as Hero).upperAtk else 0L
    }

    override fun getUpperDef(): Long {
        return if (currentHero != null) (currentHero as Hero).upperDef else 0L
    }

    override fun getUpperHp(): Long {
        return if (currentHero != null) (currentHero as Hero).upperHp else 0L
    }

    override fun getCurrentHp(): Long {
        return if (currentHero != null) (currentHero as Hero).currentHp else {
            var currentHp = 0L
            for(hero in heroes){
                currentHp += hero.currentHp
            }
            return currentHp
        }
    }

    override fun isDodge(random: Random): Boolean {
        return random.nextLong(100) + random.nextLong(((if(currentHero!=null) (currentHero as Hero).agi else 0L) * Data.DODGE_AGI_RATE).toLong()) >
                97 + random.nextInt(100).toLong() + random.nextLong(((if(currentHero!=null) (currentHero as Hero).str else 0L) * Data.DODGE_STR_RATE).toLong())
    }

    override fun isHit(random: Random): Boolean {
        return random.nextLong(100) + (if(currentHero!=null) (currentHero as Hero).str else 0L) * Data.HIT_STR_RATE >
                97 +random.nextInt(100).toLong() +
                random.nextLong(((if(currentHero!=null) (currentHero as Hero).agi else 0L) * Data.HIT_AGI_RATE).toLong())
    }

    override fun isParry(random: Random): Boolean {
        return random.nextLong(100) + (if(currentHero!=null) (currentHero as Hero).str else 0L) * Data.PARRY_STR_RATE >
                97 + random.nextInt(100).toLong() + random.nextLong(((if(currentHero!=null) (currentHero as Hero).agi else 0L) * Data.PARRY_AGI_RATE).toLong())
    }

    fun roll(random: Random) {
        currentHero = random.randomItem(heroes.filter { it.currentHp > 0 })
    }

    fun reset(){
        currentHero = null
    }

    fun totalHp():Long{
        val hp = heroes
                .map { it.currentHp }
                .sum();
        return hp;
    }
}