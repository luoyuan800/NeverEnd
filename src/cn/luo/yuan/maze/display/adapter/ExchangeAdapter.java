package cn.luo.yuan.maze.display.adapter;

import android.provider.ContactsContract;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.model.*;
import cn.luo.yuan.maze.service.PetMonsterLoder;
import cn.luo.yuan.maze.utils.Field;
import cn.luo.yuan.maze.utils.Resource;

import java.util.List;

/**
 * Created by luoyuan on 2017/6/25.
 */
public class ExchangeAdapter extends BaseAdapter {
    private List<ExchangeObject> exchanges;
    private View.OnClickListener listener;

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
            view = View.inflate(parent.getContext(), R.layout.swap_item_view, parent);
        }
        ExchangeObject item = exchanges.get(position);
        if (item != view.getTag(R.string.item)) {
            ImageView imag = (ImageView) view.findViewById(R.id.item_img);
            if (item.getExchange() instanceof Pet) {
                imag.setVisibility(View.VISIBLE);
                imag.setImageDrawable(PetMonsterLoder.loadMonsterImage(((Pet) item.getExchange()).getIndex()));
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
                    swapImg.setImageDrawable(PetMonsterLoder.loadMonsterImage(((Pet) changeExchange).getIndex()));
                }else{
                    swapImg.setVisibility(View.GONE);
                }
            }else{
                swapName.setVisibility(View.VISIBLE);
                swapName.setText(item.getExpectedKeyWord());
                if(item.getExpectedType() == Field.PET_TYPE){
                    swapImg.setVisibility(View.VISIBLE);
                    swapImg.setImageDrawable(Resource.loadImageFromAssets("monster/wenhao.jpg"));
                }else{
                    swapImg.setVisibility(View.GONE);
                }
            }
        }
        view.setTag(R.string.item, item);
        view.findViewById(R.id.get_back_item_button).setOnClickListener(listener);
        return view;
    }

    public List<ExchangeObject> getExchanges() {
        return exchanges;
    }
}
