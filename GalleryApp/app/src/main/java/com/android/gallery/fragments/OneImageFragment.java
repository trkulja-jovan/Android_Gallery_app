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
import java.io.FileNotFoundException;
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

    private String imagePath;

    public OneImageFragment(Bitmap b, @NonNull ImageButton share, @NonNull ImageButton delete, @NonNull Toolbar t, @NonNull String path){
        this.b = b;
        this.btnShare = share;
        this.btnDelete = delete;
        this.toolbar = t;
        this.imagePath = path;
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
            if(imagePath.equals(""))
                dialog.dismiss();

            /*File f = new File(path);
            if(f.exists()){
                if(f.delete()){
                    Toast.makeText(getContext(), R.string.successDeleted, Toast.LENGTH_LONG).show();
                }
            }

            Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                                                 .popBackStack();*/
            String deleteCmd = "rm -r " + imagePath;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
                Toast.makeText(getContext(), R.string.successDeleted, Toast.LENGTH_LONG).show();
            } catch (IOException e) {

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

    private void showShareOptions(View action){

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file_" + Math.random() + ".jpg");

        try(FileOutputStream fout = new FileOutputStream(file)){

            b.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();

            file.setReadable(true, false);

            Intent intent = new Intent(ACTION_SEND);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpg");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch(IOException e){
            e.printStackTrace();
        }
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
