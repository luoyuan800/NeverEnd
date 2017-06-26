package cn.luo.yuan.maze.server.model;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.server.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gluo on 6/20/2017.
 */
public class Messager extends Message {
    private Set<ServerRecord> sms = new HashSet<>();
    public void addReceiver(ServerRecord sms){
        this.sms.add(sms);
    }
    public void removeReceiver(ServerRecord sms){
        this.sms.remove(sms);
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

    @Override
    public void createGroup(@NotNull NameObject hero, @NotNull NameObject other) {

    }

    @Override
    public void groupGoToLevel(@NotNull Group group) {

    }

    @Override
    public void joinGroup(@Nullable String join, @Nullable String groupName) {

    }

    public void heroChallengeGroup(@NotNull String hero, @NotNull String group) {

    }

    @Override
    public void groupBattle(@NotNull String group_1, @NotNull String group_2) {

    }

    @Override
    public void groupBattler(@Nullable String group_hero) {

    }

    @Override
    public void heroWinGroup(@Nullable String hero, @NotNull String groupName) {

    }

    @Override
    public void groupWinHero(@NotNull String groupName, @Nullable String heroName) {

    }
}
