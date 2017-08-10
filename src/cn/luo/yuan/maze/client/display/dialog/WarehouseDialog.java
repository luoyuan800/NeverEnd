package cn.luo.yuan.maze.client.display.dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.service.ServerService;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.utils.Field;

import java.io.Serializable;
import java.util.List;


/**
 * Created by luoyuan on 2017/8/10.
 */
public class WarehouseDialog implements AdapterView.OnItemClickListener, View.OnClickListener {
    private NeverEnd context;
    private ServerService service;
    private Handler handler = new Handler();
    public WarehouseDialog(NeverEnd context){
        this.context = context;
    }

    public void show(){
        ProgressDialog progressDialog = new ProgressDialog(context.getContext());
        progressDialog.show();
        context.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<OwnedAble> ownedAbleList = service.queryWarehouse(Field.PET_TYPE, context);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        View view = View.inflate(context.getContext(), R.layout.select_submit, null);
                        LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
                        StringAdapter<OwnedAble> adapter = new StringAdapter<>(ownedAbleList);
                        adapter.setOnClickListener(WarehouseDialog.this);
                        list.setAdapter(adapter);
                        SimplerDialogBuilder.build(view, "上传", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SimplerDialogBuilder.showSelectLocalItemDialog(WarehouseDialog.this, context);
                                    }
                                });
                            }
                        }, "关闭", null, context.getContext());
                    }
                });
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getItemAtPosition(position);
        if(object instanceof Serializable){
            if(service.storeWarehouse((Serializable) object,context)){
                context.showToast("%s存储成功", object instanceof NameObject ? ((NameObject) object).getDisplayName(): "");
            }else{
                context.showToast("碎片数量不足，需要%d块片", Data.WAREHOUSE_DEBRIS);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag(R.string.item);
        if(object instanceof IDModel){
            context.getDataManager().add((IDModel) object);
            if(service.getBackWarehouse(((IDModel) object).getId(),
                    (object instanceof Pet ? Field.PET_TYPE : ( object instanceof Accessory ? Field.ACCESSORY_TYPE : Field.GOODS_TYPE)),
                    context)){
                context.showToast("取回了 %s",object instanceof NameObject ? ((NameObject) object).getDisplayName(): "" );
            }else{
                context.showToast("无法取回，稍后再试");
            }
        }
    }
}
