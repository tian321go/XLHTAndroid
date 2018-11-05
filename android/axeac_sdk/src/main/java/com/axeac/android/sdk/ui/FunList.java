package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.ComponentActivity;
import com.axeac.android.sdk.adapters.GridMainGridAdapter;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.StaticObject;

import java.util.ArrayList;
import java.util.List;

import static com.axeac.android.sdk.activity.BaseActivity.act;

/**
 * Created by hp on 2018/11/4.
 */

public class FunList extends Component{

    private RelativeLayout linearlayout;

    private RelativeLayout titleLayout;

    private TextView titleView;

    private GridView layoutContent;

    private String title = "系统功能";

    private boolean showTitle = true;

    private List<String []> dataList = new ArrayList<>();

    private Context ctx;

    public FunList(Activity ctx){
        super(ctx);
        this.ctx = ctx;
    }

    private void init(){
        linearlayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_gridlabel,null);
        titleLayout = linearlayout.findViewById(R.id.gridlabel_layout);
        titleView = linearlayout.findViewById(R.id.gridlabel_title);
        layoutContent = linearlayout.findViewById(R.id.grid_content);
        showView();
    }

    private void showView(){
        if (!showTitle){
            titleLayout.setVisibility(View.GONE);
        }
        titleView.setText(title);
        layoutContent.setAdapter(new GridMainGridAdapter(ctx,dataList));
        layoutContent.setOnItemClickListener(mGridMainListener());
    }

    private AdapterView.OnItemClickListener mGridMainListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long row) {
                doLoading("MEIP_PAGE=" + dataList.get(index)[2] + "\r\n");
            }
        };
    }

    private void doLoading(final String meip) {

        UIHelper.send(act, meip, new OnRequestCallBack() {
            @Override
            public void onStart() {
                act.showProgressDialog();
            }

            @Override
            public void onCompleted() {
                act.removeProgressDialog();
            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getReturnType().equals(StaticObject.MEIP_RETURN_FORM)) {
                    StaticObject.ismenuclick = false;
                    Intent intent = new Intent(act, ComponentActivity.class);
                    intent.putExtra("parms", response.getData());
                    act.startActivity(intent);
                }
            }

            @Override
            public void onfailed(Throwable e) {
                act.removeProgressDialog();
            }
        });
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setShowTitle(String showTitle){
        this.showTitle = Boolean.parseBoolean(showTitle);
    }

    public void addData(String data){
        if(data.indexOf("||")!=-1){
            String [] datas = StringUtil.split(data,"||");
            if (datas.length>=3){
                dataList.add(datas);
            }
        }
    }

    @Override
    public void execute() {
        init();
    }

    @Override
    public View getView() {
        return linearlayout;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void repaint() {

    }

    @Override
    public void starting() {

    }

    @Override
    public void end() {

    }
}
