package com.adealink.loadmoresample.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.adealink.baselib.framework.BaseMvpActivity;
import com.adealink.baselib.utils.LogUtil;
import com.adealink.library.view.LoadMoreRecyclerView;
import com.adealink.loadmoresample.R;
import com.adealink.loadmoresample.presenter.LoadMoreRecyclerPresenter;
import com.adealink.loadmoresample.view.LoadMoreMvpView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Xuefu_Du on 2018/5/9.
 */
public class LoadMoreRecyclerActivity extends BaseMvpActivity<LoadMoreMvpView, LoadMoreRecyclerPresenter>
        implements LoadMoreMvpView {

    @BindView(R.id.recycle)
    LoadMoreRecyclerView mRecyclerView;

    @NonNull
    @Override
    public LoadMoreRecyclerPresenter createPresenter() {
        return new LoadMoreRecyclerPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more_recycler);
        ButterKnife.bind(this);

        LogUtil.d("d_slience", "mRecyclerView:" + mRecyclerView);
        LogUtil.d("d_slience", "mRecyclerView == null:" + (mRecyclerView == null));
    }


}
