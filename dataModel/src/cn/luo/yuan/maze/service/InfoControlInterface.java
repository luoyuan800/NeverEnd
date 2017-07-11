package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.persistence.DataManagerInterface;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;


/**
 * Created by gluo on 5/12/2017.
 */
public interface InfoControlInterface {
    Hero getHero();

    Random getRandom();

    Maze getMaze();

    void addMessage(String msg);

    void stopGame();

    DataManagerInterface getDataManager();
    TaskManager getTaskManager();
    PetMonsterHelperInterface getPetMonsterHelper();

    ScheduledExecutorService getExecutor();

    long resetSkill(@NotNull SkillParameter sp);
}
