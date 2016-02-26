package com.jack.codeviewer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.jack.codeviewer.fragment.BaseListFragment;
import com.jack.codeviewer.interf.OnBackListener;

/**
 * Created by jack on 2/24/16.
 */
public class GitHomeActivity extends FragmentActivity {
    public static final String FRAGMENT_INDEX = "git_home_fragment_index";
    public static final String BUNDLE_HOME_NAME_KEY = "git_home_name_key";
    public static final String BUNDLE_HOME_PATH_KEY = "git_home_path_key";

    private OnBackListener backListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int index = getIntent().getIntExtra(FRAGMENT_INDEX, 0);
        Fragment fr;
        String tag;
        String title;

        switch (index) {
            default:
            case 0:
                title = getIntent().getStringExtra(BUNDLE_HOME_NAME_KEY);
                tag = BaseListFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new BaseListFragment();
                    fr.setArguments(getIntent().getExtras());
                }
        }

        setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !backListener.isTopLevel()) {
            backListener.onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnBackListener(OnBackListener listener) {
        backListener = listener;
    }


}
