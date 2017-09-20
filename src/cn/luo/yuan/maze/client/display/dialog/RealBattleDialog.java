package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.adapter.StringAdapter;
import cn.luo.yuan.maze.client.display.handler.ViewHandler;
import cn.luo.yuan.maze.client.display.view.RollTextView;
import cn.luo.yuan.maze.client.display.view.SpringProgressView;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.LogHelper;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.Data;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.LevelRecord;
import cn.luo.yuan.maze.model.NPCLevelRecord;
import cn.luo.yuan.maze.model.NameObject;
import cn.luo.yuan.maze.model.real.BattleEnd;
import cn.luo.yuan.maze.model.real.Battling;
import cn.luo.yuan.maze.model.real.RealState;
import cn.luo.yuan.maze.model.real.RealTimeState;
import cn.luo.yuan.maze.model.real.action.RealTimeAction;
import cn.luo.yuan.maze.model.skill.AtkSkill;
import cn.luo.yuan.maze.model.skill.DefSkill;
import cn.luo.yuan.maze.model.skill.Skill;
import cn.luo.yuan.maze.service.real.RealTimeManager;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * CopyRight @Luo
 * Created by luoyuan on 2017/9/7.
 */
public class RealBattleDialog implements View.OnClickListener {
    private RealTimeManager manager;
    private NeverEnd context;
    private View root;
    private Handler handler;
    private RealState currentState;
    private AlertDialog main;
    private HarmAble my;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    private Dialog currentShowingSubDialog;
    private String id;
    private String lastActionId;

    public RealBattleDialog(final RealTimeManager manager, NeverEnd context, String battleId) {
        this.id = battleId;
        this.context = context;
        this.manager = manager;
        root = View.inflate(context.getContext(), R.layout.real_battle_layout, null);
        handler = new Handler();
        main = new AlertDialog.Builder(context.getContext()).create();
        main.setView(root);
        main.setCancelable(false);
        main.show();
        root.findViewById(R.id.real_battle_atk_action).setOnClickListener(this);
        root.findViewById(R.id.real_battle_defend_action).setOnClickListener(this);
        root.findViewById(R.id.real_battle_skill_action).setOnClickListener(this);
        root.findViewById(R.id.real_battle_goods_action).setOnClickListener(this);
        my = context.getHero();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    updateState(manager.pollState());
                } catch (Exception e) {
                    LogHelper.logException(e, "Poll state");
                }
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int i = 3;
                while (i > 0) {
                    final int finalI = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            targetAction();
                            updateTimer(finalI);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                showStartTip();
            }
        });

        root.findViewById(R.id.real_battle_close).setOnClickListener(this);
    }

    public void stop() {
        targetAction();
        if (!executor.isShutdown()) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        executor.shutdown();
                        manager.quit();
                        executor.awaitTermination(2, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        LogHelper.logException(e, "Stop real battle");
                    }
                }
            });
        }
    }

    public void updateState(final RealState state) {
        if (state instanceof RealTimeState) {
            RealTimeAction action = ((RealTimeState) state).getAction();
            if(action!=null && action.getId().equals(lastActionId)){
                return;
            }
            currentState = state;
            lastActionId = state.getId();
            manager.setId(state.getId());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (state instanceof BattleEnd) {
                            HarmAble winner = ((BattleEnd) state).getWinner();
                            HarmAble loser = ((BattleEnd) state).getLoser();
                            if (winner != null && loser != null) {
                                if (winner.getId().equals(my.getId())) {
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            IAmWinner((BattleEnd) state);
                                        }
                                    });
                                } else {
                                    executor.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            IAmLoser((BattleEnd) state);
                                        }
                                    });
                                }
                                stop();
                            }
                        } else if (state instanceof Battling) {
                            HarmAble actioner = ((Battling) state).getActioner();
                            HarmAble waiter = ((Battling) state).getWaiter();
                            ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_timer), StringUtils.formatNumber(((Battling) state).getRemainTime()));
                            if (actioner != null && waiter != null && actioner.getId().equals(my.getId())) {
                                updateMyState(actioner, ((Battling) state).getActionerLevel(), ((Battling) state).getActionerPetIndex(), ((Battling) state).getActionerPoint(), ((Battling) state).getActionerHead());
                                updateTargetState(waiter, ((Battling) state).getWaiterLevel(), ((Battling) state).getWaiterPetIndex(), ((Battling) state).getWaiterPoint(), ((Battling) state).getWaiterHead());
                                myAction();
                            } else if (actioner != null && waiter != null) {
                                updateMyState(waiter, ((Battling) state).getWaiterLevel(), ((Battling) state).getWaiterPetIndex(), ((Battling) state).getWaiterPoint(), ((Battling) state).getWaiterHead());
                                updateTargetState(actioner, ((Battling) state).getActionerLevel(), ((Battling) state).getActionerPetIndex(), ((Battling) state).getActionerPoint(), ((Battling) state).getActionerHead());
                                targetAction();
                            }
                        }
                    } catch (Exception e) {
                        LogHelper.logException(e, "update state");
                    }
                    updateMessage((RealTimeState) state);
                }
            });
        }else {
            stop();
            main.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.real_battle_close:
                SimplerDialogBuilder.build("确认退出吗？如果战斗中强行退出会被判断失败！", Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stop();
                        dialog.dismiss();
                        main.dismiss();

                    }
                }, Resource.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, context.getContext());
                break;
            case R.id.real_battle_atk_action:
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        manager.atkAction();
                    }
                });
                targetAction();
                break;
            case R.id.real_battle_skill_action:
                showAtkSkill();
                break;
            case R.id.real_battle_defend_action:
                showDefSkill();
                break;
        }
    }

    private void showAtkSkill() {
        if(currentState instanceof RealTimeState) {
            final List<AtkSkill> atkSkills = new ArrayList<>(6);
            final List<String> skillName = new ArrayList<>(6);
            for (Skill skill : context.getHero().getSkills()) {
                if (skill instanceof AtkSkill) {
                    atkSkills.add((AtkSkill) skill);
                    int skillActionPoint = Data.getSkillActionPoint(skill);
                    skillName.add("<font color='" + (skillActionPoint <= ((RealTimeState)currentState).getActionerPoint() ? "" : "#b4a6b0") + "'>" + skill.getName() + "</font> - " + skillActionPoint);
                }
            }
            StringAdapter<String> adapter = new StringAdapter<>(skillName);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int index = (Integer) v.getTag(R.string.position);
                        final AtkSkill skill = atkSkills.get(index);
                        if (skill != null) {
                            if (Data.getSkillActionPoint(skill) <= ((RealTimeState)currentState).getActionerPoint()) {
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        manager.useAtkSkillAction(skill);
                                    }
                                });
                                targetAction();
                            }
                        }
                    } catch (Exception e) {
                        LogHelper.logException(e, "action skill");
                    }
                }
            });
            LinearLayout sv = (LinearLayout) root.findViewById(R.id.detail_action_scroll);
            sv.removeAllViews();
            ListView listView = new ListView(context.getContext());
            listView.setAdapter(adapter);
            sv.addView(listView);
            sv.setVisibility(View.VISIBLE);
            root.findViewById(R.id.action_tip).setVisibility(View.GONE);
        }
    }

    private void showDefSkill() {
        if(currentState instanceof RealTimeState) {
            final List<DefSkill> defSkills = new ArrayList<>(6);
            final List<String> skillName = new ArrayList<>(6);
            for (Skill skill : context.getHero().getSkills()) {
                if (skill instanceof DefSkill) {
                    defSkills.add((DefSkill) skill);
                    int skillActionPoint = Data.getSkillActionPoint(skill);
                    skillName.add("<font color='" + (skillActionPoint <= ((RealTimeState)currentState).getActionerPoint() ? "" : "#b4a6b0") + "'>" + skill.getName() + "</font> - " + skillActionPoint);
                }
            }
            StringAdapter<String> adapter = new StringAdapter<>(skillName);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int index = (Integer) v.getTag(R.string.position);
                        final DefSkill skill = defSkills.get(index);
                        if (skill != null) {
                            if (Data.getSkillActionPoint(skill) <= ((RealTimeState)currentState).getActionerPoint()) {
                                executor.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        manager.useDefSkillAction(skill);
                                    }
                                });
                                targetAction();
                            }
                        }
                    } catch (Exception e) {
                        LogHelper.logException(e, "action skill");
                    }
                }
            });
            LinearLayout sv = (LinearLayout) root.findViewById(R.id.detail_action_scroll);
            sv.removeAllViews();
            ListView listView = new ListView(context.getContext());
            listView.setAdapter(adapter);
            sv.addView(listView);
            sv.setVisibility(View.VISIBLE);
            root.findViewById(R.id.action_tip).setVisibility(View.GONE);
        }
    }

    private void showStartTip() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                manager.ready();
            }
        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                final View tv = root.findViewById(R.id.start_text);
                ViewHandler.setText((TextView) tv, "开始！");
                tv.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv.setVisibility(View.GONE);
                    }
                }, 500);
            }
        });
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updateState(manager.pollState());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    private void IAmLoser(BattleEnd state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                root.findViewById(R.id.real_battle_timer).setVisibility(View.INVISIBLE);
                TextView textView = (TextView) root.findViewById(R.id.start_text);
                textView.setVisibility(View.VISIBLE);
                ViewHandler.setText(textView, R.string.real_lost);
                root.findViewById(R.id.real_battle_close).setVisibility(View.VISIBLE);
            }
        });

        LevelRecord targetRecord = manager.getTargetRecord();
        if(targetRecord instanceof NPCLevelRecord){
            long turn = manager.getTurn();
            if(turn < 3){
                final String s = ((NPCLevelRecord) targetRecord).getTips().get("win1");
                if(StringUtils.isNotEmpty(s)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog dialog = new MessageDialog(context, Collections.singletonList(s));
                            dialog.show();
                        }
                    });
                }
            }else {
                final String s = ((NPCLevelRecord) targetRecord).getTips().get("win");
                if(StringUtils.isNotEmpty(s)){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog dialog = new MessageDialog(context, Collections.singletonList(s));
                            dialog.show();
                        }
                    });
                }
            }
        }
    }

    private void IAmWinner(final BattleEnd state) {
        if (currentShowingSubDialog == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    root.findViewById(R.id.real_battle_timer).setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) root.findViewById(R.id.start_text);
                    textView.setVisibility(View.VISIBLE);
                    ViewHandler.setText(textView, R.string.real_win);
                }
            });

            LevelRecord targetRecord = manager.getTargetRecord();
            if(targetRecord instanceof NPCLevelRecord) {
                long turn = manager.getTurn();
                final String s;
                if (turn < 3) {
                    s = ((NPCLevelRecord) targetRecord).getTips().get("lost1");
                    state.setAwardMate(3000);
                } else {
                    s = ((NPCLevelRecord) targetRecord).getTips().get("lost");
                    state.setAwardMate(2000);
                }
                if (StringUtils.isNotEmpty(s)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MessageDialog dialog = new MessageDialog(context, Collections.singletonList(s));
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (StringUtils.isNotEmpty(state.getUpgradeTip())) {
                                                currentShowingSubDialog = SimplerDialogBuilder.build(state.getUpgradeTip(), Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        main.dismiss();
                                                    }
                                                }, context.getContext(), context.getRandom());
                                            } else if (StringUtils.isNotEmpty(state.getWinTip())) {
                                                currentShowingSubDialog = SimplerDialogBuilder.build(state.getWinTip(), Resource.getString(R.string.conform), context.getContext(), context.getRandom());
                                            }

                                        }
                                    }, 100);
                                }
                            });
                            dialog.show();
                        }
                    });
                }
            }

            if (state.getAwardMate() > 0) {
                context.getHero().setMaterial(context.getHero().getMaterial() + state.getAwardMate());
            }
            if (state.getAwardPoint() > 0) {
                context.getHero().setPoint(context.getHero().getPoint() + state.getAwardPoint());
            }
        }
    }


    private void updateMessage(RealTimeState state) {
        if (state.getMsg().size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : state.getMsg()) {
                sb.append(s).append("<br>");
            }
            RollTextView rollTextView = (RollTextView) root.findViewById(R.id.real_battle_info);
            rollTextView.addMessage(sb.toString());

        }
    }

    private void updateTimer(long time) {
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_timer), StringUtils.formatNumber(time));
    }

    private void targetAction() {
        LinearLayout sv = (LinearLayout) root.findViewById(R.id.detail_action_scroll);
        sv.setVisibility(View.GONE);
        TextView textView = (TextView) root.findViewById(R.id.action_tip);
        textView.setVisibility(View.VISIBLE);
        ViewHandler.setText(textView, R.string.real_wait_action);
        root.findViewById(R.id.real_battle_atk_action).setEnabled(false);
        root.findViewById(R.id.real_battle_defend_action).setEnabled(false);
        root.findViewById(R.id.real_battle_skill_action).setEnabled(false);
        root.findViewById(R.id.real_battle_goods_action).setEnabled(false);
    }

    private void myAction() {
        if (!root.findViewById(R.id.real_battle_atk_action).isEnabled()) {
            root.findViewById(R.id.real_battle_atk_action).setEnabled(true);
            root.findViewById(R.id.real_battle_defend_action).setEnabled(true);
            root.findViewById(R.id.real_battle_skill_action).setEnabled(true);
            root.findViewById(R.id.real_battle_goods_action).setEnabled(true);
            TextView textView = (TextView) root.findViewById(R.id.action_tip);
            textView.setVisibility(View.VISIBLE);
            ViewHandler.setText(textView, R.string.real_my_action);
        }
    }

    private void updateMyState(HarmAble my, long level, List<Integer> petIndex, long point, String head) {
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_hp), StringUtils.formatNumber(my.getCurrentHp()));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_maxhp), StringUtils.formatNumber(my.getUpperHp()));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_point), StringUtils.formatNumber(point));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_name), my instanceof NameObject ? ((NameObject) my).getDisplayName() : my.toString());
        if (level >= 0) {
            ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_level), StringUtils.formatRealLevel(level, my.getRace()));
        } else {
            root.findViewById(R.id.real_battle_my_level).setVisibility(View.INVISIBLE);
        }
        ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_my_img), Resource.loadImageFromAssets(head, true));
        if (petIndex.size() > 0)
            ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_my_pet), Resource.loadMonsterImage(petIndex.get(0)));
        else {
            root.findViewById(R.id.real_battle_my_pet).setVisibility(View.INVISIBLE);
        }
    }

    private void updateTargetState(HarmAble target, long level, List<Integer> petIndex, long point, String head) {
        SpringProgressView hpView = (SpringProgressView) root.findViewById(R.id.real_battle_target_hp);
        hpView.setMaxCount(100);
        hpView.setCurrentCount(target.getCurrentHp() * 100f / target.getUpperHp());
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_targer_name), my instanceof NameObject ? ((NameObject) target).getDisplayName() : target.toString());
        if (level >= 0) {
            ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_target_level), StringUtils.formatRealLevel(level, target.getRace()));
        } else {
            root.findViewById(R.id.real_battle_target_level).setVisibility(View.INVISIBLE);
        }
        Drawable img = Resource.loadImageFromAssets(head, true);
        if (img == null) {
            img = Resource.loadMonsterImage(head);
        }
        ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_targer_img), img);
        if (petIndex.size() > 0) {
            ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_target_pet), Resource.loadMonsterImage(petIndex.get(0)));
        } else {
            root.findViewById(R.id.real_battle_target_pet).setVisibility(View.INVISIBLE);
        }
    }


}
