package com.storageauctions.dev;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_frame, container, false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                view.findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment fragment = null;

                        switch (item.getItemId()) {
                            case R.id.home_tab:
                                fragment = new HomeFragment();
                                break;
                            case R.id.auction_tab:
                                fragment = new AuctionListFragment();
                                break;
                            case R.id.search_tab:
                                break;
                        }

                        if (fragment != null) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment);
                            ft.commit();
                        }
                        return true;
                    }
                });

        removeTextLabel(bottomNavigationView, R.id.home_tab);
        removeTextLabel(bottomNavigationView, R.id.auction_tab);
        removeTextLabel(bottomNavigationView, R.id.search_tab);

        Fragment fragment = null;
        if (MainActivity.bWelcome == true) {
            fragment = new WelcomeFragment();
        } else {
            fragment = new HomeFragment();
        }

        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }


        return view;
    }

    private void removeTextLabel(@NonNull BottomNavigationView bottomNavigationView, @IdRes int menuItemId) {
        View view = bottomNavigationView.findViewById(menuItemId);
        int height = bottomNavigationView.getHeight();

        if (view == null) return;
        if (view instanceof MenuView.ItemView) {
            ViewGroup viewGroup = (ViewGroup) view;
            int padding = 0;
            float paddingTop = viewGroup.getPaddingTop();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    padding = v.getHeight();
                    viewGroup.removeViewAt(i);
                }
            }
            viewGroup.setPadding(view.getPaddingLeft(), 28, view.getPaddingRight(), view.getPaddingBottom());
        }
    }
}
