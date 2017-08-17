package cn.luo.yuan.maze.server.model;

import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.MessageReceiver;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.Pet;
import cn.luo.yuan.maze.model.ServerRecord;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.model.skill.SkillAbleObject;
import cn.luo.yuan.maze.service.BattleMessage;
import cn.luo.yuan.maze.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gluo on 6/20/2017.
 */
public class Messager implements BattleMessage {
    private Set<MessageReceiver> sms = new HashSet<>();

    public void addReceiver(MessageReceiver sms) {
        if (sms != null) {
            this.sms.add(sms);
        }
    }

    public void removeReceiver(MessageReceiver sms) {
        if (sms != null)
            this.sms.remove(sms);
    }

    public void notification(String msg) {
        msg = StringUtils.getCurrentTime() + " - " + msg;
        for (MessageReceiver sr : sms) {
            if (sr != null) {
                sr.receive(msg);
            }
        }
    }

    @Override
    public void battleTooLong() {
        notification("战斗时间太长，系统决定双方生命值减半！");
    }

    @Override
    public void win(NameObject winner, NameObject loser) {
        notification(winner.getDisplayName() + "击败了" + loser.getDisplayName());
    }

    @Override
    public void lost(NameObject loser, NameObject winner) {
        notification(loser.getDisplayName() + "被" + winner.getDisplayName() + "击败了");
    }

    @Override
    public void hit(NameObject hero) {
        notification(hero.getDisplayName() + " 使出了暴击，攻击伤害提高了。");
    }

    @Override
    public void harm(NameObject atker, NameObject defender, long harm) {
        notification(atker.getDisplayName() + "攻击了" + defender.getDisplayName() + "，造成了<font color='red'>" + StringUtils.formatNumber(harm, false) + "</font>点伤害。");
    }

    @Override
    public void petSuppress(NameObject pet, NameObject monster) {
        notification(pet.getDisplayName() + "被" + monster.getDisplayName() + "的霸气震慑得不敢动弹");
    }

    @Override
    public void petDefend(Pet pet) {
        notification(pet.getDisplayNameWithLevel() + "舍身救主，挡下了攻击！");
    }

    @Override
    public void dodge(NameObject hero, NameObject monster) {
        notification(hero.getDisplayName() + "身手敏捷的闪开了" + monster.getDisplayName() + "的攻击");
    }

    @Override
    public void parry(NameObject hero) {
        notification(hero.getDisplayName() + "成功进行了一次格挡，减少了受到的伤害！");
    }

    @Override
    public void silent(SkillAbleObject atker, HarmAble target, Skill skill) {
        notification((atker instanceof NameObject ? ((NameObject) atker).getDisplayName() : "") +
                "想要释放技能 <font color='red'>" + skill.getName() + "</font> 但是被" +
                (target instanceof NameObject ? ((NameObject) target).getDisplayName() : "") + "阻止了");
    }

    @Override
    public void releaseSkill(HarmAble target, Skill skill) {
        notification((target instanceof NameObject ? ((NameObject) target).getDisplayName() : "") + "使用了技能 <font color='red'>" + skill.getName() + "</font>");
    }

    @Override
    public void rowMessage(String msg) {
        notification(msg);
    }

    @Override
    public void startBattle(String hero, String monster) {
        notification("<b>" + hero + "和" + monster + "开始战斗！</b>");
    }

    @Override
    public void myTurn(String displayName) {
        notification(String.format("%s的攻击回合", displayName));
    }

    public void restore(@Nullable String hero) {
        notification(hero + "生命值已经恢复！");
    }

    public void waitingForRestore(@Nullable String hero, long period) {
        notification(hero + "已经挂掉，还要等待" + (period / 1000) + "秒才能复活！");
    }

    public void materialGet(@Nullable String geter, long awardMaterial) {
        notification(geter + "获得了" + StringUtils.formatNumber(awardMaterial, false) + "锻造点");
    }

    public void restoreLimit(@Nullable String hero) {
        notification(hero + "的重生次数已经达到上限，请取回后重新上传。");
    }

    public void speak(@Nullable String name, @Nullable String msg) {
        notification(name + ": " + msg);
    }

    public void buildGroup(@Nullable String hero_1, @NotNull String hero_2) {
        notification(hero_1 + "与" + hero_2 + "组成了一个新队伍");
    }
}
