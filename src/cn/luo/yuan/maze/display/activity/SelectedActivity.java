package cn.luo.yuan.maze.display.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.display.adapter.StringAdapter;
import cn.luo.yuan.maze.model.Element;
import cn.luo.yuan.maze.model.gift.Gift;
import cn.luo.yuan.maze.model.Hero;
import cn.luo.yuan.maze.model.HeroIndex;
import cn.luo.yuan.maze.persistence.DataManager;
import cn.luo.yuan.maze.persistence.IndexManager;
import cn.luo.yuan.maze.service.InfoControl;
import cn.luo.yuan.maze.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by luoyuan on 2017/3/18.
 */
public class SelectedActivity extends Activity implements View.OnClickListener,View.OnLongClickListener{
    private IndexManager indexManager;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_index);
        Resource.init(this);
        indexManager = new IndexManager(this);
        final List<HeroIndex>  indexList = indexManager.getIndex();
        StringAdapter<HeroIndex> adapter = new StringAdapter<>(indexList);
        adapter.setOnClickListener(this);
        adapter.setOnLongClickListener(this);
        ((ListView)findViewById(R.id.selected_hero_index)).setAdapter(adapter);
        findViewById(R.id.new_index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化Hero
                int index = indexList.size() + indexList.hashCode();
                final DataManager dataManager = new DataManager(index, SelectedActivity.this);
                final Hero hero = dataManager.loadHero();
                final AlertDialog dialog = new AlertDialog.Builder(SelectedActivity.this).create();
                dialog.setTitle(Resource.getString(R.string.name_input));
                dialog.show();
                dialog.setContentView(R.layout.init_hero);
                dialog.setCancelable(false);
                Spinner element = (Spinner) dialog.findViewById(R.id.select_element);
                ArrayAdapter<Element> fa = new ArrayAdapter<>(SelectedActivity.this, android.R.layout.simple_spinner_item, Arrays.asList(Element.values()));
                element.setAdapter(fa);
                Spinner gift = (Spinner) dialog.findViewById(R.id.select_gift);
                ArrayAdapter<Gift> gAdapter = new ArrayAdapter<>(SelectedActivity.this, android.R.layout.simple_spinner_item, Arrays.asList(Gift.values()));
                gift.setAdapter(gAdapter);
                gift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Gift g = gAdapter.getItem(position);
                        AlertDialog giftDetail = new AlertDialog.Builder(SelectedActivity.this).create();
                        giftDetail.setMessage(g.getDesc());
                        giftDetail.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.conform), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        giftDetail.show();
                    }
                });
                dialog.findViewById(R.id.init_hero).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hero.setName(((EditText)dialog.findViewById(R.id.hero_name)).getText().toString());
                        DatePicker picker = (DatePicker) dialog.findViewById(R.id.birthday);
                        GregorianCalendar calendar = new GregorianCalendar(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
                        hero.setBirthDay(calendar.getTimeInMillis());
                        Spinner element = (Spinner) dialog.findViewById(R.id.select_element);
                        hero.setElement((Element) element.getSelectedItem());
                        hero.setGift(((Gift)gift.getSelectedItem()).getName());
                        if(!StringUtils.isNotEmpty(hero.getName())){
                            dialog.show();
                        }else{
                            dataManager.saveHero(hero);
                            dialog.dismiss();
                            openGameView(index);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag(R.string.item);
        if(o instanceof HeroIndex){
            openGameView(((HeroIndex) o).getIndex());
        }
    }

    private void openGameView(int index){

    }

    @Override
    public boolean onLongClick(View v) {
        Object o = v.getTag(R.string.item);
        if(o instanceof HeroIndex) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setMessage(Resource.getString(R.string.delete_index) + ((HeroIndex) o).getName());
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, Resource.getString(R.string.conform), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DataManager dataManager = new DataManager(((HeroIndex) o).getIndex(), SelectedActivity.this);
                    dataManager.clean();
                }
            });
        }
        return false;
    }
}
