package com.commonrail.mtf.mvp.ui.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/23 下午9:27
 * 修改人：wengyiming
 * 修改时间：16/3/23 下午9:27
 * 修改备注：
 */
public  abstract  class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> items;


    protected boolean hasHeader = false;

    protected final static int HEADER = 0;
    protected final static int ITEM = 1;
    
    protected int itemLayoutRes = -1;
    protected int headerLayoutRes = -1;

    public void setItems(final List<T> mItems) {
        items = mItems;
    }

    public void setHasHeader(final boolean mHasHeader) {
        hasHeader = mHasHeader;
    }


    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getItemViewType(final int position) {
        if (hasHeader && position == 0) {
            return HEADER;
        }
        return ITEM;
    }


    public interface BaseAdapterInterface {
        void createView();

        void bindView();
        
        boolean hasHeader();
        
        
        
    }


}
