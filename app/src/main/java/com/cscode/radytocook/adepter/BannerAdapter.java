package com.cscode.radytocook.adepter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.cscode.radytocook.R;
import com.cscode.radytocook.model.Banner;
import com.cscode.radytocook.utils.ProportionalImageView;

import java.util.List;

import static com.cscode.radytocook.retrofit.APIClient.Base_URL;

public class BannerAdapter extends PagerAdapter {

    private Context context;
    private List<Banner> bannerList;

    public BannerAdapter(Context activity, List<Banner> imagesArray) {

        this.context = activity;
        this.bannerList = imagesArray;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        Log.e("imge", " " + bannerList.get(position).getBannerImg());
        View viewItem = inflater.inflate(R.layout.image_item, container, false);
        ProportionalImageView imageView = viewItem.findViewById(R.id.imageView);
        Glide.with(context).load(Base_URL + "/" + bannerList.get(position).getBannerImg()).placeholder(R.drawable.slider).into(imageView);
        ((ViewPager) container).addView(viewItem);

        return viewItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        ((ViewPager) container).removeView((View) object);
    }
}