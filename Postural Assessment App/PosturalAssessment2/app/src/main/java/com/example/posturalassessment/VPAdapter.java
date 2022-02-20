package com.example.posturalassessment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VPAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();
    private fragment1 fragment1 = new fragment1();
    private fragment2 fragment2 = new fragment2();
    private fragment3 fragment3 = new fragment3();

    public VPAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFragment(Fragment fragment, String title){

        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
    }

    public void addFragment1( fragment1 fragment1, String title){
        this.fragment1 = fragment1;
        fragmentArrayList.add(fragment1);
        fragmentTitle.add(title);
    }

    public void addFragment2( fragment2 fragment2, String title){
        this.fragment2 = fragment2;
        fragmentArrayList.add(fragment2);
        fragmentTitle.add(title);
    }

    public void addFragment3( fragment3 fragment3, String title){
        this.fragment3 = fragment3;
        fragmentArrayList.add(fragment3);
        fragmentTitle.add(title);
    }

    public com.example.posturalassessment.fragment1 getFragment1() {
        return fragment1;
    }

    public com.example.posturalassessment.fragment2 getFragment2() {
        return fragment2;
    }

    public com.example.posturalassessment.fragment3 getFragment3() {
        return fragment3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


        return fragmentTitle.get(position);
    }


}
