package cn.luo.yuan.maze.client.display.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.Path;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.dialog.RealBattleDialog;
import cn.luo.yuan.maze.client.display.handler.ViewHandler;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.RemoteRealTimeManager;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.LevelRecord;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.real.NoDebrisState;
import cn.luo.yuan.maze.model.real.RealTimeState;
import cn.luo.yuan.maze.model.real.level.ElyosrRealLevel;
import cn.luo.yuan.maze.model.real.level.EvilerRealLevel;
import cn.luo.yuan.maze.model.real.level.GhosrRealLevel;
import cn.luo.yuan.maze.model.real.level.NonsrRealLevel;
import cn.luo.yuan.maze.model.real.level.OrgerRealLevel;
import cn.luo.yuan.maze.model.real.level.WizardsrRealLevel;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by luoyuan on 2017/9/11.
 */
public class PalaceActivity extends BaseActivity {
    private NeverEnd gameContext;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    private RestConnection server;
    private ProgressDialog progress;
    private RealBattleDialog battleDialog;

    public void onExit(View view) {
        hideProgress();
        if (battleDialog != null) {
            battleDialog.stop();
        }
        this.finish();
    }

    public void rangeBattle(View view) {
        final RemoteRealTimeManager manager = new RemoteRealTimeManager(server, gameContext);
        showProgressDialog(Resource.getString(R.string.ranging));
        progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection con = server.getHttpURLConnection(Path.REAL_BATTLE_QUIT, RestConnection.POST);
                            con.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
                            server.connect(con);
                        } catch (IOException e) {
                            LogHelper.logException(e, "Quit wait");
                        }
                    }
                });
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean stop = false;
                while (!stop && progress.isShowing()) {
                    RealTimeState state = manager.pollState();
                    if(state!=null) {
                        if (state instanceof NoDebrisState) {
                            gameContext.showPopup(Resource.getString(R.string.not_debris));
                        } else {
                            battleDialog = new RealBattleDialog(manager, gameContext);
                        }
                        stop = true;
                        hideProgress();
                    }
                }
            }
        });
    }

    public void updateLevel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    HttpURLConnection con = server.getHttpURLConnection(Path.POLL_REAL_RECORD, RestConnection.POST);
                    con.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
                    final Object o = server.connect(con);
                    if (o instanceof LevelRecord) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ImageView) findViewById(R.id.hero_pic)).setImageDrawable(
                                        Resource.loadImageFromAssets(((LevelRecord) o).getHead(), true));
                                String realLevel = StringUtils.formatRealLevel(((LevelRecord) o).getPoint(), gameContext.getHero().getRace());
                                ViewHandler.setText((TextView) findViewById(R.id.online_palace_level),
                                        realLevel);
                                ViewHandler.setText((TextView) findViewById(R.id.hero_name), gameContext.getHero().getDisplayName());
                                String percent = ((LevelRecord) o).getWin() + ((LevelRecord) o).getLost() > 0 ? StringUtils.formatPercentage(((LevelRecord) o).getWin() * 100 / (((LevelRecord) o).getWin() + ((LevelRecord) o).getLost())) : "0%";
                                ViewHandler.setText((TextView) findViewById(R.id.online_palace_rate), percent);
                                boolean isUpgrade = false;
                                boolean isDowngrade = false;
                                switch (gameContext.getHero().getRace()) {
                                    case Wizardsr:
                                        isUpgrade = WizardsrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPoint(), ((LevelRecord) o).getPriorPoint());
                                        isDowngrade = WizardsrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPriorPoint(), ((LevelRecord) o).getPoint());
                                        break;
                                    case Nonsr:
                                        isUpgrade = NonsrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPoint(), ((LevelRecord) o).getPriorPoint());
                                        isDowngrade = NonsrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPriorPoint(), ((LevelRecord) o).getPoint());
                                        break;
                                    case Ghosr:
                                        isUpgrade = GhosrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPoint(), ((LevelRecord) o).getPriorPoint());
                                        isDowngrade = GhosrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPriorPoint(), ((LevelRecord) o).getPoint());
                                        break;
                                    case Orger:
                                        isUpgrade = OrgerRealLevel.Companion.isLevelUp(((LevelRecord) o).getPoint(), ((LevelRecord) o).getPriorPoint());
                                        isDowngrade = OrgerRealLevel.Companion.isLevelUp(((LevelRecord) o).getPriorPoint(), ((LevelRecord) o).getPoint());
                                        break;
                                    case Eviler:
                                        isUpgrade = EvilerRealLevel.Companion.isLevelUp(((LevelRecord) o).getPoint(), ((LevelRecord) o).getPriorPoint());
                                        isDowngrade = EvilerRealLevel.Companion.isLevelUp(((LevelRecord) o).getPriorPoint(), ((LevelRecord) o).getPoint());
                                        break;
                                    case Elyosr:
                                        isUpgrade = ElyosrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPoint(), ((LevelRecord) o).getPriorPoint());
                                        isDowngrade = ElyosrRealLevel.Companion.isLevelUp(((LevelRecord) o).getPriorPoint(), ((LevelRecord) o).getPoint());
                                        break;
                                }
                                if (isUpgrade) {
                                    gameContext.showPopup(String.format("恭喜升级为%s", realLevel));
                                } else if (isDowngrade) {
                                    gameContext.showPopup(String.format("抱歉降级为%s", realLevel));
                                }
                            }
                        });
                    }
                    hideProgress();
                } catch (IOException e) {
                    LogHelper.logException(e, "poll record!");
                }
            }
        });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_palcace);
        gameContext = (NeverEnd) getApplication();
        gameContext.setContext(this);
        Resource.init(this);
        server = new RestConnection(Field.SERVER_URL, getVersion(), Resource.getSingInfo());
        submitRecord();
    }

    private void submitRecord() {
        showProgressDialog("");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection con = server.getHttpURLConnection(Path.UPDATE_REAL_RECORD, RestConnection.POST);
                    con.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
                    LevelRecord record = new LevelRecord(gameContext.getHero());
                    NeverEndConfig config = gameContext.getDataManager().loadConfig();
                    if (config != null) {
                        record.setHead(config.getHead());
                    }
                    final Object o = server.connect(record, con);
                    if (o.equals(Field.RESPONSE_RESULT_OK)) {
                        updateLevel();
                    }
                } catch (IOException e) {
                    LogHelper.logException(e, "poll record!");
                }
            }
        });
    }

    private void showProgressDialog(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                } else if (progress == null) {
                    progress = new ProgressDialog(PalaceActivity.this);
                }
                if(StringUtils.isNotEmpty(tip)){
                    progress.setMessage(tip);
                }
                progress.setOnDismissListener(null);
                progress.show();
            }
        });
    }

    private void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
            }
        });
    }
}
