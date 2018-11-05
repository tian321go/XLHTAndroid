package com.axeac.android.client.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.axeac.android.client.R;
/**
 * 网络地址列表适配器
 * @author axeac
 * @version 2.3.0.0001
 * */
public class NetworkListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    /**
     * 存储网络名称的list集合
     * */
    private List<String> netDescList;
    /**
     * 存储网络地址的list集合
     * */
    private List<String> netUrlList;
    private ListView mNetworkList;
    private Activity mContext;
    private float UpX, DownX = 0;

    public NetworkListAdapter(ListView mNetworkList, Activity mContext, List<String> netDescList, List<String> netUrlList) {
        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.netDescList = netDescList;
        this.mNetworkList = mNetworkList;
        this.netUrlList = netUrlList;
    }

    @Override
    public int getCount() {
        return netUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return netUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.network_list_item, null);
            holder = new ViewHolder();
            holder.netDesc = (TextView) convertView.findViewById(R.id.network_list_item_desc);
            holder.netUrl = (TextView) convertView.findViewById(R.id.network_list_item_url);
            holder.netDel = (Button) convertView.findViewById(R.id.network_list_item_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.netDesc.setText(netDescList.get(position));
        holder.netUrl.setText(netUrlList.get(position));
        holder.netDel.setTag(holder);
//        holder.netDel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ViewHolder holder = (ViewHolder) view.getTag();
//                if (holder.netUrl.getText().toString().equals(StaticObject.read.getString(StaticObject.SERVERURL, ""))) {
//                    StaticObject.wirte.edit().putString(StaticObject.SERVERDESC, "").commit();
//                    StaticObject.wirte.edit().putString(StaticObject.SERVERURL, "").commit();
//                }
//
//                List<NetInfo> urllist = OrmHelper.getLiteOrm(mContext).query(new QueryBuilder<>(NetInfo.class)
//                        .where("serverurl = ? ", new Object[]{holder.netUrl.getText().toString()}));
//                if (urllist != null && urllist.size() > 0) {
//                    for(NetInfo info:urllist){
//                        OrmHelper.getLiteOrm(mContext).delete(info);
//                    }
//                }
//
//                netDescList.remove(holder.netDesc.getText().toString());
//                netUrlList.remove(holder.netUrl.getText().toString());
//                mNetworkList.setAdapter(new NetworkListAdapter(mNetworkList, mContext, netDescList, netUrlList));
//                /*mContext.startActivity(new Intent(mContext, NetworkListActivity.class));
//                mContext.finish();*/
//            }
//        });
//        convertView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ViewHolder holder = (ViewHolder) v.getTag();
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        break;
//                    case MotionEvent.ACTION_DOWN:
//                        DownX = event.getX();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        UpX = event.getX();
//                        if (UpX > DownX && Math.abs(UpX - DownX) > 20) {
//                            holder.netDel.setVisibility(View.GONE);
//                        } else if (UpX < DownX && Math.abs(UpX - DownX) > 20) {
//                            holder.netDel.setVisibility(View.VISIBLE);
//                        } else {
//                            int netId = -1;
//                            List<NetInfo> list = OrmHelper.getLiteOrm(mContext).query(new QueryBuilder<>(NetInfo.class)
//                                    .where("serverurl = ? ", new Object[]{holder.netUrl.getText().toString()}));
//                            if (list.size() > 0) {
//                                for (NetInfo info : list) {
//                                    netId = info.getId();
//                                }
//                            }
//
//                            Intent modifyIntent = new Intent(mContext, NetworkSetActivity.class);
//                            modifyIntent.putExtra("net_id", netId);
//                            mContext.startActivity(modifyIntent);
//                            mContext.finish();
//                        }
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
        return convertView;
    }

    public final class ViewHolder {
        public TextView netDesc;
        public TextView netUrl;
        public Button netDel;
    }
}