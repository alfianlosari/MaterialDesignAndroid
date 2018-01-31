package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.utils.DateUtils;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */

public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";
    public static final String ARG_ITEM_ID = "item_id";

    public Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private View mRootView;
    private TextView mAuthorTextView;
    private TextView mPublishedDateTextView;

    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    public String body;
    public String title;

    private ParagraphListAdapter mAdapter;
    private String itemId;

    public ArticleDetailFragment() {}

    public static ArticleDetailFragment newInstance(String itemId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public ArticleDetailActivity getDetailActivity() {
        return ((ArticleDetailActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mToolbar = mRootView.findViewById(R.id.app_bar);
        mImageView = mRootView.findViewById(R.id.image);
        mAuthorTextView = mRootView.findViewById(R.id.author);
        mPublishedDateTextView = mRootView.findViewById(R.id.published_date);
        mCollapsingToolbar = mRootView.findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle(" ");
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getDetailActivity()));
        mAdapter = new ParagraphListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            itemId = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getLoaderManager().getLoader(0) != null) {
            getLoaderManager().restartLoader(0, null, this);
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newInstanceForItemServerId(getActivity(), itemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToNext()) {
            Picasso.with(getDetailActivity()).load(data.getString(ArticleLoader.Query.PHOTO_URL)).into(mImageView);
            title = data.getString(ArticleLoader.Query.TITLE);
            mCollapsingToolbar.setTitle(title);
            mAuthorTextView.setText("by " + data.getString(ArticleLoader.Query.AUTHOR));
            mPublishedDateTextView.setText(DateUtils.formatPublishedDate(data.getString(ArticleLoader.Query.PUBLISHED_DATE)));
            body = Html.fromHtml(data.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")).toString();
            String[] paragraphs = body.split("(\r\n|\n)");
            mAdapter.setParagraphs(paragraphs);
        }
        data.close();
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i("Fragment", "Restored");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    class ParagraphListAdapter extends  RecyclerView.Adapter<ParagraphListAdapter.ViewHolder> {

        String[] mParagraphs;

        private void setParagraphs(String[] paragraphs) {
            mParagraphs = paragraphs;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_body, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ((TextView) holder.itemView).setText(mParagraphs[position]);
        }

        @Override
        public int getItemCount() {
            if (mParagraphs == null) return 0;
            return mParagraphs.length;
        }

        class ViewHolder extends  RecyclerView.ViewHolder {
            public ViewHolder(TextView itemView) {
                super(itemView);
            }
        }

    }

}
