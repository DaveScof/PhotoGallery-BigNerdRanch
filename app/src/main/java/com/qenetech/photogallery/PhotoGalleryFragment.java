package com.qenetech.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.qenetech.photogallery.backend.FlickrFetchr;
import com.qenetech.photogallery.backend.ThumbnailDownloader;
import com.qenetech.photogallery.model.GalleryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davescof on 4/4/17.
 */

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }
    private List<GalleryItem> mItems = new ArrayList<>();

    private RecyclerView mPhotoRecyclerView;
    private int mGalleryPage;
    private int lastPosition;
    private FlickrFetchr mFlickrFetchr = new FlickrFetchr();
    private boolean mIsLoading = false;

    private PhotoAdapter mAdapter;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindPhoto(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.frag_photo_gallery_recyclerView);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        ViewTreeObserver observer = mPhotoRecyclerView.getViewTreeObserver();
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && !mIsLoading) {
                    GridLayoutManager manager = (GridLayoutManager) mPhotoRecyclerView.getLayoutManager();
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    int pastVisibleItems = manager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        lastPosition = manager.findLastVisibleItemPosition() - visibleItemCount + 2;
                        mGalleryPage++;
                        new FetchItemsTask().execute();
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread stoped");
    }

    private void setupAdapter(){
        if (isAdded())
        {
            if (mAdapter == null)
            {
                mAdapter = new PhotoAdapter(mItems);
                mPhotoRecyclerView.setAdapter(mAdapter);
            }
            else {
                mAdapter.setGalleryItems(mItems);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindPhoto (Drawable drawable) {
            mImageView.setImageDrawable(drawable);
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter (List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
            Drawable placeHolder = getResources().getDrawable(R.drawable.background_material_red);
            holder.bindPhoto(placeHolder);
            mThumbnailDownloader.queueThumbnail(holder, item.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        public void setGalleryItems(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>{

        @Override
        protected void onPreExecute() {
            mIsLoading =  true;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            return mFlickrFetchr.fetchItems(mGalleryPage);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
            mIsLoading =  false;
        }
    }
}
