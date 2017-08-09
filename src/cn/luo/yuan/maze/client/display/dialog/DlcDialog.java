package cn.luo.yuan.maze.client.display.dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.DLCManager;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;

import java.util.List;

/**
 * Copyright @Luo
 * Created by Gavin Luo on 8/9/2017.
 */
public class DlcDialog implements DLCManager.DetailCallBack, DLCManager.BuyCallBack, DLCManager.QueryCallBack, View.OnClickListener {
    private NeverEnd context;
    private Handler handler = new Handler();
    private ProgressDialog progress;
    private DLCManager manager;
    public DlcDialog(NeverEnd context){
        this.context = context;
        manager = new DLCManager(context);
    }

    public void show(){
        progress = new ProgressDialog(context.getContext());
        progress.show();
        context.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                manager.queryMonsterDLCs(DlcDialog.this);
            }
        });
        LoadMoreListView list = new LoadMoreListView(context.getContext());

    }

    @Override
    public void onBuySuccess(MonsterDLC dlc) {

    }

    @Override
    public void onBuyFailure(String failure) {

    }

    @Override
    public void onDetailSuccess(MonsterDLC dlc) {

    }

    @Override
    public void onDetailFailure() {

    }

    @Override
    public void onQuerySuccess(final List<DLCKey> keys) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progress!=null && progress.isShowing()){
                    progress.dismiss();
                    StringAdapter<DLCKey> adapter = new StringAdapter<DLCKey>(keys);
                    adapter.setOnClickListener(DlcDialog.this);
                    LoadMoreListView list = new LoadMoreListView(context.getContext());
                    list.setAdapter(adapter);
                    SimplerDialogBuilder.build(list, Resource.getString(R.string.close), context.getContext(), context.getRandom());
                }
            }
        });
    }

    @Override
    public void onQueryFailure() {

    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag(R.string.item);
        if(o instanceof MonsterDLC){
            MonsterDLC dlc = (MonsterDLC)o;

        }
    }
}
