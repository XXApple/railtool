package com.commonrail.mtf.mvp.ui.activity.bluetooth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonrail.mtf.R;

/**
 * �����Ĳ˵��� - ������ʾ�����е��б�ġ�
 */
public class SampleListFragment extends ListFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), "You click: " + position, Toast.LENGTH_SHORT).show();
        super.onListItemClick(l, v, position, id);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SampleAdapter adapter = new SampleAdapter(getActivity());

        // for (int i = 0; i < 20; i++) {
        // adapter.add(new SampleItem("Sample List",
        // android.R.drawable.ic_menu_search));
        // }

        adapter.add(new SampleItem("�½���¼", android.R.drawable.ic_menu_add));
        adapter.add(new SampleItem("�鿴��¼", android.R.drawable.ic_menu_search));
        adapter.add(new SampleItem("�ϴ���¼", android.R.drawable.ic_menu_upload));

        this.setListAdapter(adapter);


    }

    public class SampleAdapter extends ArrayAdapter<SampleItem> {
        public SampleAdapter(Context context) {
            super(context, 0);
        }

        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_list_bluet, null);
            }
            ImageView icon = (ImageView) convertView
                    .findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView
                    .findViewById(R.id.row_title);
            title.setText(getItem(position).tag);

            return convertView;
        }
    }

    private class SampleItem {
        public String tag;
        public int iconRes;

        public SampleItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }
}
