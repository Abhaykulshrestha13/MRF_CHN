package com.cscode.radytocook.adepter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cscode.radytocook.SliderItemFragment;

public class SliderPagerAdapter extends FragmentPagerAdapter {
  public SliderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
    super(fm, behavior);
  }

  @NonNull @Override public Fragment getItem(int position) {
    return SliderItemFragment.newInstance(position);
  }

  @Override public int getCount() {
    return 2;
  }
}