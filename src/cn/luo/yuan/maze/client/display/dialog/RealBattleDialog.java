package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.display.handler.ViewHandler;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.model.HarmAble;
import cn.luo.yuan.maze.model.real.RealLevel;
import cn.luo.yuan.maze.model.real.RealTimeState;
import cn.luo.yuan.maze.service.real.RealTimeManager;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyuan on 2017/9/7.
 */
public class RealBattleDialog {
    private RealTimeManager manager;
    private NeverEnd context;
    private View root;
    private Handler handler;
    private RealTimeState currentState;
    private AlertDialog dialog;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public RealBattleDialog(final RealTimeManager manager, NeverEnd context) {
        this.context = context;
        this.manager = manager;
        root = View.inflate(context.getContext(), R.layout.real_battle_layout, null);
        handler = new Handler();
        dialog = new AlertDialog.Builder(context.getContext()).create();
        dialog.setView(root);
        dialog.setCancelable(false);
        dialog.show();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int i =3;
                while ( i > 0){
                    final int finalI = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
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
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updateState(manager.pollState());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void showStartTip() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                root.findViewById(R.id.start_text).setVisibility(View.VISIBLE);
                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                root.findViewById(R.id.start_text).setVisibility(View.GONE);
                            }
                        });
                    }
                }, 500, TimeUnit.MILLISECONDS);
            }
        });
    }

    public void updateState(final RealTimeState state) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                HarmAble actioner = state.getActioner();
                HarmAble waiter = state.getWaiter();
                ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_timer), StringUtils.formatNumber(state.getRemainTime() / 1000));
                if (actioner != null && actioner.getId().equals(context.getHero().getId())) {
                    updateMyState(actioner, state.getActionerLevel(), state.getActionerPetIndex(), state.getActionerPoint(), state.getActionerHead());
                    updateTargetState(waiter, state.getWaiterLevel(), state.getWaiterPetIndex(), state.getWaiterPoint(), state.getWaiterHead());
                } else {
                    updateMyState(waiter, state.getWaiterLevel(), state.getWaiterPetIndex(), state.getWaiterPoint(), state.getWaiterHead());
                    updateTargetState(actioner, state.getActionerLevel(), state.getActionerPetIndex(), state.getActionerPoint(), state.getActionerHead());
                    targetAction();
                }
            }
        });
    }

    private void updateTimer(long time) {
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_timer), StringUtils.formatNumber(time));
    }

    private void targetAction() {
        ScrollView sv = (ScrollView) root.findViewById(R.id.detail_action_scroll);
        sv.setVisibility(View.GONE);
        TextView textView = (TextView) root.findViewById(R.id.action_tip);
        textView.setVisibility(View.VISIBLE);
        ViewHandler.setText(textView, "等待对方行动");
        root.findViewById(R.id.real_battle_atk_action).setEnabled(false);
        root.findViewById(R.id.real_battle_defend_action).setEnabled(false);
        root.findViewById(R.id.real_battle_skill_action).setEnabled(false);
        root.findViewById(R.id.real_battle_goods_action).setEnabled(false);
    }

    private void updateMyState(HarmAble my, long level, int petIndex, long point, String head) {
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_hp), StringUtils.formatNumber(my.getCurrentHp()));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_maxhp), StringUtils.formatNumber(my.getUpperHp()));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_point), StringUtils.formatNumber(point));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_my_level), RealLevel.Companion.getLevel(level));
        ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_my_img), Resource.loadImageFromAssets(head, true));
        ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_my_pet), Resource.loadMonsterImage(petIndex));
    }

    private void updateTargetState(HarmAble target, long level, int petIndex, long point, String head) {
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_target_hp), StringUtils.formatNumber(target.getCurrentHp()));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_targer_maxhp), StringUtils.formatNumber(target.getUpperHp()));
        ViewHandler.setText((TextView) root.findViewById(R.id.real_battle_target_level), RealLevel.Companion.getLevel(level));
        ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_targer_img), Resource.loadImageFromAssets(head, true));
        ViewHandler.setImage((ImageView) root.findViewById(R.id.real_battle_target_pet), Resource.loadMonsterImage(petIndex));
    }

}
