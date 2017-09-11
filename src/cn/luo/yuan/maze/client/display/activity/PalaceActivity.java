package cn.luo.yuan.maze.client.display.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.Path;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.handler.ViewHandler;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.model.LevelRecord;
import cn.luo.yuan.maze.model.NeverEndConfig;
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
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_palcace);
        gameContext = (NeverEnd) getApplication();
        gameContext.setContext(this);
        Resource.init(this);
        server = new RestConnection(Field.SERVER_URL,getVersion(), Resource.getSingInfo());
        submitRecord();
    }

    private void updateLevel() {
        executor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    HttpURLConnection con = server.getHttpURLConnection(Path.POLL_REAL_RECORD, RestConnection.POST);
                    con.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
                    final Object o = server.connect(con);
                    if(o instanceof LevelRecord){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ImageView)findViewById(R.id.hero_pic)).setImageDrawable(
                                        Resource.loadImageFromAssets(((LevelRecord) o).getHead(),true));
                                ViewHandler.setText((TextView) findViewById(R.id.online_palace_level),
                                        StringUtils.formatRealLevel(((LevelRecord) o).getPoint(), gameContext.getHero().getRace()));
                                ViewHandler.setText((TextView) findViewById(R.id.hero_name), gameContext.getHero().getDisplayName());
                                String percent = ((LevelRecord) o).getWin() + ((LevelRecord) o).getLost() > 0 ? StringUtils.formatPercentage(((LevelRecord) o).getWin() * 100 / (((LevelRecord) o).getWin() + ((LevelRecord) o).getLost())) : "0%";
                                ViewHandler.setText((TextView) findViewById(R.id.online_palace_rate), percent);
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
    private void submitRecord() {
        showProgressDialog();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection con = server.getHttpURLConnection(Path.UPDATE_REAL_RECORD, RestConnection.POST);
                    con.addRequestProperty(Field.OWNER_ID_FIELD, gameContext.getHero().getId());
                    LevelRecord record = new LevelRecord(gameContext.getHero());
                    NeverEndConfig config = gameContext.getDataManager().loadConfig();
                    if(config!=null){
                        record.setHead(config.getHead());
                    }
                    final Object o = server.connect(record, con);
                    if(o.equals(Field.RESPONSE_RESULT_OK)){
                        updateLevel();
                    }
                } catch (IOException e) {
                    LogHelper.logException(e, "poll record!");
                }
            }
        });
    }
    private ProgressDialog progress;
    private void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(progress!=null && progress.isShowing()){
                    progress.dismiss();
                }else if(progress == null){
                    progress = new ProgressDialog(PalaceActivity.this);
                }
                progress.show();
            }
        });
    }

    private void hideProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress!=null && progress.isShowing()){
                    progress.dismiss();
                }
            }
        });
    }

    public void onExit(View view){
        this.finish();
    }
}
