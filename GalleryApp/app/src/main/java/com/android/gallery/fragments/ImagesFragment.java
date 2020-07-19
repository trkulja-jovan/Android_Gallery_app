package com.android.gallery.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.gallery.R;
import com.android.gallery.adapters.ImageAdapter;
import com.android.gallery.utils.ImagesGuard;
import com.android.gallery.utils.Init;

public class ImagesFragment extends Fragment {

    @FunctionalInterface
    public interface OnOptionClickListener {
        void onOptionSelected(View view, Integer path);
    }

    private OnOptionClickListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_images, container, false);

        Init.getInstance().initComponents( () -> {

            RecyclerView rw = v.findViewById(R.id.recyclerView);
            Init.setRecyclerView(rw);

            GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
            manager.setOrientation(GridLayoutManager.VERTICAL);

            rw.setLayoutManager(manager);
            rw.setAdapter(new ImageAdapter(ImagesGuard.getBitmapsPath(), mCallback));
        });

        return v;
    }

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);

        try {
            mCallback = (OnOptionClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnOptionClickListener!");
        }
    }

}
