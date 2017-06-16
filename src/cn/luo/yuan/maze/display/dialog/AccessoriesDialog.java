package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.StringAdapter;
import cn.luo.yuan.maze.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.List;

/**
 * Created by luoyuan on 2017/5/26.
 */
public class AccessoriesDialog implements LoadMoreListView.OnRefreshLoadingMoreListener, View.OnClickListener, View.OnLongClickListener {
    private Dialog dialog;
    private StringAdapter<Accessory> accessoryAdapter;
    private List<Accessory> accessories;
    private String key = "";
    private GameContext context;
    private Accessory main, fuse;

    public AccessoriesDialog(GameContext context) {
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setView(R.layout.accessory);
        dialog = builder.create();
        accessories = context.getDataManager().loadAccessories(0, 10, key);
        accessoryAdapter = new StringAdapter<>(accessories);
        accessoryAdapter.setOnClickListener(this);
        accessoryAdapter.setOnLongClickListener(this);
    }

    public void show() {
        dialog.show();
        LoadMoreListView list = (LoadMoreListView) dialog.findViewById(R.id.accessories_list);
        list.setOnLoadListener(this);
        list.setAdapter(accessoryAdapter);
        dialog.findViewById(R.id.accessory_mount)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (main != null) {
                                                if (main.isMounted()) {
                                                    context.getAccessoryHelper().unMountAccessory(main, context.getHero());
                                                } else {
                                                    context.getAccessoryHelper().mountAccessory(main, context.getHero());
                                                }
                                                context.getViewHandler().refreshAccessory(context.getHero());
                                                refreshMainAccessoryView();
                                            }
                                        }
                                    }
                );
        dialog.findViewById(R.id.accessory_fuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (main != null && fuse != null) {
                    if (fuse.isMounted()) {
                        context.getAccessoryHelper().unMountAccessory(fuse, context.getHero());
                    }
                    context.getDataManager().delete(fuse);
                    accessoryAdapter.getData().remove(fuse);
                    if (context.getAccessoryHelper().fuse(main, fuse)) {
                        new AlertDialog.Builder(context.getContext()).setTitle("升级成功").
                                setMessage(Html.fromHtml(main.getDisplayName())).setCancelable(false).
                                setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        refreshMainAccessoryView();
                                        fuse = null;
                                        refreshFuseAccessoryView();
                                        accessoryAdapter.notifyDataSetChanged();
                                    }
                                }).
                                create().show();
                    } else {
                        new AlertDialog.Builder(context.getContext()).setTitle("升级失败").
                                setMessage(Html.fromHtml(main.getDisplayName())).
                                setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        refreshMainAccessoryView();
                                        fuse = null;
                                        refreshFuseAccessoryView();
                                        accessoryAdapter.notifyDataSetChanged();
                                    }
                                }).setCancelable(false).
                                create().show();
                    }

                }
            }
        });
        dialog.findViewById(R.id.accessories_close).setOnClickListener(new View.OnClickListener() {
                                                                           @Override
                                                                           public void onClick(View v) {
                                                                               dismiss();
                                                                           }
                                                                       }
        );
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void onLoadMore(LoadMoreListView loadMoreListView) {
        List<Accessory> accessories = context.getDataManager().loadAccessories(this.accessories.size(), 10, key);
        if (accessories.size() > 0) {
            this.accessories.addAll(accessories);
            loadMoreListView.onLoadMoreComplete(false);
            accessoryAdapter.notifyDataSetChanged();
        } else {
            loadMoreListView.onLoadMoreComplete(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag(R.string.item) instanceof Accessory) {
            main = (Accessory) v.getTag(R.string.item);
            refreshMainAccessoryView();
        }
    }

    private void refreshMainAccessoryView() {
        ((TextView) dialog.findViewById(R.id.accessory_name)).setText(Html.fromHtml(main.getDisplayName()));
        ((TextView) dialog.findViewById(R.id.accessory_effects)).setText(Html.fromHtml(StringUtils.formatEffectsAsHtml(main.getEffects())));
        if (main.isMounted()) {
            ((Button) dialog.findViewById(R.id.accessory_mount)).setText(Resource.getString(R.string.need_un_mount));
        } else {
            ((Button) dialog.findViewById(R.id.accessory_mount)).setText(Resource.getString(R.string.need_mount));
        }
        detectFuseAble();
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getTag(R.string.item) instanceof Accessory && v.getTag(R.string.item)!=main) {
            fuse = (Accessory) v.getTag(R.string.item);
            refreshFuseAccessoryView();
            return true;
        }
        return false;
    }

    public void refreshFuseAccessoryView() {
        ((TextView) dialog.findViewById(R.id.accessory_name_2)).setText(Html.fromHtml(fuse.getDisplayName()));
        ((TextView) dialog.findViewById(R.id.accessory_effects_2)).setText(Html.fromHtml(StringUtils.formatEffectsAsHtml(fuse.getEffects())));
        detectFuseAble();
    }

    private void detectFuseAble() {
        if (fuse != null) {
            dialog.findViewById(R.id.accessory_fuse).setEnabled(true);
        } else {
            dialog.findViewById(R.id.accessory_fuse).setEnabled(false);
        }
    }
}
