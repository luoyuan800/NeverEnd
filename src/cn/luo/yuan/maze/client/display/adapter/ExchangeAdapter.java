package cn.luo.yuan.maze.client.display.adapter;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.client.service.ClientPetMonsterHelper;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.client.utils.Resource;
import cn.luo.yuan.maze.utils.StringUtils;

import java.util.List;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class ExchangeAdapter extends BaseAdapter {
    private List<ExchangeObject> exchanges;
    private View.OnClickListener listener;
    private String buttonString = null;

    public void setExchanges(List<ExchangeObject> exchanges){
        this.exchanges = exchanges;
    }

    public ExchangeAdapter(List<ExchangeObject> exchanges, View.OnClickListener listener) {
        this.exchanges = exchanges;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return exchanges.size();
    }

    @Override
    public ExchangeObject getItem(int position) {
        return exchanges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(parent.getContext(), R.layout.swap_item_view, null);
        }
        ExchangeObject item = exchanges.get(position);
        if (item != view.getTag(R.string.item)) {
            ImageView imag = (ImageView) view.findViewById(R.id.item_img);
            if (item.getExchange() instanceof Pet) {
                imag.setVisibility(View.VISIBLE);
                imag.setImageDrawable(ClientPetMonsterHelper.loadMonsterImage(((Pet) item.getExchange()).getIndex()));
            } else {
                imag.setVisibility(View.GONE);
            }
            View iteName = view.findViewById(R.id.item_name);
            if (item.getExchange() instanceof NameObject) {
                iteName.setVisibility(View.VISIBLE);
                ((TextView) iteName).setText(Html.fromHtml(((NameObject) item.getExchange()).getDisplayName()));
            } else {
                iteName.setVisibility(View.GONE);
            }
            TextView swapName = (TextView) view.findViewById(R.id.swap_item_name);
            ImageView swapImg = (ImageView) view.findViewById(R.id.swap_item_img);
            if (item.getChanged()!=null){
                IDModel changeExchange = item.getChanged().getExchange();
                if(changeExchange instanceof NameObject) {
                    swapName.setVisibility(View.VISIBLE);
                    swapName.setText(Html.fromHtml(((NameObject) changeExchange).getDisplayName()));
                }else{
                    swapName.setVisibility(View.GONE);
                }
                if(changeExchange instanceof Pet){
                    swapImg.setVisibility(View.VISIBLE);
                    swapImg.setImageDrawable(ClientPetMonsterHelper.loadMonsterImage(((Pet) changeExchange).getIndex()));
                }else{
                    swapImg.setVisibility(View.GONE);
                }
            }else{
                swapName.setVisibility(View.VISIBLE);
                swapName.setText(item.getExpectedKeyWord());
                if(item.getExpectedType() == Field.PET_TYPE){
                    swapImg.setVisibility(View.VISIBLE);
                    swapImg.setImageDrawable(Resource.loadImageFromAssets("monster/wenhao.jpg", true));
                }else{
                    swapImg.setVisibility(View.GONE);
                }
            }
        }
        view.setTag(R.string.item, item);
        Button button = (Button)view.findViewById(R.id.get_back_item_button);
        button.setOnClickListener(listener);
        if(StringUtils.isNotEmpty(buttonString)){
            button.setText(buttonString);
        }
        button.setTag(R.string.item, item);
        button.setTag(R.string.adapter, ExchangeAdapter.this);
        return view;
    }

    public List<ExchangeObject> getExchanges() {
        return exchanges;
    }

    public void setButtonString(String buttonString) {
        this.buttonString = buttonString;
    }
}
