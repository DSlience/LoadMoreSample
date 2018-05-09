package com.adealink.library.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adealink.library.R;


/**
 * 展示列表加载更多状态的View
 */
public class LoadMoreView extends RelativeLayout {

    //状态列举
    public static final int STATUS_HIDE = 0x00;
    public static final int STATUS_LOADING = 0x01;
    public static final int STATUS_ERROR = 0x02;
    public static final int STATUS_THEEND = 0x03;

    private View mLoadingView;
    private View mErrorView;
    private View mTheEndView;

    private LoadMoreStyleConfig mLoadMoreStyleConfig;

    private int mViewStatus;

    private OnClickListener mOnRetryClickListener;

    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.recycler_load_more_view, this);
        setOnClickListener(null);

        //2017-10-30 暂时不去设置默认的LoadMoreView的样式，即使用xml中的布局；
//        mLoadMoreStyleConfig = new LoadMoreStyleConfig.Build().build();

        showViewByStatus(STATUS_HIDE);//初始为隐藏状态
    }

    public int getViewStatus() {
        return mViewStatus;
    }

    public void setLoadMoreStyleConfig(LoadMoreStyleConfig config) {
        this.mLoadMoreStyleConfig = config;
    }

    public void setLoadingView(View mLoadingView) {
        this.mLoadingView = mLoadingView;
    }

    public void setErrorView(View mErrorView) {
        this.mErrorView = mErrorView;
        if (null != mOnRetryClickListener) {
            this.mErrorView.setOnClickListener(mOnRetryClickListener);
        }
    }

    public void setTheEndView(View mTheEndView) {
        this.mTheEndView = mTheEndView;
    }

    public void showViewByStatus(int viewStatus) {
        if (mViewStatus == viewStatus) {
            return;
        }
        mViewStatus = viewStatus;

        if (viewStatus == STATUS_HIDE) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);

        if (null != mLoadMoreStyleConfig) {
            this.setBackgroundColor(ContextCompat.getColor(getContext(), mLoadMoreStyleConfig.getBgColor()));
        }

        switch (viewStatus) {
            case STATUS_LOADING:
                if (null == mLoadingView) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.loading_viewstub);
                    mLoadingView = viewStub.inflate();

                    if (null != mLoadMoreStyleConfig) {
                        try {
                            TextView tv_loading = (TextView) mLoadingView.findViewById(R.id.tv_loading);
                            tv_loading.setTextColor(ContextCompat.getColor(getContext(), mLoadMoreStyleConfig.getTextColor()));
                            tv_loading.setText(mLoadMoreStyleConfig.getTipLoading());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                break;
            case STATUS_ERROR:
                if (null == mErrorView) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.error_viewstub);
                    mErrorView = viewStub.inflate();

                    if (null != mLoadMoreStyleConfig) {
                        try {
                            TextView tv_error = (TextView) mErrorView.findViewById(R.id.tv_error);
                            tv_error.setTextColor(ContextCompat.getColor(getContext(), mLoadMoreStyleConfig.getTextColor()));
                            tv_error.setText(mLoadMoreStyleConfig.getTipError());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (null != mOnRetryClickListener) {
                        mErrorView.setOnClickListener(mOnRetryClickListener);
                    }
                }
                break;
            case STATUS_THEEND:
                if (null == mTheEndView) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.end_viewstub);
                    mTheEndView = viewStub.inflate();

                    if (null != mLoadMoreStyleConfig) {
                        try {
                            TextView tv_end = (TextView) mTheEndView.findViewById(R.id.tv_end);
                            tv_end.setTextColor(ContextCompat.getColor(getContext(), R.color.recycler_no_more));
                            tv_end.setText(mLoadMoreStyleConfig.getTipEnd());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }

        if (null != mLoadingView) {
            mLoadingView.setVisibility(viewStatus == STATUS_LOADING ? View.VISIBLE : View.GONE);
        }
        if (null != mErrorView) {
            mErrorView.setVisibility(viewStatus == STATUS_ERROR ? View.VISIBLE : View.GONE);
        }
        if (null != mTheEndView) {
            mTheEndView.setVisibility(viewStatus == STATUS_THEEND ? View.VISIBLE : View.GONE);
        }
    }

    public void setOnRetry(OnClickListener listener) {
        this.mOnRetryClickListener = listener;
    }

}