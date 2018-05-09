package com.adealink.library.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.adealink.library.R;
import com.adealink.library.adapter.HeaderAndFooterAdapter;
import com.adealink.library.adapter.RecyclerViewAdapter;

/**
 * 加载更多功能；
 */
public class LoadMoreRecyclerView extends RecyclerView {

    //触发在上下滑动监听器的容差距离
    private final int HIDE_THRESHOLD = 20;
    //“没有更多了”的展示动画的时间
    private final int ANIM_DURATION = 1000;//mills

    private boolean mLoadMoreEnabled = true;
    private boolean mLoadingData = false;//是否正在加载数据
    private boolean isNoMore = false;//是否没有更多数据了 true:没有更多了
    private boolean mCanShowNoMore = true;//是否可以展示“没有更多了”
    private Animation mAnimation;//“没有更多了”的展示动画
    private boolean mIsAnimShowing;//“没有更多了”的展示动画是否正在执行

    private LoadMoreView mLoadMoreView;
    protected LayoutManagerType layoutManagerType;//当前RecyclerView类型
    private int lastVisibleItemPosition;//最后一个可见的item的位置
    private int[] lastPositions;//最后一个的位置
    private int mScrolledYDistance = 0;//Y轴移动的实际距离（最顶部为0）
    private int mScrolledXDistance = 0;//X轴移动的实际距离（最左侧为0）
    private boolean mIsScrollDown;//true:列表下滑  false:列表上滑
    private int mCurScrollState = RecyclerView.SCROLL_STATE_IDLE;//当前滑动的状态

    private OnLoadMoreListener mLoadMoreListener;
    private RecyclerScrollListener mRecyclerScrollListener;

    //列表滚动的监听
    public void setRecyclerScrollListener(RecyclerScrollListener listener) {
        mRecyclerScrollListener = listener;
    }

    public interface RecyclerScrollListener {
        void onScrollUp();//scroll down to up

        void onScrollDown();//scroll from up to down

        void onScrolled(int distanceX, int distanceY);// moving state,you can get the move distance

        void onScrollStateChanged(int state);
    }

    //简化滚动监听类-可以自觉选择实现的方法
    public static abstract class RecyclerScrollSimpleListener implements RecyclerScrollListener {

        @Override
        public void onScrollUp() {
        }

        @Override
        public void onScrollDown() {
        }

        @Override
        public void onScrolled(int distanceX, int distanceY) {
        }

        @Override
        public void onScrollStateChanged(int state) {
        }
    }

    //加载更多的监听
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    //重新加载的监听
    public void setOnRetryListener(final View.OnClickListener listener) {
        mLoadMoreView.setOnRetry(listener);
    }

    //--------展示“没有更多了”--------Start
    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            mIsAnimShowing = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mIsAnimShowing = false;
            if (mLoadMoreView != null) {
                mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_HIDE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 延迟一定时间-置为LoadMoreView.STATUS_HIDE状态
     */
    private void hideLoadMoreWithDelay() {
        if (mIsAnimShowing) {
            return;
        }
        mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_THEEND);
        mAnimation.setAnimationListener(mAnimationListener);
        mAnimation.setDuration(ANIM_DURATION);
        mLoadMoreView.startAnimation(mAnimation);
    }
    //--------展示“没有更多了”--------End

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mAnimation = AnimationUtils.loadAnimation(context, R.anim.listview_loadmore_fadeout_anim);
        if (mLoadMoreEnabled) {
            setLoadMoreFooter(new LoadMoreView(getContext()));
        }
    }

    /**
     * @param loadMoreFooter 设置自定义的“加载更多”的footerview
     */
    private void setLoadMoreFooter(LoadMoreView loadMoreFooter) {
        this.mLoadMoreView = loadMoreFooter;

        //Ps:mFootView inflate的时候没有以RecyclerView为parent，所以要设置LayoutParams
        ViewGroup.LayoutParams layoutParams = mLoadMoreView.getLayoutParams();
        if (layoutParams != null) {
            mLoadMoreView.setLayoutParams(new LayoutParams(layoutParams));
        } else {
            mLoadMoreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * @return 获取LoadMoreView 可以设置自定义布局
     */
    public LoadMoreView getLoadMoreView() {
        return mLoadMoreView;
    }

    /**
     * @param config 配置“加载更多”的样式
     */
    public void setLoadMoreStyleConfig(LoadMoreStyleConfig config) {
        if (null != mLoadMoreView) {
            mLoadMoreView.setLoadMoreStyleConfig(config);
        }
    }

    /**
     * @param enabled “加载更多”是否可用
     */
    public void setLoadMoreEnabled(boolean enabled) {
        mLoadMoreEnabled = enabled;
        if (!enabled) {
            mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_HIDE);
        }
    }

    /**
     * @param noMore 设置是否已加载全部； true：没有更多数据了
     */
    public void setNoMore(boolean noMore) {
        mLoadingData = false;
        isNoMore = noMore;
        if (isNoMore) {
            mCanShowNoMore = true;
            //空列表时不显示“没有更多了”
            Adapter adapter = getAdapter();
            if (adapter != null && adapter.getItemCount() > 0) {
                hideLoadMoreWithDelay();
            }
        } else {
            mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_HIDE);
        }
    }

    /**
     * 加载错误
     */
    public void showError() {
        mLoadingData = false;
        mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_ERROR);
    }

    /**
     * 展示正在加载中状态
     */
    private void showLoadingMore() {
        mLoadingData = true;
        mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_LOADING);
    }

    /**
     * 加载数据完成
     */
    public void loadComplete() {
        if (mLoadingData) {
            mLoadingData = false;
            mLoadMoreView.showViewByStatus(LoadMoreView.STATUS_HIDE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (null == adapter) return;
        if (adapter instanceof HeaderAndFooterAdapter) {
            HeaderAndFooterAdapter wrapAdapter = (HeaderAndFooterAdapter) adapter;
            if (mLoadMoreEnabled && wrapAdapter.getFootersCount() == 0) {
                wrapAdapter.addFooterView(mLoadMoreView);
            }
            super.setAdapter(wrapAdapter);
        } else if (adapter instanceof RecyclerViewAdapter) {
            RecyclerViewAdapter wrapAdapter = (RecyclerViewAdapter) adapter;
            if (mLoadMoreEnabled && wrapAdapter.getFootersCount() == 0) {
                wrapAdapter.addFooterView(mLoadMoreView);
            }
            if (mLoadMoreEnabled) {
                super.setAdapter(wrapAdapter.getHeaderAndFooterAdapter());
            } else {
                super.setAdapter(wrapAdapter);
            }
        } else {
            throw new RuntimeException("adapter is not or RecyclerViewAdapter or HeaderAndFooterAdapter!");
        }

    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        int firstVisibleItemPosition = 0;
        LayoutManager layoutManager = getLayoutManager();

        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LinearLayout;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GridLayout;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.StaggeredGridLayout;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LinearLayout:
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GridLayout:
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case StaggeredGridLayout:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(lastPositions);
                firstVisibleItemPosition = findMax(lastPositions);
                break;
        }

        // 根据类型来计算出第一个可见的item的位置，由此判断是否触发到底部的监听器
        // 计算并判断当前是向上滑动还是向下滑动
        calculateScrollUpOrDown(firstVisibleItemPosition, dy);
        // 移动距离超过一定的范围，我们监听就没有啥实际的意义了
        mScrolledXDistance += dx;
        mScrolledYDistance += dy;
        mScrolledXDistance = (mScrolledXDistance < 0) ? 0 : mScrolledXDistance;
        mScrolledYDistance = (mScrolledYDistance < 0) ? 0 : mScrolledYDistance;
        if (mIsScrollDown && (dy == 0)) {
            mScrolledYDistance = 0;
        }
        //Be careful in here
        if (null != mRecyclerScrollListener) {
            mRecyclerScrollListener.onScrolled(mScrolledXDistance, mScrolledYDistance);
        }

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        mCurScrollState = state;

        if (mRecyclerScrollListener != null) {
            mRecyclerScrollListener.onScrollStateChanged(state);
        }

        if (mLoadMoreListener != null && mLoadMoreEnabled) {
            if (mCurScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                LayoutManager layoutManager = getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (visibleItemCount > 0
                        && lastVisibleItemPosition >= totalItemCount - 1
                        && totalItemCount > visibleItemCount) {
                    //没有更多数据了&&可以展示“没有更多了”动画
                    if (isNoMore && mCanShowNoMore) {
                        mCanShowNoMore = false;
                        hideLoadMoreWithDelay();
                        return;
                    }
                    //还有更多数据可以加载
                    if (!isNoMore) {
                        mLoadMoreView.setVisibility(View.VISIBLE);
                        if (mLoadingData) {
                            return;
                        } else {
                            showLoadingMore();
                            mLoadMoreListener.onLoadMore();
                        }
                    }

                }
            }
        }

    }

    /**
     * 计算当前是向上滑动还是向下滑动
     */
    private void calculateScrollUpOrDown(int firstVisibleItemPosition, int dy) {
        mIsScrollDown = dy <= 0;
        if (null != mRecyclerScrollListener && Math.abs(dy) > HIDE_THRESHOLD) {
            if (mIsScrollDown) {
                mRecyclerScrollListener.onScrollDown();
            } else {
                mRecyclerScrollListener.onScrollUp();
            }
        }
    }

    /**
     * @param lastPositions 数组-最后一个的位置
     * @return lastPositions数组中最大的一个
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public enum LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }

}