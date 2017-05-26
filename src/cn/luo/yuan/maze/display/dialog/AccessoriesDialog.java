package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.StringAdapter;
import cn.luo.yuan.maze.display.view.LoadMoreListView;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.service.GameContext;

import java.util.List;

/**
 * Created by luoyuan on 2017/5/26.
 */
public class AccessoriesDialog {
    private Dialog dialog;
    private StringAdapter<Accessory> accessoryAdapter;
    private List<Accessory> accessories;

    public AccessoriesDialog(GameContext context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setView(R.layout.accessory_fuse);
        dialog = builder.create();
        accessories = context.getDataManager().loadAllAccessories()
        accessories = new StringAdapter<>();
    }

    public void show() {
        dialog.show();
        LoadMoreListView list = (LoadMoreListView) dialog.findViewById(R.id.accessories_list);

    }

    public void dismiss() {
        dialog.dismiss();
    }
}
