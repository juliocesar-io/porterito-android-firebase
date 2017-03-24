package co.globinn.portero;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import co.globinn.portero.utils.Notification;
import iammert.com.library.Status;
import iammert.com.library.StatusView;
import io.ghyeok.stickyswitch.widget.StickySwitch;
import test.jinesh.loadingviews.LoadingImageView;
import test.jinesh.loadingviews.LoadingTextView;


public class MainActivity extends AppCompatActivity {

    // [START declare_database_ref]
    private DatabaseReference mDatabase;

    private Context mContext;
    // [END declare_database_ref]

    private Notification mNotification;

    public static String TAG = "MainActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = getApplicationContext();

        mNotification = new Notification(this);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .build();
        ImageLoader.getInstance().init(config);


        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference("sensor");
        // [END initialize_database_ref]
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);


        final LoadingImageView ImgCapture = (LoadingImageView)findViewById(R.id.img_capture);

        final StickySwitch stickySwitch = (StickySwitch) findViewById(R.id.sticky_switch);

        final StatusView statusView = (StatusView) findViewById(R.id.status);


        statusView.setStatus(Status.LOADING);








        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Integer a = Integer.parseInt(dataSnapshot.child("btn_push").getValue().toString());
                Integer b = Integer.parseInt(dataSnapshot.child("is_door_open").getValue().toString());



                String imageUri = dataSnapshot.child("photo_url").getValue().toString();

                final String date = dataSnapshot.child("date").getValue().toString();
                final String prox = dataSnapshot.child("proximity_cm").getValue().toString();

                ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance



                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.ic_camera_iris_grey600_48dp) // resource or drawable
                        .showImageOnFail(R.drawable.ic_camera_iris_grey600_48dp) // resource or drawable
                        .build();


                imageLoader.displayImage(imageUri, ImgCapture, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }
                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });

                //LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.asd);
                //TextView text = (TextView) findViewById(R.id.textView);


                final LoadingTextView dateText = (LoadingTextView)findViewById(R.id.date_text);
                dateText.startLoading();


                final LoadingTextView titleText = (LoadingTextView)findViewById(R.id.title_img);
                titleText.startLoading();


                final LoadingTextView sensorText = (LoadingTextView)findViewById(R.id.sensor_status);
                sensorText.startLoading();


                View status = statusView.findViewById(R.id.status_container);
                TextView stausText = (TextView) statusView.findViewById(R.id.status_text);
                ImageView statusImg = (ImageView)   statusView.findViewById(R.id.icon_status);



                ImgCapture.startLoading();




                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms

                        dateText.stopLoading();
                        titleText.stopLoading();
                        sensorText.stopLoading();
                        titleText.setText("Ultima Captura");
                        sensorText.setText("Proximidad:" + " " + prox + "cm");
                        dateText.setText(date);

                        ImgCapture.stopLoading();
                        //ImgCapture.setImageBitmap();
                    }
                }, 3000);




                switch (a){
                    case 1:

                        //relativeLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                        //text.setText("Estado 1");

                        showNotificationStateRequest("Nueva notificacion", "Estado 1");

                        //stickySwitch.setDirection(StickySwitch.Direction.RIGHT);



                        break;

                    case 0:
                        //relativeLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                        //text.setText("Estado 0");
                        showNotificationStateRequest("Nueva notificacion", "Estado 0");
                        //stickySwitch.setDirection(StickySwitch.Direction.LEFT);

                }

                switch (b){
                    case 1:


                        showNotificationStateRequest("Nueva notificacion", "Puerta abierta");
                        status.setBackgroundColor(Color.parseColor("#FF5722"));
                        stausText.setText("Puerta abierta");
                        statusImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_open_outline_white_48dp));
                        statusView.setStatus(Status.ERROR);




                        break;

                    case 0:

                        showNotificationStateRequest("Nueva notificacion", "Puerta cerrada");
                        stausText.setText("Puerta cerrada");
                        statusImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_outline_white_48dp));





                        status.setBackgroundColor(Color.parseColor("#4CAF50"));

                        statusView.setStatus(Status.ERROR);




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);
        mNotification.setSmallIcon(R.drawable.ic_glasses_grey600_48dp);

        stickySwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {
                Log.d(TAG, "Now Selected : " + direction.name() + ", Current Text : " + text);

                switch (direction.name()){
                    case "RIGHT":
                        mDatabase.addValueEventListener(postListener);

                        break;
                    case "LEFT":


                        break;

                }
            }
        });


    }


    private void showNotificationStateRequest(String ticker, String message){
        mNotification.setTicker(ticker);
        mNotification.setMessage(message);
        mNotification.setIntent(new Intent(this, MainActivity.class));
        mNotification.show(Notification.ID_NOTIFICATION_GLOBAL);
    }
}
