package cn.luo.yuan.maze.client.display.dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.*;
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
    private ProgressDialog progressDialog;

    private void closeProgressDialog(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }
    public WarehouseDialog(NeverEnd context){
        this.context = context;
        progressDialog = new ProgressDialog(context.getContext());
        service = new ServerService(context);
    }

    public void show(){
        final View view = View.inflate(context.getContext(), R.layout.select_submit, null);
        final RadioButton petType = (RadioButton)view.findViewById(R.id.pet_type);
        final RadioButton accessoryType = (RadioButton)view.findViewById(R.id.accessory_type);
        final RadioButton goodsType = (RadioButton)view.findViewById(R.id.goods_type);
        petType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    accessoryType.setChecked(false);
                    goodsType.setChecked(false);
                    progressDialog.show();
                    context.getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<OwnedAble> ownedAbleList = service.queryWarehouse(Field.PET_TYPE, context);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
                                    StringAdapter<OwnedAble> adapter = new StringAdapter<>(ownedAbleList);
                                    list.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    });
                }
            }
        });
        goodsType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    accessoryType.setChecked(false);
                    petType.setChecked(false);
                    progressDialog.show();
                    context.getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<OwnedAble> ownedAbleList = service.queryWarehouse(Field.GOODS_TYPE, context);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
                                    StringAdapter<OwnedAble> adapter = new StringAdapter<>(ownedAbleList);
                                    list.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    });
                }
            }
        });
        accessoryType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    goodsType.setChecked(false);
                    petType.setChecked(false);
                    progressDialog.show();
                    context.getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<OwnedAble> ownedAbleList = service.queryWarehouse(Field.ACCESSORY_TYPE, context);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
                                    StringAdapter<OwnedAble> adapter = new StringAdapter<>(ownedAbleList);
                                    list.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    });
                }
            }
        });
        petType.setChecked(true);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getItemAtPosition(position);
        if(object instanceof Serializable){
            context.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    if(service.storeWarehouse((Serializable) object,context)){
                        context.showToast("%s存储成功", object instanceof NameObject ? ((NameObject) object).getDisplayName(): "");
                    }else{
                        context.showToast("碎片数量不足，需要%d块片", Data.WAREHOUSE_DEBRIS);
                    }
                }
            });

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
