package com.axeac.android.sdk.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.axeac.android.sdk.R;

import java.util.List;

/**
 * Created by hp on 2018/3/9.
 */

public class PhotoSelectorAdapter extends BaseAdapter{
    private List<String> path;
    private int mMaxPosition;
    private Context context;
    private GridView gridView;
    private boolean type = false;
    public PhotoSelectorAdapter(Context mContext, List<String> path, GridView gridView,Boolean type){
        this.context=mContext;
        this.path=path;
        this.gridView = gridView;
        this.type = type;
    }
    @Override
    public int getCount() {
        if (!type)
            mMaxPosition = path.size()+1;
        else
            mMaxPosition = path.size();
        return mMaxPosition;
    }

    @Override
    public Object getItem(int i) {
        return path.get(i);
    }

    public int getMaxPosition(){
        return mMaxPosition;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.photo_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.photo_item_image);
            holder.delimageView = view.findViewById(R.id.photo_item_delete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (!type) {
            if (i == mMaxPosition - 1) {
                holder.imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.axeac_btn_add));
                holder.delimageView.setVisibility(View.GONE);
                if (i == 6 && mMaxPosition == 7) {
                    holder.imageView.setVisibility(View.GONE);
                }
            } else {
                holder.imageView.setImageBitmap(getBitmapByPath(path.get(i)));
            }
        }else{
            holder.imageView.setImageBitmap(getBitmapByPath(path.get(i)));
            holder.delimageView.setVisibility(View.GONE);
        }
        holder.delimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (path.size()==3) {
                    ViewGroup.LayoutParams params = gridView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    gridView.setLayoutParams(params);
                }
                path.remove(i);
                notifyDataSetChanged();

            }
        });
        return view;
    }
    public final class ViewHolder {
        public ImageView imageView,delimageView;
    }
    private static Bitmap getBitmapByPath(String imageFile) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 500 * 400);
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageFile, opts);
    }
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
