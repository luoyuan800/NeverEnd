package cn.luo.yuan.maze.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.service.GameContext;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/31/2017.
 */
public class RangePointDialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Dialog dialog;
    private GameContext context;
    private int maxPoint;
    private SeekBar strAddValue;
    private SeekBar agiAddValue;

    public RangePointDialog(GameContext context) {
        this.context = context;
        long point = context.getHero().getPoint();
        if (point > Integer.MAX_VALUE) {
            maxPoint = Integer.MAX_VALUE;
        } else {
            maxPoint = (int) point;
        }
    }

    public void show() {
        dialog = new AlertDialog.Builder(context.getContext()).setTitle(R.string.rang_point_title).setView(R.layout.rang_point).show();
        dialog.findViewById(R.id.range_close).setOnClickListener(this);
        dialog.findViewById(R.id.range_conform).setOnClickListener(this);
        ((TextView) dialog.findViewById(R.id.point_value)).setText(StringUtils.formatNumber(context.getHero().getPoint()));
        strAddValue = (SeekBar) dialog.findViewById(R.id.str_add_value);
        strAddValue.setMax(maxPoint);
        strAddValue.setOnSeekBarChangeListener(this);
        agiAddValue = (SeekBar) dialog.findViewById(R.id.agi_add_value);
        agiAddValue.setMax(maxPoint);
        agiAddValue.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.range_close:
                dialog.dismiss();
                break;
            case R.id.range_conform:
                int strPoint = strAddValue.getProgress();
                int agiPoint = agiAddValue.getProgress();
                if (agiPoint != 0)
                    context.getHero().setAgi(context.getHero().getAgi() + agiPoint);
                if (strPoint != 0)
                    context.getHero().setStr(context.getHero().getStr() + strPoint);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            switch (seekBar.getId()) {
                case R.id.str_add_value:
                    reRangeMax(progress, agiAddValue);
                    break;
                case R.id.agi_add_value:
                    reRangeMax(progress, strAddValue);
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void reRangeMax(int progress, SeekBar ranger) {
        int remain = maxPoint - progress;
        ranger.setMax(remain);
    }
}
