package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private Cursor mCursor;
    public String mSelectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);


        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getStringExtra(SELECTED_ITEM_ID) != null) {
                mSelectedItemId = getIntent().getStringExtra(SELECTED_ITEM_ID);
            }
        } else {
            mSelectedItemId = savedInstanceState.getString(SELECTED_ITEM_ID);
        }

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mCursor.moveToPosition(position);
                mSelectedItemId = mCursor.getString(ArticleLoader.Query.SERVER_ID);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        if (getLoaderManager().getLoader(0) != null) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    public void onBackButtonClicked(View view) {
        onBackPressed();
    }

    public void onShareButtonClicked(View view) {
        Fragment fragment = mPagerAdapter.getCurrentFragment();
        if (fragment != null) {
            ArticleDetailFragment articleDetailFragment = (ArticleDetailFragment) fragment;
            String body = articleDetailFragment.body;
            if (!TextUtils.isEmpty(body)) {
                if (body.length() > 1000) {
                    body = body.substring(0, 2000);
                }

                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText(body)
                        .getIntent(), getString(R.string.action_share)));
            } else {
                Snackbar.make(view, getString(R.string.no_article_to_share), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        int pos = -1;
        if (mCursor != null) {
            mCursor.moveToPosition(-1);
            while (mCursor.moveToNext()) {
                pos++;
                String itemId = mCursor.getString(ArticleLoader.Query.SERVER_ID);
                if (mSelectedItemId.equals(itemId)) {
                    mPagerAdapter.notifyDataSetChanged();
                    mPager.setCurrentItem(pos, false);
                    return;
                }
            }
        }
        mPagerAdapter.notifyDataSetChanged();
        mCursor.moveToFirst();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getString(ArticleLoader.Query.SERVER_ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_ITEM_ID, mSelectedItemId);
    }

}
