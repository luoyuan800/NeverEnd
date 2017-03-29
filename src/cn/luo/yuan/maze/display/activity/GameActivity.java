package cn.luo.yuan.maze.display.activity;

import android.app.Activity;
import android.os.Bundle;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.view.RollTextView;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.service.RunningService;

/**
 * Created by luoyuan on 2017/3/29.
 */
public class GameActivity extends Activity {
    DataManager dataManager;
    InfoControl control;
    public void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.game_layout);
        dataManager = new DataManager(savedInstanceState.getInt("index"), this);
        control = new InfoControl((RollTextView) findViewById(R.id.info_view));
        RunningService runningService = new RunningService(dataManager.loadHero(),dataManager.loadMaze(),control, dataManager, Data.REFRESH_SPEED);
        new Thread(runningService).start();
    }
}
