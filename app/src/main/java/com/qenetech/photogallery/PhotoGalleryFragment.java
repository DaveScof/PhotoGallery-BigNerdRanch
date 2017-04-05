package com.qenetech.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.qenetech.photogallery.backend.FlickrFetchr;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
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

    private void setupAdapter(){
        if (isAdded())
        {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
            mPhotoRecyclerView.getLayoutManager().scrollToPosition(lastPosition);
        }
    }
    private class PhotoHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        public void bindPhoto (GalleryItem item){
            mTextView.setText(item.getCaption());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter (List<GalleryItem> galleryItems){
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PhotoHolder(new TextView(getActivity()));
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
            holder.bindPhoto(item);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
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
