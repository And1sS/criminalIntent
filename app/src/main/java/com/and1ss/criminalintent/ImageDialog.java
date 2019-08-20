package com.and1ss.criminalintent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static android.content.ContentValues.TAG;

public class ImageDialog extends DialogFragment {

    private static final String ARG_PATH = "PATH";

    private String mPath;

    private Bitmap mImage;

    private ImageView mImageView;

    private ImageDialog() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPath = getArguments().getString(ARG_PATH);

        mImage = BitmapFactory.decodeFile(mPath);
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.image_dialog, null);

        mImageView = (ImageView) view.findViewById(R.id.image);
        mImageView.setImageBitmap(mImage);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            Matrix matrix = new Matrix();
            Matrix savedMatrix = new Matrix();

            static final int NONE = 0;
            static final int DRAG = 1;
            static final int ZOOM = 2;
            int mode = NONE;

            PointF start = new PointF();
            PointF mid = new PointF();
            float oldDist = 1f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView view = (ImageView) v;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - start.x, event.getY()
                                    - start.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                        break;
                }

                view.setImageMatrix(matrix);
                return true;
            }

            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float) Math.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                }).setTitle(R.string.crime_image_dialog_title)
                .create();
    }

    public static ImageDialog newInstance(String path) {
        ImageDialog dialog = new ImageDialog();

        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);

        dialog.setArguments(args);

        return dialog;
    }
}
