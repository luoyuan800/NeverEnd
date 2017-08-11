package cn.luo.yuan.maze.client.display.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.DLCManager;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.utils.StringUtils;

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
    }

    @Override
    public void onBuySuccess(MonsterDLC dlc) {
        context.showPopup(String.format("购买%s成功", dlc.getTitle()));
    }

    @Override
    public void onBuyFailure(String failure) {
        context.showPopup("购买失败");
    }

    @Override
    public void onDetailSuccess(final MonsterDLC dlc) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                View view = View.inflate(context.getContext(),R.layout.monster_dlc, null);
                view.setTag(R.string.item, dlc);
                if(dlc.getImage().size() > 0){
                    ((ImageView)view.findViewById(R.id.dlc_1)).setImageDrawable(Resource.loadDrawableFromBytes(dlc.getImage().get(0)));
                }
                if(dlc.getImage().size() > 1){
                    ((ImageView)view.findViewById(R.id.dlc_2)).setImageDrawable(Resource.loadDrawableFromBytes(dlc.getImage().get(1)));
                }
                if(dlc.getImage().size() > 2){
                    ((ImageView)view.findViewById(R.id.dlc_2)).setImageDrawable(Resource.loadDrawableFromBytes(dlc.getImage().get(2)));
                }
                ((TextView)view.findViewById(R.id.dlc_title)).setText(Html.fromHtml(dlc.getTitle()));
                ((TextView)view.findViewById(R.id.dlc_desc)).setText(Html.fromHtml(dlc.getDesc()));
                ((TextView)view.findViewById(R.id.dlc_cost)).setText(StringUtils.formatNumber(dlc.getDebrisCost()));
                Dialog dialog = SimplerDialogBuilder.build(view, "详情", context.getContext(), context.getRandom());
                view.findViewById(R.id.dlc_buy).setOnClickListener(DlcDialog.this);
                view.findViewById(R.id.dlc_buy).setTag(R.string.item, dlc);
                view.findViewById(R.id.dlc_buy).setTag(R.string.dialog, dialog);
                view.findViewById(R.id.close).setOnClickListener(DlcDialog.this);
                view.findViewById(R.id.close).setTag(R.string.item, dlc);
                view.findViewById(R.id.close).setTag(R.string.dialog, dialog);
            }
        });
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
            switch (v.getId()){
                case R.id.dlc_buy:
                    manager.buyMonsterDlc(dlc, this);
                    break;
                case R.id.close:
                    Dialog dialog = (Dialog) v.getTag(R.string.dialog);
                    if(dialog!=null){
                        dialog.dismiss();
                    }
                    break;
            }
        }
        if( o instanceof DLCKey){
            DLCKey key = (DLCKey)o;
            manager.queryMonsterDLC(key.getId(), this);
        }

    }
}
