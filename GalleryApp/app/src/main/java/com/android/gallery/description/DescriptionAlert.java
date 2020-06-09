package com.android.gallery.description;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.gallery.R;
import com.android.gallery.utils.DescriptionGuard;

public class DescriptionAlert {

    private Activity activity;

    public DescriptionAlert(Activity activity){
        this.activity = activity;
    }

    private Activity getActivity(){
        return this.activity;
    }

    public void initAndShowDialog(Bitmap bitmap){

        AlertDialog builder = new AlertDialog.Builder(getActivity()).create();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.des_dialog, null);

        EditText editText = dialogView.findViewById(R.id.edt_comment);
        Button btnOk = dialogView.findViewById(R.id.buttonSubmit);
        Button btnCancel = dialogView.findViewById(R.id.buttonCancel);

        btnOk.setOnClickListener(action -> {
            DescriptionGuard.setDescription(editText.getText().toString());
            System.out.println(DescriptionGuard.getDescription());
            builder.hide();
        });

        btnCancel.setOnClickListener(action -> builder.dismiss());

        builder.setView(dialogView);
        builder.show();
    }

}
