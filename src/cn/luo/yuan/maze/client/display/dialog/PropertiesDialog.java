package cn.luo.yuan.maze.client.display.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.client.service.NeverEnd;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.service.RangePropertiesHelper;
import cn.luo.yuan.maze.utils.StringUtils;

/**
 * Created by gluo on 5/31/2017.
 */
public class PropertiesDialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Dialog dialog;
    private NeverEnd context;
    private int maxPoint;
    private SeekBar strAddValue;
    private SeekBar agiAddValue;
    private TextView strAddShow;
    private TextView agiAddShow;
    Handler handler = new Handler();

    public PropertiesDialog(NeverEnd context) {
        this.context = context;
        long point = context.getHero().getPoint();
        if (point > Integer.MAX_VALUE) {
            maxPoint = Integer.MAX_VALUE;
        } else {
            maxPoint = (int) point;
        }
    }

    public void show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(context.getContext()).setTitle(R.string.properties_view_title).setView(R.layout.properties_view).show();
        }else {
            dialog = new AlertDialog.Builder(context.getContext()).setTitle(R.string.properties_view_title).setView(View.inflate(context.getContext(), R.layout.properties_view, null)).show();
        }
        dialog.findViewById(R.id.range_close).setOnClickListener(this);
        dialog.findViewById(R.id.range_conform).setOnClickListener(this);
        Hero hero = context.getHero();
        ((TextView) dialog.findViewById(R.id.point_value)).setText(StringUtils.formatNumber(context.getHero().getPoint()));
        strAddValue = (SeekBar) dialog.findViewById(R.id.str_add_value);
        strAddValue.setMax(maxPoint);
        strAddValue.setOnSeekBarChangeListener(this);
        agiAddValue = (SeekBar) dialog.findViewById(R.id.agi_add_value);
        agiAddValue.setMax(maxPoint);
        agiAddValue.setOnSeekBarChangeListener(this);
        ((TextView)dialog.findViewById(R.id.agi_value)).setText(StringUtils.formatNumber(context.getHero().getAgi()));
        ((TextView)dialog.findViewById(R.id.str_value)).setText(StringUtils.formatNumber(context.getHero().getStr()));
        strAddShow = (TextView) dialog.findViewById(R.id.str_add_show);
        agiAddShow = (TextView) dialog.findViewById(R.id.agi_add_show);
        ((TextView) dialog.findViewById(R.id.hero_atk)).setText(StringUtils.formatNumber(context.getHero().getUpperAtk()));
        ((TextView) dialog.findViewById(R.id.hero_def)).setText(StringUtils.formatNumber(hero.getUpperDef()));
        ((TextView) dialog.findViewById(R.id.hero_hp)).setText(StringUtils.formatNumber(hero.getCurrentHp()));
        dialog.findViewById(R.id.accessory_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accessory_layout:
                new AccessoriesDialog(context, null).show();
                break;
            case R.id.range_close:
                dialog.dismiss();
                break;
            case R.id.range_conform:
                int strPoint = strAddValue.getProgress();
                int agiPoint = agiAddValue.getProgress();
                if(agiPoint + strPoint <= context.getHero().getPoint()) {
                    if (agiPoint != 0) {
                        RangePropertiesHelper.addAgi(agiPoint, context);
                    }
                    if (strPoint != 0) {
                        RangePropertiesHelper.addStr(strPoint, context);
                    }
                    context.getHero().setPoint(context.getHero().getPoint() - strPoint - agiPoint);
                }
                dialog.dismiss();
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            switch (seekBar.getId()) {
                case R.id.str_add_value:
                    reRangeMax(progress, agiAddValue);
                    strAddShow.setText(StringUtils.formatNumber(progress));
                    break;
                case R.id.agi_add_value:
                    agiAddShow.setText(StringUtils.formatNumber(progress));
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
