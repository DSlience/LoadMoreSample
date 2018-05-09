package com.adealink.library.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xuefu_Du on 2017/7/9.
 */
public abstract class RecyclerViewAdapter<M, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<M> mDataList = new ArrayList<>();
    protected HeaderAndFooterAdapter mHeaderAndFooterAdapter;

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * @param position
     * @return 获取指定索引位置的数据模型
     */
    public M getItem(int position) {
        return mDataList.get(position);
    }

    /**
     * @return 获取数据集合
     */
    public List<M> getDataList() {
        return mDataList;
    }

    public final void notifyItemRangeInsertedWrapper(int positionStart, int itemCount) {
        if (mHeaderAndFooterAdapter == null) {
            notifyItemRangeInserted(positionStart, itemCount);
        } else {
            mHeaderAndFooterAdapter.notifyItemRangeInserted(mHeaderAndFooterAdapter.getHeadersCount() + positionStart, itemCount);
        }
    }

    /**
     * @param data 在集合头部添加新的数据集合
     */
    public void addNewData(List<M> data) {
        if (data != null && !data.isEmpty()) {
            mDataList.addAll(0, data);
            notifyItemRangeInsertedWrapper(0, data.size());
        }
    }

    /**
     * @param data 在集合尾部添加更多数据集合
     */
    public void addMoreData(List<M> data) {
        if (data != null && !data.isEmpty()) {
            int positionStart = mDataList.size();
            mDataList.addAll(mDataList.size(), data);
            notifyItemRangeInsertedWrapper(positionStart, data.size());
        }
    }

    public final void notifyDataSetChangedWrapper() {
        if (mHeaderAndFooterAdapter == null) {
            notifyDataSetChanged();
        } else {
            mHeaderAndFooterAdapter.notifyDataSetChanged();
        }
    }

    /**
     * @param data 设置全新的数据集合，如果传入空（null或空表），则清空数据列表
     *             （第一次从服务器加载数据，或者下拉刷新当前界面数据表）
     */
    public void setData(List<M> data) {
        mDataList.clear();
        if (data != null && !data.isEmpty()) {
            mDataList.addAll(data);
        }
        notifyDataSetChangedWrapper();
    }

    /**
     * 清空数据列表
     */
    public void clear() {
        mDataList.clear();
        notifyDataSetChangedWrapper();
    }

    public final void notifyItemRemovedWrapper(int position) {
        if (mHeaderAndFooterAdapter == null) {
            notifyItemRemoved(position);
        } else {
            mHeaderAndFooterAdapter.notifyItemRemoved(mHeaderAndFooterAdapter.getHeadersCount() + position);
        }
    }

    /**
     * @param position 删除指定索引数据条目
     */
    public void removeItem(int position) {
        mDataList.remove(position);
        notifyItemRemovedWrapper(position);
    }

    /**
     * @param viewHolder 删除指定数据条目
     */
    public void removeItem(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (mHeaderAndFooterAdapter != null) {
            mDataList.remove(position - mHeaderAndFooterAdapter.getHeadersCount());
            mHeaderAndFooterAdapter.notifyItemRemoved(position);
        } else {
            removeItem(position);
        }
    }

    /**
     * @param model 删除指定数据条目
     */
    public void removeItem(M model) {
        removeItem(mDataList.indexOf(model));
    }

    public final void notifyItemInsertedWrapper(int position) {
        if (mHeaderAndFooterAdapter == null) {
            notifyItemInserted(position);
        } else {
            mHeaderAndFooterAdapter.notifyItemInserted(mHeaderAndFooterAdapter.getHeadersCount() + position);
        }
    }

    /**
     * 在指定位置添加数据条目
     *
     * @param position
     * @param model
     */
    public void addItem(int position, M model) {
        mDataList.add(position, model);
        notifyItemInsertedWrapper(position);
    }

    /**
     * @param model 在集合头部添加数据条目
     */
    public void addFirstItem(M model) {
        addItem(0, model);
    }

    /**
     * @param model 在集合末尾添加数据条目
     */
    public void addLastItem(M model) {
        addItem(mDataList.size(), model);
    }

    public final void notifyItemChangedWrapper(int position) {
        if (mHeaderAndFooterAdapter == null) {
            notifyItemChanged(position);
        } else {
            mHeaderAndFooterAdapter.notifyItemChanged(mHeaderAndFooterAdapter.getHeadersCount() + position);
        }
    }

    /**
     * 替换指定索引的数据条目
     *
     * @param location
     * @param newModel
     */
    public void setItem(int location, M newModel) {
        mDataList.set(location, newModel);
        notifyItemChangedWrapper(location);
    }

    /**
     * 替换指定数据条目
     *
     * @param oldModel
     * @param newModel
     */
    public void setItem(M oldModel, M newModel) {
        setItem(mDataList.indexOf(oldModel), newModel);
    }

    public final void notifyItemMovedWrapper(int fromPosition, int toPosition) {
        if (mHeaderAndFooterAdapter == null) {
            notifyItemMoved(fromPosition, toPosition);
        } else {
            mHeaderAndFooterAdapter.notifyItemMoved(mHeaderAndFooterAdapter.getHeadersCount() + fromPosition, mHeaderAndFooterAdapter.getHeadersCount() + toPosition);
        }
    }

    /**
     * 移动数据条目的位置
     *
     * @param fromPosition
     * @param toPosition
     */
    public void moveItem(int fromPosition, int toPosition) {
        notifyItemChangedWrapper(fromPosition);
        notifyItemChangedWrapper(toPosition);

        // 要先执行上面的 notifyItemChanged,然后再执行下面的 moveItem 操作

        mDataList.add(toPosition, mDataList.remove(fromPosition));
        notifyItemMovedWrapper(fromPosition, toPosition);
    }

    /**
     * 移动数据条目的位置。该方法在 ItemTouchHelper.Callback 的 onMove 方法中调用
     *
     * @param from
     * @param to
     */
    public void moveItem(RecyclerView.ViewHolder from, RecyclerView.ViewHolder to) {
        int fromPosition = from.getAdapterPosition();
        int toPosition = to.getAdapterPosition();
        if (mHeaderAndFooterAdapter != null) {
            mHeaderAndFooterAdapter.notifyItemChanged(fromPosition);
            mHeaderAndFooterAdapter.notifyItemChanged(toPosition);

            // 要先执行上面的 notifyItemChanged,然后再执行下面的 moveItem 操作

            mDataList.add(toPosition - mHeaderAndFooterAdapter.getHeadersCount(), mDataList.remove(fromPosition - mHeaderAndFooterAdapter.getHeadersCount()));
            mHeaderAndFooterAdapter.notifyItemMoved(fromPosition, toPosition);
        } else {
            moveItem(fromPosition, toPosition);
        }
    }

    /**
     * @return 获取第一个数据模型
     */
    public
    @Nullable
    M getFirstItem() {
        return getItemCount() > 0 ? getItem(0) : null;
    }

    /**
     * @return 获取最后一个数据模型
     */
    public
    @Nullable
    M getLastItem() {
        return getItemCount() > 0 ? getItem(getItemCount() - 1) : null;
    }


    public void addHeaderView(View headerView) {
        getHeaderAndFooterAdapter().addHeaderView(headerView);
    }

    public void addFooterView(View footerView) {
        getHeaderAndFooterAdapter().addFooterView(footerView);
    }

    public int getHeadersCount() {
        return mHeaderAndFooterAdapter == null ? 0 : mHeaderAndFooterAdapter.getHeadersCount();
    }

    public int getFootersCount() {
        return mHeaderAndFooterAdapter == null ? 0 : mHeaderAndFooterAdapter.getFootersCount();
    }

    public HeaderAndFooterAdapter getHeaderAndFooterAdapter() {
        if (mHeaderAndFooterAdapter == null) {
            synchronized (RecyclerViewAdapter.this) {
                if (mHeaderAndFooterAdapter == null) {
                    mHeaderAndFooterAdapter = new HeaderAndFooterAdapter(this);
                }
            }
        }
        return mHeaderAndFooterAdapter;
    }

    /**
     * 是否是头部或尾部
     *
     * @param viewHolder
     * @return
     */
    public boolean isHeaderOrFooter(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition() < getHeadersCount() || viewHolder.getAdapterPosition() >= getHeadersCount() + getItemCount();
    }

}