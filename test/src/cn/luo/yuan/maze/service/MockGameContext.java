package cn.luo.yuan.maze.service;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.persistence.DataManagerInterface;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by gluo on 6/30/2017.
 */
public class MockGameContext implements InfoControlInterface {
    public Random random = new Random(System.currentTimeMillis());
    public Hero hero;
    @Override
    public Hero getHero() {
        return hero;
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public Maze getMaze() {
        return null;
    }

    @Override
    public void addMessage(String msg) {

    }

    @Override
    public void stopGame() {

    }

    @Override
    public DataManagerInterface getDataManager() {
        return null;
    }

    @Override
    public TaskManager getTaskManager() {
        return null;
    }

    @Override
    public PetMonsterHelperInterface getPetMonsterHelper() {
        return null;
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return null;
    }

    @Override
    public long resetSkill(@NotNull SkillParameter sp) {
        return 0;
    }

    @Override
    public void showPopup(String msg) {

    }
}
