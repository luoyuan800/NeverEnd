package cn.luo.yuan.maze.server;

import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.Maze;
import cn.luo.yuan.maze.model.skill.SkillParameter;
import cn.luo.yuan.maze.persistence.DataManagerInterface;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.service.PetMonsterHelperInterface;
import cn.luo.yuan.maze.service.TaskManager;
import cn.luo.yuan.maze.utils.Random;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 7/24/2017.
 */
public class GameContext implements InfoControlInterface{
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private Random random = new Random(System.currentTimeMillis());
    @Override
    public Hero getHero() {
        return null;
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
    public PetMonsterHelperInterface getPetMonsterHelper() {
        return null;
    }

    @Override
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    @Override
    public long resetSkill(@NotNull SkillParameter sp) {
        return 0;
    }

    @Override
    public void showPopup(String msg) {

    }

    @Override
    public void postTaskInUIThread(Runnable task) {

    }

    @Override
    public void refreshHead() {

    }

    @Override
    public void showEmptyAccessoryDialog() {

    }

    @Override
    public void showInputPopup(InputListener listener, String hint) {

    }
}
