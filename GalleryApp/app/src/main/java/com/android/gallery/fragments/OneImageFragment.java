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
import com.android.gallery.utils.ImagesGuard;
import com.android.gallery.utils.Init;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.content.Intent.ACTION_SEND;

public class OneImageFragment extends Fragment {

    private Bitmap b;

    private View v;

    private ImageButton btnShare;
    private ImageButton btnDelete;

    private Toolbar toolbar;

    private Integer imagePos;

    public OneImageFragment(@NonNull Bitmap b,
                            @NonNull ImageButton share,
                            @NonNull ImageButton delete,
                            @NonNull Toolbar t,
                            @NonNull Integer path){
        this.b = b;
        this.btnShare = share;
        this.btnDelete = delete;
        this.toolbar = t;
        this.imagePos = path;
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

    private String getImagePos(){
        return ImagesGuard.getBitmapsPath().get(imagePos);
    }

    private void deletePicture(View action){

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

        builder.setTitle(R.string.potvrda);
        builder.setMessage(R.string.delete);

        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            if(getImagePos().equals(""))
                dialog.dismiss();

            String deleteCmd = "rm -r " + getImagePos();
            Runtime runtime = Runtime.getRuntime();
            try {
                ImagesGuard.getBitmapsPath().remove(imagePos);

                Objects.requireNonNull(Init.getRecyclerView().getAdapter()).notifyDataSetChanged();
                Init.getRecyclerView().invalidate();

                runtime.exec(deleteCmd);
                Toast.makeText(getContext(), R.string.successDeleted, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
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

        List<String> newPaths = Init.getInstance().getImagesFromUri(getContext(), true);
        ImagesGuard.setBitmapsPath(newPaths);
    }
}
