package com.android.gallery.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.gallery.R;
import com.android.gallery.exceptions.InitializeException;
import com.android.gallery.utils.DescriptionGuard;
import com.android.gallery.utils.Init;
import com.jsibbold.zoomage.ZoomageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Intent.ACTION_SEND;

public class OneImageFragment extends Fragment {

    private Bitmap b;

    private ImageButton openDesc;
    private ImageButton openShare;

    private Toolbar toolbarDock;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_one_image, container, false);

        Init.getInstance().initComponents( () -> {
            ZoomageView z = v.findViewById(R.id.zoom);
            z.setImageBitmap(b);
            setRetainInstance(true);

            toolbarDock = v.findViewById(R.id.toolbar4);

            getActivity().getActionBar().show();

            openDesc = v.findViewById(R.id.btnDesc2);
            openShare = v.findViewById(R.id.btnShare2);
        });

        openDesc.setOnClickListener(action -> {

            Init.getInstance()
                 .getAlertDescriptionInstance(getActivity())
                 .initAndShowDialog(this.b);

            String txt = DescriptionGuard.getDescription();
            System.out.println(txt);
        });

        openShare.setOnClickListener(action -> {
            showShareOptions();
        });

        return v;
    }

    public OneImageFragment(Bitmap b){
        this.b = b;
    }

    private void showShareOptions(){

        Intent share = new Intent(ACTION_SEND);
        share.setType("image/jpeg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
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
}
