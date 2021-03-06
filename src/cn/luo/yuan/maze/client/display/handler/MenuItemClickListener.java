package cn.luo.yuan.maze.client.display.handler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.*;
import android.widget.*;
import cn.luo.yuan.maze.Path;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.activity.OnlineActivity;
import cn.luo.yuan.maze.client.display.activity.PalaceActivity;
import cn.luo.yuan.maze.client.display.adapter.PetAdapter;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.dialog.*;
import cn.luo.yuan.maze.client.service.LocalRealTimeManager;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.client.utils.RestConnection;
import cn.luo.yuan.maze.client.utils.SDFileUtils;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.service.InfoControlInterface;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
    private Context context;
    private NeverEnd control;

    public MenuItemClickListener(Context context, NeverEnd control) {
        this.context = context;
        this.control = control;
    }

    //分享文本
    public void shareToNet() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_string));
        shareIntent.setType("text/*");
        Intent chooser = Intent.createChooser(shareIntent, "分享到");
        context.startActivity(chooser);
    }

    private void installAPK(File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须要加上这一句话才能保证安装完成后不会跳出到主界面
        context.startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ad_show:
                control.showAd();
                break;
            case R.id.debris:
                debrisChangeDialog();
                break;
            case R.id.palace:
                Intent palaceIntent = new Intent(context, PalaceActivity.class);
                context.startActivity(palaceIntent);
                break;
            case R.id.update:
                control.getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        final String version = Resource.getVersion();
                        final RestConnection server = new RestConnection(Field.SERVER_URL, version,Resource.getSingInfo());
                        try {
                            HttpURLConnection con = server.getHttpURLConnection(Path.GET_RELEASE_NOTE, RestConnection.GET);
                            final String rn = server.connect(con).toString();
                            con = server.getHttpURLConnection(Path.GET_CURRENT_VERSION, RestConnection.GET);
                            final String cv = server.connect(con).toString();

                            showReleaseNote(version, server, rn, cv);
                        } catch (Exception e) {
                            LogHelper.logException(e, "Query Version");
                        }
                    }
                });

                break;
            case R.id.backup:
                SimplerDialogBuilder.build("备份存档到服务器上需要消耗" + Data.UPLOAD_SAVE_DEBRIS + "个碎片。备份成功后你可以在其他手机上恢复你的存档。", Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final  ProgressDialog progress = new ProgressDialog(context);
                        progress.show();
                        control.getExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                String path = SDFileUtils.zipFiles(control.getHero().getId(),control.getDataManager().retrieveAllSaveFile());
                                final String name = control.getServerService().uploadSaveFile(path, control.getHero().getId());
                                if(StringUtils.isNotEmpty(name)){
                                    control.getViewHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                            SimplerDialogBuilder.build("请牢记你的备份编号： " + name, "备份成功", Resource.getString(R.string.conform), null, context, control.getRandom());
                                        }
                                    });
                                }else{
                                    progress.dismiss();
                                    control.showPopup("备份失败，确认你有足够的碎片后重试！");
                                }
                            }
                        });
                    }
                }, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, context);
                break;
            case R.id.help:
                final AlertDialog helpDetailDialog = new AlertDialog.Builder(context).create();
                helpDetailDialog.setButton(DialogInterface.BUTTON_POSITIVE, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                Button bas = new Button(context);
                linearLayout.addView(bas);
                bas.setText("基本术语");
                bas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpDetailDialog.setMessage(Html.fromHtml(Resource.readStringFromAssets("help","base")));
                        helpDetailDialog.show();
                    }
                });

                Button elementRace = new Button(context);
                linearLayout.addView(elementRace);
                elementRace.setText("种族五行相克");
                elementRace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout ll= new LinearLayout(context);
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ImageView iv = new ImageView(context);
                        iv.setImageResource(R.drawable.race);
                        ll.addView(iv);
                        ImageView eiv =new ImageView(context);
                        eiv.setImageResource(R.drawable.elements);
                        ll.addView(eiv);
                        AlertDialog cont = new AlertDialog.Builder(context).setTitle("种族五行介绍").setView(ll).setNeutralButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                        cont.show();
                    }
                });
                Button petCatch = new Button(context);
                linearLayout.addView(petCatch);
                petCatch.setText("宠物系统");
                petCatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpDetailDialog.setMessage(Html.fromHtml(Resource.readStringFromAssets("help","pet_catch")));
                        helpDetailDialog.show();
                    }
                });

                Button names = new Button(context);
                linearLayout.addView(names);
                names.setText("前后缀名");
                names.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpDetailDialog.setMessage(Html.fromHtml(Resource.readStringFromAssets("help","names")));
                        helpDetailDialog.show();
                    }
                });

                Button tower = new Button(context);
                linearLayout.addView(tower);
                tower.setText("战斗塔");
                tower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpDetailDialog.setMessage(Html.fromHtml(Resource.readStringFromAssets("help","battle_tower")));
                        helpDetailDialog.show();
                    }
                });

                Button accessory = new Button(context);
                linearLayout.addView(accessory);
                accessory.setText("装备");
                accessory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpDetailDialog.setMessage(Html.fromHtml(Resource.readStringFromAssets("help","accessory")));
                        helpDetailDialog.show();
                    }
                });

                Button clickSkillHelp = new Button(context);
                linearLayout.addView(clickSkillHelp);
                clickSkillHelp.setText("点击技能说明");
                clickSkillHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClickSkillDialog clickSkillDialog1 = new ClickSkillDialog(control);
                        clickSkillDialog1.show(0, true);
                    }
                });
                SimplerDialogBuilder.build(linearLayout, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                    }
                }, context,true);
                break;
            case R.id.cdkey:
                new CdkeyDialog(control).show();
                break;
            case R.id.warehouse:
                new WarehouseDialog(control).show();
                break;
            case R.id.accessory:
                new AccessoriesDialog(control, new AccessoriesDialog.OnAccessoryChangeListener() {
                    @Override
                    public void change(InfoControlInterface context) {
                        control.getViewHandler().refreshFreqProperties();
                    }
                }).show();
                break;
            case R.id.crash:
//                throw new RuntimeException("test exception");
                /*Monster monster = control.getPetMonsterHelper().randomMonster(5);
                if(monster!=null){
                    LocalRealTimeManager m = new LocalRealTimeManager(control, monster);
                    new RealBattleDialog(m , control, "");
                }*/
                Hero hero = control.getHero().clone();
                hero.setName("Clone * " + hero.getName());
                hero.setId("123");
                NPCLevelRecord record = new NPCLevelRecord(hero);
                LocalRealTimeManager m = new LocalRealTimeManager(control, hero);
                m.setTargetRecord(record);
                new RealBattleDialog(m , control, "");
                break;
            case R.id.skills:
                new SkillDialog(control).show();
                break;
            case R.id.pay:
                showPayDialog();
                break;
            case R.id.reincarnation:
                SimplerDialogBuilder.build("转生，所有属性和技能重置，人物的基础成长会有少许增加。转生后<b>除了</b>仓库里的宠物、物品和装备，背包中的所有东西（宠物、物品和装备）都会被<b>清空</b>！转生后会增加难度，会出现更多的强力怪物！这是给那些想挑战更高难度玩家准备的！一定要慎重！",
                        Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long orein = control.getHero().getReincarnate();
                                if(control.reincarnate() > orein){
                                    control.getViewHandler().showGiftChoose();
                                }
                            }
                        }, Resource.getString(R.string.close), null, context);
                break;
            case R.id.goods:
                new GoodsDialog().show(control);
                break;
            case R.id.theme:
                NeverEndConfig config = control.getDataManager().loadConfig();
                if(config.getTheme() == android.R.style.Theme_Holo_NoActionBar_Fullscreen){
                   config.setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
                }else{
                    config.setTheme(android.R.style.Theme_Holo_NoActionBar_Fullscreen);
                }
                control.getDataManager().save(config);
                control.getViewHandler().reCreate();
                break;
            case R.id.online_battle:
                Intent gameIntent = new Intent(context, OnlineActivity.class);
                context.startActivity(gameIntent);
                break;
            case R.id.excahnge:
                new ExchangeDialog(control).show();
                break;
            case R.id.local_shop:
                new ShopDialog(control).showLocalShop();
                break;
            case R.id.pets:
                new PetDialog(control, new PetAdapter(context, control.getDataManager(), "")).show();
                break;
            case R.id.pause:
                boolean pause = control.pauseGame();
                item.setTitle(pause ? "继续" : "暂停");
                break;
            case R.id.save:
                control.save(true);
                break;
            default:
                AlertDialog dialog_bak = new AlertDialog.Builder(context).setPositiveButton(R.string.conform, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.working);
                dialog_bak.setView(imageView);
                dialog_bak.show();
        }
        return false;
    }

    private void debrisChangeDialog() {
        final ProgressDialog debrisPost = new ProgressDialog(context);
        debrisPost.show();
        control.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    final int debris = Integer.parseInt(control.getServerService().postDebrisCount(control));
                    control.getViewHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            debrisPost.dismiss();
                        }
                    });
                    showDerisChangeDialog(debris);
                }catch (Exception e){
                    LogHelper.logException(e, "change debris!");
                }
            }
        });
    }

    private void showDerisChangeDialog(final int debris) {
        if(debris > 0) {
            control.getViewHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        LinearLayout derisLayout = new LinearLayout(context);
                        derisLayout.setOrientation(LinearLayout.VERTICAL);
                        final NumberPicker picker = new NumberPicker(context);
                        picker.setMaxValue(debris);
                        picker.setMinValue(1);
                        derisLayout.addView(picker);
                        TextView debrisTip = new TextView(context);
                        debrisTip.setText(Resource.getString(R.string.debris_tip));
                        derisLayout.addView(debrisTip);
                        final Button myKeys = new Button(context);
                        myKeys.setText(R.string.MY_DEBRIS_KEY);
                        myKeys.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                queryMyKeys();
                            }
                        });
                        derisLayout.addView(myKeys);
                        final Dialog dialog = SimplerDialogBuilder.build(derisLayout, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.show();
                                control.getExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        final String cd = control.getServerService().changeDebris(picker.getValue(), control);
                                        control.getViewHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                dialog.dismiss();
                                                if(StringUtils.isNotEmpty(cd)){
                                                    SimplerDialogBuilder.build(Resource.getString(R.string.debris_change_result, cd), context, false);
                                                }else{
                                                    control.showToast("碎片数量不足！");
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, context);
                    } catch (Exception e) {
                        LogHelper.logException(e, "Show change debria dialog");
                    }
                }
            });
        }
    }

    private void queryMyKeys() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        control.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<CDKey> keys = control.getServerService().queryMyKeys(control);
                control.getViewHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        ListView listView = new ListView(context);
                        listView.setAdapter(new StringAdapter<CDKey>(keys));
                        SimplerDialogBuilder.build(listView, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, context, false);
                    }
                });
            }
        });
    }

    public void showReleaseNote(final String version, final RestConnection server, final String rn, final String cv) {
        control.getViewHandler().post(new Runnable() {
            @Override
            public void run() {
                String msg = "";
                if(cv.equals(version)){
                    msg = "当前已经是最新版本了<br>" + rn;
                }else{
                    msg = "需要更新到： " + cv + "<br>" + rn;
                }
                SimplerDialogBuilder.build(msg, "当前版本" + version, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("下载中");
                        progressDialog.show();
                        control.getExecutor().submit(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection con = null;
                                try {
                                    con = server.getHttpURLConnection(Path.DOWNLOAD_APK, RestConnection.POST);
                                    con.connect();
                                    installAPK(SDFileUtils.saveFileIntoSD(con.getInputStream(), "update", cv + "apk"));
                                    dialog.dismiss();
                                } catch (IOException e) {
                                    LogHelper.logException(e, "Download apk");
                                }
                            }
                        });

                    }
                }, context, null);
            }
        });
    }

    private void showPayDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("点赞？");
        ScrollView linearLayout = new ScrollView(context);
        TextView tv = new TextView(context);
        tv.setText("您的肯定是对我最好的鼓励！请关注作者的公众号<某鸟碎碎>，偶尔会发送兑换码哦！分享游戏到你的朋友圈、基友圈、游戏群、论坛，然后截图发送到公众号，可以获得兑换码。");
        tv.setAutoLinkMask(Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        linearLayout.addView(tv);
        dialog.setView(linearLayout);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                shareToNet();
            }

        });
        dialog.show();
    }
}