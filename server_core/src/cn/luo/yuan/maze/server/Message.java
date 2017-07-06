package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.server.model.Group;
import cn.luo.yuan.maze.service.BattleMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by gluo on 5/26/2017.
 */
public abstract class Message implements BattleMessage {

    public abstract void createGroup(@NotNull NameObject hero, @NotNull NameObject other) ;

    public abstract void groupGoToLevel(@NotNull Group group) ;

    public abstract void joinGroup(@Nullable String join, @Nullable String groupName);
    public abstract void heroChallengeGroup(@NotNull String hero, @NotNull String group);

    public abstract void groupBattle(@NotNull String group_1, @NotNull String group_2);

    public abstract void groupBattler(@Nullable String group_hero);

    public abstract void heroWinGroup(@Nullable String hero, @NotNull String groupName) ;

    public abstract void groupWinHero(@NotNull String groupName, @Nullable String heroName) ;
}
