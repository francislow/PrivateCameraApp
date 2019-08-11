package com.example.franc.unmix;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.Window;

import java.util.List;

/**
 * Created by franc on 11/8/2019.
 */

public class CustomInformationDialog extends Dialog {

    public CustomInformationDialog(@NonNull Context context) {
        super(context);
    }

    public CustomInformationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_information);

        // Set dialog background to transparent
        this.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        // Set appear and disappear transitions
        this.getWindow().getAttributes().windowAnimations = R.style.DialogFade;

        this.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }
}
