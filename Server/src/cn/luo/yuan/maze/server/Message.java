package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.service.BattleMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Created by gluo on 5/26/2017.
 */
public class Message implements BattleMessage {
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
    public void hit(NameObject hero) {

    }

    @Override
    public void harm(NameObject atker, NameObject defender, long harm) {

    }

    @Override
    public void petSuppress(NameObject pet, NameObject monster) {

    }

    @Override
    public void petDefend(Pet pet) {

    }

    @Override
    public void dodge(NameObject hero, NameObject monster) {

    }

    @Override
    public void parry(NameObject hero) {

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

    public void createGroup(@NotNull NameObject hero, @NotNull NameObject other) {
        System.out.println(hero.getName() + " and " + other.getName() + " create group ");
    }

    public void groupGoToLevel(@NotNull Group group) {
        System.out.println(group.getId() + " go to " + group.getLevel());
    }
}
