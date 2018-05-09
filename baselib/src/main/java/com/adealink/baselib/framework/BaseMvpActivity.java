package com.adealink.baselib.framework;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * MVP框架基类Activity
 * Created by Xuefu_Du on 2018/5/9.
 */
public abstract class BaseMvpActivity<V extends MvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P> {

    private Unbinder mUnbinder;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
