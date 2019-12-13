package com.chalkboystudios.franc.unmix.CustomWidgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.chalkboystudios.franc.unmix.R;


/**
 * Custom instruction page when user clicks on information button in fragment page gallery
 */
public class CustomInformationDialog extends Dialog {
    private Context context;
    private ImageView informationIV;
    private int currentDialogPage = 1;

    public CustomInformationDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomInformationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout
        this.setContentView(R.layout.dialog_information);

        // Set dialog background to transparent
        this.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        // Set appear and disappear transitions
        this.getWindow().getAttributes().windowAnimations = R.style.DialogFade;

        // Set cancel on back button pressed
        this.setOnCancelListener(new DialogCancelListener());

        // Set initial background
        informationIV = findViewById(R.id.informationIV);
        informationIV.setBackground(context.getResources().getDrawable(R.drawable.instruction1));

        // User click to go next page
        informationIV.setOnClickListener(new InformationClickListener());
    }

    private class DialogCancelListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
        }
    }

    private class InformationClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (currentDialogPage == 1) {
                informationIV.setBackground(context.getResources().getDrawable(R.drawable.instruction2));
                currentDialogPage = 2;
            }
            else if (currentDialogPage == 2) {
                informationIV.setBackground(context.getResources().getDrawable(R.drawable.instruction3));
                currentDialogPage = 3;
            }
            else {
                CustomInformationDialog.this.dismiss();
            }
        }
    }
}
