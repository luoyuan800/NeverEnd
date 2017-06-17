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
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Comparator;
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
    private Comparator<Accessory> order = new Comparator<Accessory>() {
        @Override
        public int compare(Accessory lhs, Accessory rhs) {
            int compare = Boolean.compare(rhs.isMounted(), lhs.isMounted());
            return compare == 0 ? Long.compare(rhs.getLevel(), lhs.getLevel()) : compare;
        }
    };

    public AccessoriesDialog(GameContext context) {
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setView(R.layout.accessory);
        dialog = builder.create();
        accessories = context.getDataManager().loadAccessories(0, 10, key, order);
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
                                                accessoryAdapter.notifyDataSetChanged();
                                                context.getViewHandler().refreshAccessory(context.getHero());
                                                context.getViewHandler().refreshProperties(context.getHero());
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
                        context.getViewHandler().refreshAccessory(context.getHero());
                        context.getViewHandler().refreshProperties(context.getHero());
                    }
                    context.getHero().setMaterial(context.getHero().getMaterial() - Data.FUSE_COST * main.getLevel());
                    context.getDataManager().delete(fuse);
                    accessoryAdapter.getData().remove(fuse);
                    if (context.getAccessoryHelper().fuse(main, fuse)) {
                        new AlertDialog.Builder(context.getContext()).setTitle("升级成功").
                                setMessage(Html.fromHtml(main.toString())).setCancelable(false).
                                setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        refreshMainAccessoryView();
                                        fuse = null;
                                        refreshFuseAccessoryView();
                                        accessoryAdapter.notifyDataSetChanged();
                                        if(main.isMounted()) {
                                            context.getViewHandler().refreshAccessory(context.getHero());
                                            context.getViewHandler().refreshProperties(context.getHero());
                                        }
                                    }
                                }).
                                create().show();
                    } else {
                        new AlertDialog.Builder(context.getContext()).setTitle("升级失败").
                                setMessage(Html.fromHtml(main.toString())).
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
        List<Accessory> accessories = context.getDataManager().loadAccessories(this.accessories.size(), 10, key, order);
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

    @Override
    public boolean onLongClick(View v) {
        Object tag = v.getTag(R.string.item);
        if (tag instanceof Accessory && tag != main && !((Accessory) tag).isMounted()) {
            fuse = (Accessory) tag;
            refreshFuseAccessoryView();
            return true;
        }
        return false;
    }

    public void refreshFuseAccessoryView() {
        if (fuse != null) {
            ((TextView) dialog.findViewById(R.id.accessory_name_2)).setText(Html.fromHtml(fuse.getDisplayName()));
            ((TextView) dialog.findViewById(R.id.accessory_effects_2)).setText(Html.fromHtml(StringUtils.formatEffectsAsHtml(fuse.getEffects())));
        } else {
            ((TextView) dialog.findViewById(R.id.accessory_name_2)).setText(StringUtils.EMPTY_STRING);
            ((TextView) dialog.findViewById(R.id.accessory_effects_2)).setText(StringUtils.EMPTY_STRING);
        }
        detectFuseAble();
    }

    private void refreshMainAccessoryView() {
        if (main != null) {
            ((TextView) dialog.findViewById(R.id.accessory_name)).setText(Html.fromHtml(main.getDisplayName()));
            ((TextView) dialog.findViewById(R.id.accessory_effects)).setText(Html.fromHtml(StringUtils.formatEffectsAsHtml(main.getEffects())));
            if (main.isMounted()) {
                ((Button) dialog.findViewById(R.id.accessory_mount)).setText(Resource.getString(R.string.need_un_mount));
            } else {
                ((Button) dialog.findViewById(R.id.accessory_mount)).setText(Resource.getString(R.string.need_mount));
            }
            dialog.findViewById(R.id.accessory_mount).setEnabled(true);
        } else {
            ((TextView) dialog.findViewById(R.id.accessory_name)).setText(StringUtils.EMPTY_STRING);
            ((TextView) dialog.findViewById(R.id.accessory_effects)).setText(StringUtils.EMPTY_STRING);
            ((Button) dialog.findViewById(R.id.accessory_mount)).setText(Resource.getString(R.string.need_un_mount));
            dialog.findViewById(R.id.accessory_mount).setEnabled(false);
        }
        detectFuseAble();
    }

    private void detectFuseAble() {
        boolean costE = true;
        if(main!=null){
            long value = Data.FUSE_COST * main.getLevel();
            costE = context.getHero().getMaterial() >= value;
            if(costE){
                ((Button)dialog.findViewById(R.id.accessory_fuse)).setText(R.string.upgrade);
            }else{
                ((Button)dialog.findViewById(R.id.accessory_fuse)).setText("升级需要锻造" + StringUtils.formatNumber(value));
            }
        }
        if (fuse != null && main != null && fuse.getType().equals(main.getType()) && costE) {
            dialog.findViewById(R.id.accessory_fuse).setEnabled(true);
        } else {
            dialog.findViewById(R.id.accessory_fuse).setEnabled(false);
        }
    }
}
