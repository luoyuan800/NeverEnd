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
import cn.luo.yuan.maze.model.dlc.DLC;
import cn.luo.yuan.maze.model.dlc.DLCKey;
import cn.luo.yuan.maze.model.dlc.MonsterDLC;
import cn.luo.yuan.maze.model.dlc.SingleItemDLC;
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

    public DlcDialog(NeverEnd context) {
        this.context = context;
        manager = new DLCManager(context);
    }

    public void show() {
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
    public void onBuySuccess(DLC dlc) {
        context.showPopup(String.format("购买%s成功", dlc.getTitle()));
    }

    @Override
    public void onBuyFailure(String failure) {
        context.showPopup("购买失败，请确保你拥有足够的碎片。碎片可以通过观看广告获取。");
    }

    @Override
    public void onDetailSuccess(final DLC dlc) {
        if (dlc instanceof MonsterDLC) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    View view = View.inflate(context.getContext(), R.layout.monster_dlc, null);
                    view.setTag(R.string.item, dlc);
                    if (((MonsterDLC)dlc).getImage().size() > 0) {
                        ((ImageView) view.findViewById(R.id.dlc_1)).setImageDrawable(Resource.loadDrawableFromBytes(((MonsterDLC)dlc).getImage().get(0)));
                    } else {
                        ((ImageView) view.findViewById(R.id.dlc_1)).setImageDrawable(null);
                    }
                    if (((MonsterDLC)dlc).getImage().size() > 1) {
                        ((ImageView) view.findViewById(R.id.dlc_2)).setImageDrawable(Resource.loadDrawableFromBytes(((MonsterDLC)dlc).getImage().get(1)));
                    } else {
                        ((ImageView) view.findViewById(R.id.dlc_2)).setImageDrawable(null);
                    }
                    if (((MonsterDLC)dlc).getImage().size() > 2) {
                        ((ImageView) view.findViewById(R.id.dlc_3)).setImageDrawable(Resource.loadDrawableFromBytes(((MonsterDLC)dlc).getImage().get(2)));
                    } else {
                        ((ImageView) view.findViewById(R.id.dlc_3)).setImageDrawable(null);
                    }
                    ((TextView) view.findViewById(R.id.dlc_title)).setText(Html.fromHtml(dlc.getTitle()));
                    ((TextView) view.findViewById(R.id.dlc_desc)).setText(Html.fromHtml(dlc.getDesc() + (dlc instanceof MonsterDLC ? "<br>注意：该资料片是扩充怪物资料，使得你在迷宫中可以遇到和捕获她们，并不是直接获取宠物。" : "")));
                    ((TextView) view.findViewById(R.id.dlc_cost)).setText(StringUtils.formatNumber(dlc.getDebrisCost()));
                    Dialog dialog = SimplerDialogBuilder.build(view, "详情", context.getContext(), context.getRandom());
                    view.findViewById(R.id.dlc_buy).setOnClickListener(DlcDialog.this);
                    view.findViewById(R.id.dlc_buy).setTag(R.string.item, dlc);
                    view.findViewById(R.id.dlc_buy).setTag(R.string.dialog, dialog);
                    view.findViewById(R.id.close).setOnClickListener(DlcDialog.this);
                    view.findViewById(R.id.close).setTag(R.string.item, dlc);
                    view.findViewById(R.id.close).setTag(R.string.dialog, dialog);
                }
            });
        } else if(dlc instanceof SingleItemDLC){
            SimplerDialogBuilder.build(String.format("%s X %s", dlc.getDesc(), StringUtils.formatNumber(dlc.getDebrisCost())), Resource.getString(R.string.buy_label), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    View view = new View(context.getContext());
                    view.setId(R.id.dlc_buy);
                    view.setTag(R.string.item, dlc);
                    DlcDialog.this.onClick(view);
                }
            }, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, context.getContext());
        }
    }

    @Override
    public void onDetailFailure() {

    }

    @Override
    public void onQuerySuccess(final List<DLCKey> keys) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                    StringAdapter<DLCKey> adapter = new StringAdapter<DLCKey>(keys);
                    adapter.setOnClickListener(DlcDialog.this);
                    LoadMoreListView list = new LoadMoreListView(context.getContext());
                    list.setAdapter(adapter);
                    SimplerDialogBuilder.build(list, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, context.getContext(), true);
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
        switch (v.getId()) {
            case R.id.dlc_buy:
                if(o instanceof MonsterDLC) {
                    manager.buyMonsterDlc((MonsterDLC) o, this);
                }else if(o instanceof SingleItemDLC){
                    manager.buySingleItemDlc((SingleItemDLC) o, this);
                }
                break;
            case R.id.close:
                Dialog dialog = (Dialog) v.getTag(R.string.dialog);
                if (dialog != null) {
                    dialog.dismiss();
                }
                break;
        }

        if (o instanceof DLCKey) {
            DLCKey key = (DLCKey) o;
            manager.queryMonsterDLC(key.getId(), this);
        }

    }
}
