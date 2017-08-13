package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.view.LoadMoreListView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Accessory;
import cn.luo.yuan.maze.model.goods.Goods;
import cn.luo.yuan.maze.utils.Random;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.List;

/**
 * Created by luoyuan on 2017/6/24.
 */
public class SimplerDialogBuilder {
    public static Dialog build(String msg, String posivStr, final DialogInterface.OnClickListener posiv, Context context, Random random) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        dialogBuilder
                .withMessageColor(Color.RED)
                .withMessage(Html.fromHtml(msg))                     //.withMessage(null)  no Msg
                .withButton1Text(posivStr)                                      //def gone
                .withDuration(700)
                .withDialogColor(Color.WHITE)
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (posiv != null) {
                            posiv.onClick(dialogBuilder, DialogInterface.BUTTON_POSITIVE);
                        }
                        dialogBuilder.dismiss();
                    }
                })
                .show();
        return dialogBuilder;
    }

    public static Dialog build(String msg, String posivStr, Context context, Random random) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        dialogBuilder
                .withMessageColor(Color.RED)
                .withMessage(Html.fromHtml(msg))                     //.withMessage(null)  no Msg
                .withButton1Text(posivStr)                                      //def gone
                .withDuration(700)
                .withDialogColor(Color.WHITE)
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
        return dialogBuilder;
    }

    public static Dialog build(String msg, String title, String posivStr, Context context, Random random) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        dialogBuilder
                .withTitle(title)
                .withMessageColor(Color.RED)
                .withMessage(Html.fromHtml(msg))                     //.withMessage(null)  no Msg
                .withButton1Text(posivStr)                                      //def gone
                .withDuration(700)
                .withDialogColor(Color.WHITE)
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
        return dialogBuilder;
    }

    public static AlertDialog build(String msg, String positiveStr, DialogInterface.OnClickListener positive,
                                    String negativeStr, DialogInterface.OnClickListener negative, Context context) {
        return new AlertDialog.Builder(context).setMessage(Html.fromHtml(msg))
                .setPositiveButton(positiveStr, positive).setNegativeButton(negativeStr, negative).show();
    }

    public static Dialog build(View view, String positiveStr, final DialogInterface.OnClickListener positive,
                               String negativeStr, final DialogInterface.OnClickListener negative, Context context) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withDialogColor(Color.WHITE)
                .setCustomView(view, context)                   //.withMessage(null)  no Msg
                .withButton1Text(positiveStr)                                      //def gone
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (positive != null) {
                            positive.onClick(dialogBuilder, 1);
                        } else {
                            dialogBuilder.dismiss();
                        }
                    }
                })
                .withButton2Text(negativeStr)
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (negative != null) {
                            negative.onClick(dialogBuilder, 2);
                        } else {
                            dialogBuilder.dismiss();
                        }
                    }
                })
                .show();
        return dialogBuilder;
    }

    public static AlertDialog build(String msg, Context context, boolean lazy) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(Html.fromHtml(msg));
        if (lazy) {
            return builder.create();
        } else {
            return builder.show();
        }
    }

    public static Dialog build(View view, String posivStr, DialogInterface.OnClickListener listener, Context context) {
        if (listener == null) {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder
                    .withDialogColor(Color.WHITE)
                    .setCustomView(view, context)                   //.withMessage(null)  no Msg
                    .withButton1Text(posivStr)                                      //def gone
                    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .show();
            return dialogBuilder;
        }
        return new AlertDialog.Builder(context).setView(view).setPositiveButton(posivStr, listener).show();
    }

    public static Dialog build(View view, String title, Context context, Random random) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        dialogBuilder
                .withDialogColor(Color.WHITE)
                .withTitle(title)
                .setCustomView(view, context)                   //.withMessage(null)  no Msg
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .show();
        return dialogBuilder;

    }

    public static Dialog build(View view, String b1, final DialogInterface.OnClickListener b1Listener, String b2, Context context, Random random) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        dialogBuilder
                .withDialogColor(Color.WHITE)
                .setCustomView(view, context)                   //.withMessage(null)  no Msg
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withButton1Text(b1)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b1Listener.onClick(dialogBuilder, 1);
                        dialogBuilder.dismiss();
                    }
                })
                .withButton2Text(b2)
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
        return dialogBuilder;
    }

    public static Dialog showSelectLocalItemDialog(AdapterView.OnItemClickListener listener, final NeverEnd context) {
        View view = View.inflate(context.getContext(), R.layout.select_submit, null);
        final RadioButton petR = (RadioButton) view.findViewById(R.id.pet_type);
        final RadioButton accessoryR = (RadioButton) view.findViewById(R.id.accessory_type);
        final RadioButton goodsR = (RadioButton) view.findViewById(R.id.goods_type);
        final EditText key = (EditText) view.findViewById(R.id.key_text);
        final LoadMoreListView list = (LoadMoreListView) view.findViewById(R.id.item_list);
        list.setOnItemClickListener(listener);
        petR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    accessoryR.setChecked(false);
                    goodsR.setChecked(false);
                    final PetAdapter adapter = new PetAdapter(context.getContext(), context.getDataManager(), key.getText().toString());
                    list.setAdapter(adapter);
                    list.setOnLoadListener(adapter);
                    adapter.notifyDataSetChanged();
                    key.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            adapter.setLimitKeyWord(s.toString());
                        }
                    });
                }
            }
        });
        accessoryR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    petR.setChecked(false);
                    goodsR.setChecked(false);
                    final StringAdapter<Accessory> adapter = new StringAdapter<>(context.getDataManager().loadAccessories(0, 100, key.getText().toString(), null));
                    list.setAdapter(adapter);
                    list.setOnLoadListener(new LoadMoreListView.OnRefreshLoadingMoreListener() {
                        @Override
                        public void onLoadMore(LoadMoreListView loadMoreListView) {
                            List<Accessory> collection = context.getDataManager().loadAccessories(adapter.getCount(), 100, key.getText().toString(), null);
                            if (collection.size() > 0) {
                                adapter.getData().addAll(collection);
                            } else {
                                list.onLoadMoreComplete(true);
                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }
        });
        goodsR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    accessoryR.setChecked(false);
                    petR.setChecked(false);
                    StringAdapter<Goods> adapter = new StringAdapter<>(context.getDataManager().loadAllGoods(false));
                    list.onLoadMoreComplete(true);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        petR.setChecked(true);
        return build(view, Resource.getString(R.string.close), null, context.getContext());
    }

    public static Dialog buildClickWarnDialog(Context context, Random random) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withDialogColor(Color.WHITE)
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                .withMessage("点累了吧？休息一会吧！")
                .withMessageColor(Color.RED)
        ;
        if (random != null) {
            dialogBuilder.withEffect(random.randomItem(Effectstype.values()));
        }
        if(random!=null && random.nextBoolean()){
            dialogBuilder.setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });
            dialogBuilder.withButton1Text("不了，谢谢！");
            dialogBuilder.setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            dialogBuilder.withButton2Text("好的，谢谢！");
        }else{
            dialogBuilder.setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });
            dialogBuilder.withButton2Text("不了，谢谢！");

            dialogBuilder.setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            dialogBuilder.withButton1Text("好的，谢谢！");
        }
        dialogBuilder.show();
        return dialogBuilder;
    }
}
