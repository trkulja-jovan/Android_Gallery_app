package com.android.gallery.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.gallery.R;
import com.android.gallery.exceptions.InitializeException;
import com.android.gallery.utils.ImagesGuard;
import com.android.gallery.utils.Init;
import com.jsibbold.zoomage.ZoomageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.content.Intent.ACTION_SEND;

public class OneImageFragment extends Fragment {

    private Bitmap b;

    private View v;

    private ImageButton btnShare;
    private ImageButton btnDelete;

    private Toolbar toolbar;

    private int imagePosition;

    public OneImageFragment(Bitmap b, @NonNull ImageButton share, @NonNull ImageButton delete, @NonNull Toolbar t){
        this.b = b;
        this.btnShare = share;
        this.btnDelete = delete;
        this.toolbar = t;
        this.imagePosition = Init.getInstance().getImagePosition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_one_image, container, false);

        Init.getInstance().initComponents(this::initializeComponents);

        btnShare.setOnClickListener(this::showShareOptions);
        btnDelete.setOnClickListener(this::deletePicture);

        return v;
    }

    private void initializeComponents(){
        ZoomageView z = v.findViewById(R.id.zoom);
        z.setImageBitmap(b);
        setRetainInstance(true);
    }

    private void deletePicture(View action){

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

        builder.setTitle(R.string.potvrda);
        builder.setMessage(R.string.delete);

        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            String path = getImagePath();
            if(path.equals(""))
                dialog.dismiss();

            File f = new File(path);
            if(f.exists()){
                if(f.delete()){
                    Toast.makeText(getContext(), R.string.successDeleted, Toast.LENGTH_LONG).show();
                }
            }

            Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                                                 .popBackStack();
        });

        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private String getImagePath(){
        try {
            return ImagesGuard.getBitmapsPath().get(imagePosition);
        } catch(IndexOutOfBoundsException e){
            return "";
        }
    }

    private void showShareOptions(View action){

        Intent share = new Intent(ACTION_SEND);
        share.setType("image/jpeg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file_" + Math.random() + ".jpg");
        try {

            boolean res = f.createNewFile();
            if(res){
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            } else {
                throw new InitializeException("Error creating file");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory().getPath()));
        startActivity(Intent.createChooser(share, "Share"));

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        assert btnDelete != null && btnShare != null && toolbar != null;
        btnShare.setVisibility(View.INVISIBLE);
        btnDelete.setVisibility(View.INVISIBLE);
        toolbar.setVisibility(View.INVISIBLE);
    }
}
