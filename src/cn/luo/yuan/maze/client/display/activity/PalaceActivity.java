package cn.luo.yuan.maze.client.display.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.Path;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.dialog.RealBattleDialog;
import cn.luo.yuan.maze.client.display.dialog.SimplerDialogBuilder;
import cn.luo.yuan.maze.client.display.handler.ViewHandler;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.RemoteRealTimeManager;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.LevelRecord;
import cn.luo.yuan.maze.model.NeverEndConfig;
import cn.luo.yuan.maze.model.real.BattleEnd;
import cn.luo.yuan.maze.model.real.Battling;
import cn.luo.yuan.maze.model.real.NoDebris;
import cn.luo.yuan.maze.model.real.Quit;
import cn.luo.yuan.maze.model.real.RealState;
import cn.luo.yuan.maze.model.real.Waiting;
import cn.luo.yuan.maze.model.real.level.*;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
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

    private boolean ranging = false;
    public void rangeBattle(View view) {
        final RemoteRealTimeManager manager = new RemoteRealTimeManager(server, gameContext);
        ranging = true;
        final Dialog progressDialog = SimplerDialogBuilder.build("匹配中……", Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                synchronized (manager) {
                    ranging = false;
                }
            }
        }, this, gameContext.getRandom());
        progressDialog.setCancelable(false);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (ranging) {
                    synchronized (manager) {
                        if(queryRangingState(manager, progressDialog)) {
                            break;
                        }
                    }
                }
                if(ranging){
                    ranging = false;
                    queryRangingState(manager, progressDialog);
                }
            }
        });
    }

    public boolean queryRangingState(final RemoteRealTimeManager manager, final DialogInterface progressDialog) {
        final RealState state = manager.pollState();
        if(state instanceof Waiting){
            return false;
        }
        if (state instanceof NoDebris) {
            ranging = false;
            gameContext.showPopup(Resource.getString(R.string.not_debris));
            return true;
        }
        if(state instanceof Battling){
            ranging = false;
            manager.setId(state.getId());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    battleDialog = new RealBattleDialog(manager, gameContext, state.getId());
                    progressDialog.dismiss();
                }
            });
            return true;
        }
        if(state instanceof BattleEnd || state instanceof Quit){
            updateLevel();
            ranging= false;
            return true;
        }
        return false;
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
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection con = server.getHttpURLConnection(Path.TOP_N_PALACE, RestConnection.POST);
                    con.addRequestProperty(Field.COUNT, String.valueOf(5));
                    Object o = server.connect(con);
                    if (o instanceof List) {
                        final StringBuilder sb = new StringBuilder();
                        for (LevelRecord record : (List<LevelRecord>) o) {
                            if (record.getHero() != null)
                                sb.append(record.getHero().getDisplayName()).append("<br>").append(" ")
                                        .append(StringUtils.formatRealLevel(record.getPoint(), record.getHero().getRace())).append("<br>");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ViewHandler.setText((TextView) findViewById(R.id.online_palace_range), sb.toString());
                            }
                        });
                    }
                } catch (IOException e) {
                    LogHelper.logException(e, "query top n palace");
                }
            }
        });
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
                        record.setElementer(config.isElementer());
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
                if (StringUtils.isNotEmpty(tip)) {
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
