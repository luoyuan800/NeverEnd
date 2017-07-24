package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/24/2017.
 */
public class MockBattleMessage implements BattleMessage {
    private void println(String format, Object...args){
        System.out.println(String.format(format, args));
    }
    @Override
    public void battleTooLong() {

    }

    @Override
    public void win(NameObject winner, NameObject loser) {

    }

    @Override
    public void lost(NameObject loser, NameObject winner) {

    }

    @Override
    public void petSuppress(NameObject pet, NameObject monster) {

    }

    @Override
    public void petDefend(Pet pet) {

    }

    @Override
    public void silent(SkillAbleObject atker, HarmAble target, Skill skill) {

    }

    @Override
    public void releaseSkill(HarmAble target, Skill skill) {

    }

    @Override
    public void rowMessage(String msg) {

    }

    @Override
    public void startBattle(String hero, String monster) {

    }

    @Override
    public void hit(NameObject hero) {
        println("hit %s", hero.getDisplayName());
    }

    @Override
    public void dodge(NameObject hero, NameObject monster) {
        println(" %s doge from %s", hero.getDisplayName(), monster.getDisplayName());
    }

    @Override
    public void parry(NameObject hero) {
        println("parry: %s", hero);
    }

    @Override
    public void harm(NameObject atker, NameObject defender, long harm) {
        println("%s atk %s, harm: %s", atker.getDisplayName(), defender.getDisplayName(), harm);
    }
}
