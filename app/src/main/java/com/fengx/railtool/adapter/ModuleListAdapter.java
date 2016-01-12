package com.fengx.railtool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fengx.railtool.R;
import com.fengx.railtool.po.Module;
import com.fengx.railtool.util.common.L;

import java.util.List;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午9:18
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午9:18
 * 修改备注：
 */
public class ModuleListAdapter
        extends RecyclerView.Adapter<ModuleListAdapter.ViewHolder> {

    private Click mClick;
    private final List<Module> mInjectors;

    public ModuleListAdapter(List<Module> items) {
        mInjectors = items;
        L.e("IndexAdapter:" + mInjectors.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_module, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mInjectors.get(position);
        holder.mContentView.setText(mInjectors.get(position).getModuleName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mClick != null) {
                    mClick.itemClick(position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mInjectors.size();
    }

    public void setClick(Click mClick) {
        this.mClick = mClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Module mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface Click {
        void itemClick(int p);
    }
}
