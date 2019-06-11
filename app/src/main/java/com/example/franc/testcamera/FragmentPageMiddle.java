package com.example.franc.testcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPageMiddle extends Fragment {
    ImageView imageView0;
    ImageView imageView1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Getting stored infomation from bundle (shared by all fragments)
        Bundle bundle = getArguments();
        String message = Integer.toString(bundle.getInt("count"));

        View view = inflater.inflate(R.layout.fragment_page_middle, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ImageView
        imageView0 = (ImageView) getActivity().findViewById(R.id.myIV0);
        imageView0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Activity1.class);
                startActivity(intent);
            }
        });

        imageView1 = (ImageView) getActivity().findViewById(R.id.myIV1);

    }

    @Override
    public void onPause() {
        System.out.println("fragmentpage0 PAUSED");
        super.onPause();
    }

    @Override
    public void onResume() {
        System.out.println("fragmentpage0 RESUMED");
        super.onResume();
        //If picture was taken
        if (((ActivityMain)getActivity()).wasPictureTaken()) {
            System.out.println("checked picture taken or not");
            //Get the image file
            File currentImageFile = ((ActivityMain)getActivity()).getPicture();
            //Show picture as an image view in xml design
            String currentPhotoPath = currentImageFile.getAbsolutePath();
            File imgFile = new  File(currentPhotoPath);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView0.setImageBitmap(myBitmap);
            imageView1.setImageBitmap(myBitmap);
        }
        else {
            System.out.println("MY PICTURE WAS NOT TAKEN");
        }
    }
}
