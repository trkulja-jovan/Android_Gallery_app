package com.android.gallery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gallery.R;
import com.android.gallery.fragments.ImagesFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {

    private List<String> images;
    private static ImagesFragment.OnOptionClickListener listener;

    public ImageAdapter(List<String> images, ImagesFragment.OnOptionClickListener listener){
        this.images = images;
        ImageAdapter.listener = listener;
    }

    static class Holder extends RecyclerView.ViewHolder{

        private ImageView img;
        private Integer path;

        Holder(View view) {
            super(view);
            this.img = view.findViewById(R.id.sqrImg);

            img.setOnClickListener(action -> listener.onOptionSelected(action, path));

        }

        private void setPathAndPosition(Integer path){
            this.path = path;
        }

    }

    @NonNull
    @Override
    public ImageAdapter.Holder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.image_adapter, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder h, int pos){
        String path = images.get(pos);
        h.setPathAndPosition(pos);

        Picasso.get()
               .load(new File(path))
               .fit()
               .into(h.img);

    }

    @Override
    public int getItemCount(){
        return images != null ? images.size() : 0;
    }

}
