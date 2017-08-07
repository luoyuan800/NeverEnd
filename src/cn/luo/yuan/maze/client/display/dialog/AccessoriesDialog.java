package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.exception.MountLimitException;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.service.AccessoryHelper;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * Created by luoyuan on 2017/5/26.
 */
public class AccessoriesDialog implements LoadMoreListView.OnRefreshLoadingMoreListener, View.OnClickListener, View.OnLongClickListener {
    private OnAccessoryChangeListener changeListener;
    private Dialog dialog;
    private StringAdapter<Accessory> accessoryAdapter;
    private List<Accessory> accessories;
    private String key = "";
    private NeverEnd context;
    private Accessory main, fuse;
    private Comparator<Accessory> order = new Comparator<Accessory>() {
        @Override
        public int compare(Accessory lhs, Accessory rhs) {
            int compare = Boolean.compare(rhs.isMounted(), lhs.isMounted());
            return compare == 0 ? Long.compare(rhs.getLevel(), lhs.getLevel()) : compare;
        }
    };
    public AccessoriesDialog(NeverEnd context, OnAccessoryChangeListener listener) {
        this.context = context;
        this.changeListener = listener;
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.accessory);
        } else {
            builder.setView(View.inflate(context.getContext(), R.layout.accessory, null));
        }
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
        list.initQuery(new LoadMoreListView.OnQueryChange() {
            @Override
            public void onQueryChange(String query) {
                key = query;
                accessoryAdapter.setData(context.getDataManager().loadAccessories(0, 10, key, order));
            }
        });
        dialog.findViewById(R.id.accessory_mount)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (main != null) {
                                                if (main.isMounted()) {
                                                    context.getAccessoryHelper().unMountAccessory(main, context.getHero());
                                                } else {
                                                    try {
                                                        context.mountAccessory(main, true);
                                                    } catch (MountLimitException e) {
                                                        Toast.makeText(context.getContext(), e.word, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                accessoryAdapter.notifyDataSetChanged();
                                                refreshMainAccessoryView();
                                                notifyChange();
                                            }
                                        }
                                    }
                );
        dialog.findViewById(R.id.accessory_fuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (main != null && fuse != null && !main.getId().equals(fuse.getId())) {
                    if (fuse.isMounted()) {
                        AccessoryHelper.unMountAccessory(fuse, context.getHero());
                    }
                    context.getHero().setMaterial(context.getHero().getMaterial() - Data.FUSE_COST * main.getLevel());

                    if (context.getAccessoryHelper().fuse(main, fuse)) {
                        fuse = null;
                        context.getDataManager().save(main);
                        new AlertDialog.Builder(context.getContext()).setTitle("升级成功").
                                setMessage(Html.fromHtml(main.toString())).setCancelable(false).
                                setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        refreshMainAccessoryView();
                                        refreshFuseAccessoryView();
                                        accessoryAdapter.notifyDataSetChanged();
                                    }
                                }).
                                create().show();
                    } else {
                        fuse = null;
                        new AlertDialog.Builder(context.getContext()).setTitle("升级失败").
                                setMessage(Html.fromHtml(main.toString())).
                                setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        refreshMainAccessoryView();
                                        refreshFuseAccessoryView();
                                        accessoryAdapter.notifyDataSetChanged();
                                    }
                                }).setCancelable(false).
                                create().show();
                    }
                    context.getDataManager().delete(fuse);
                    accessoryAdapter.getData().remove(fuse);
                    notifyChange();

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

    private void notifyChange() {
        if(changeListener!=null){
            changeListener.change(context);
        }
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
        List<Accessory> accessories = context.getDataManager().loadAccessories(accessoryAdapter.getCount(), 10, key, order);
        if (accessories.size() > 0) {
            accessoryAdapter.addAll(accessories);
            loadMoreListView.onLoadMoreComplete(false);
            accessoryAdapter.notifyDataSetChanged();
        } else {
            loadMoreListView.onLoadMoreComplete(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag(R.string.item) instanceof Accessory) {
            Accessory tag = (Accessory) v.getTag(R.string.item);
            if(fuse == null || !tag.equals(fuse)) {
                main = tag;
                refreshMainAccessoryView();
            }
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

    public static interface OnAccessoryChangeListener {
        void change(InfoControlInterface context);
    }

    private void refreshFuseAccessoryView() {
        if (fuse != null) {
            ((TextView) dialog.findViewById(R.id.accessory_name_2)).setText(Html.fromHtml(fuse.getDisplayName()));
            ((TextView) dialog.findViewById(R.id.accessory_effects_2)).setText(Html.fromHtml(StringUtils.formatEffectsAsHtml(fuse.getEffects())));
            if (StringUtils.isNotEmpty(fuse.getDesc())) {
                ((TextView) dialog.findViewById(R.id.accessory_desc_2)).setText(Html.fromHtml(fuse.getDesc()));
            } else {
                ((TextView) dialog.findViewById(R.id.accessory_desc_2)).setText(StringUtils.EMPTY_STRING);
            }
        } else {
            ((TextView) dialog.findViewById(R.id.accessory_name_2)).setText(StringUtils.EMPTY_STRING);
            ((TextView) dialog.findViewById(R.id.accessory_effects_2)).setText(StringUtils.EMPTY_STRING);
            ((TextView) dialog.findViewById(R.id.accessory_desc_2)).setText(StringUtils.EMPTY_STRING);
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
            if (StringUtils.isNotEmpty(main.getDesc())) {
                ((TextView) dialog.findViewById(R.id.accessory_desc)).setText(Html.fromHtml(main.getDesc()));
            } else {
                ((TextView) dialog.findViewById(R.id.accessory_desc)).setText(StringUtils.EMPTY_STRING);
            }
            dialog.findViewById(R.id.accessory_mount).setEnabled(true);
        } else {
            ((TextView) dialog.findViewById(R.id.accessory_name)).setText(StringUtils.EMPTY_STRING);
            ((TextView) dialog.findViewById(R.id.accessory_effects)).setText(StringUtils.EMPTY_STRING);
            ((TextView) dialog.findViewById(R.id.accessory_desc)).setText(StringUtils.EMPTY_STRING);
            ((Button) dialog.findViewById(R.id.accessory_mount)).setText(Resource.getString(R.string.need_un_mount));
            dialog.findViewById(R.id.accessory_mount).setEnabled(false);
        }
        detectFuseAble();
    }

    private void detectFuseAble() {
        boolean costE = true;
        if (main != null) {
            long value = Data.FUSE_COST * main.getLevel();
            costE = context.getHero().getMaterial() >= value;
            if (costE) {
                ((Button) dialog.findViewById(R.id.accessory_fuse)).setText(R.string.upgrade);
            } else {
                ((Button) dialog.findViewById(R.id.accessory_fuse)).setText("升级需要锻造" + StringUtils.formatNumber(value, false));
            }
        }
        if (fuse != null && main != null && fuse.getType().equals(main.getType()) && costE && !fuse.getId().equals(main.getId())) {
            dialog.findViewById(R.id.accessory_fuse).setEnabled(true);
        } else {
            dialog.findViewById(R.id.accessory_fuse).setEnabled(false);
        }
    }
}
