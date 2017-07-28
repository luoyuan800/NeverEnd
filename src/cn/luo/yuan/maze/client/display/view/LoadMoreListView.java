package cn.luo.yuan.maze.client.display.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import cn.luo.yuan.maze.R;
import cn.luo.yuan.maze.utils.StringUtils;

public class LoadMoreListView extends ListView implements OnScrollListener,
        OnClickListener {
    private DListViewLoadingMore loadingMoreState = DListViewLoadingMore.LV_NORMAL;// 加载更多默认状态.
    private View mLoadingView;// 加载中...View(mFootView)
    private TextView mLoadMoreTextView;// 加载更多.(mFootView)
    private OnRefreshLoadingMoreListener onRefreshLoadingMoreListener;// 下拉刷新接口（自定义）
    private SearchView queryView;

    public LoadMoreListView(Context context) {
        super(context, null);
        initDragListView(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDragListView(context);
    }

    /**
     * 初始化ListView
     */
    public void initDragListView(Context context) {
        initLoadMoreView(context);// 初始化footer
        queryView = new SearchView(context);
        queryView.setVisibility(View.GONE);
        addHeaderView(queryView);
        setOnScrollListener(this);// ListView滚动监听
    }

    public void initQuery(final OnQueryChange onQueryChange){
        queryView.setVisibility(View.VISIBLE);
        queryView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(onQueryChange!=null){
                    onQueryChange.onQueryChange(query);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(StringUtils.isEmpty(newText)){
                    onQueryChange.onQueryChange(StringUtils.EMPTY_STRING);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 底部点击事件
     */
    @Override
    public void onClick(View v) {
        // 防止重复点击
        if (onRefreshLoadingMoreListener != null
                && loadingMoreState == DListViewLoadingMore.LV_NORMAL) {
            switchFooterViewState(DListViewLoadingMore.LV_LOADING);
            onRefreshLoadingMoreListener.onLoadMore(this);// 对外提供方法加载更多.
        }

    }

    /**
     * 点击加载更多
     *
     * @param flag 数据是否已全部加载完毕
     */
    public void onLoadMoreComplete(boolean flag) {
        if (flag) {
            switchFooterViewState(DListViewLoadingMore.LV_OVER);
        } else {
            switchFooterViewState(DListViewLoadingMore.LV_NORMAL);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

    /**
     * ListView 滑动监听
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void setOnLoadListener(OnRefreshLoadingMoreListener loadListener) {
        this.onRefreshLoadingMoreListener = loadListener;
    }

    // 点击加载更多枚举所有状态
    private enum DListViewLoadingMore {
        LV_LOADING, // 普通状态
        LV_NORMAL, // 加载状态
        LV_OVER // 结束状态
    }

    /**
     * 自定义接口
     */
    public interface OnRefreshLoadingMoreListener {
        /**
         * 点击加载更多
         */
        void onLoadMore(LoadMoreListView loadMoreListView);

    }

    public interface OnQueryChange {
        void onQueryChange(String query);
    }

    /**
     * 初始化底部加载更多控件
     */
    private void initLoadMoreView(Context context) {
        View mFootView = LayoutInflater.from(context).inflate(R.layout.footer, (ViewGroup) this.findViewById(R.id.load_more_view));

        View mLoadMoreView = mFootView.findViewById(R.id.load_more_view);

        mLoadMoreTextView = (TextView) mFootView
                .findViewById(R.id.load_more_tv);

        mLoadingView = mFootView
                .findViewById(R.id.loading_layout);

        mLoadMoreView.setOnClickListener(this);

        addFooterView(mFootView);
    }

    // 更新Footview视图
    private void switchFooterViewState(DListViewLoadingMore state) {
        switch (state) {
            // 普通状态
            case LV_NORMAL:
                mLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setText("查看更多");
                break;
            // 加载中状态
            case LV_LOADING:
                mLoadingView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setVisibility(View.GONE);
                break;
            // 加载完毕状态
            case LV_OVER:
                mLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setText("加载完毕");
                break;
            default:
                break;
        }
        loadingMoreState = state;
    }
}