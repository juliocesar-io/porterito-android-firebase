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

    StatusView statusView;

    LoadingTextView dateText;
    LoadingTextView titleText;
    LoadingTextView sensorText;

    private Notification mNotification;
    private String imgUrl;
    private String proxString;
    private String dateString;
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


        final LoadingImageView ImgCaptureView = (LoadingImageView)findViewById(R.id.img_capture);
        final StickySwitch stickySwitch = (StickySwitch) findViewById(R.id.sticky_switch);
        statusView = (StatusView) findViewById(R.id.status);
        dateText = (LoadingTextView)findViewById(R.id.date_text);
        titleText = (LoadingTextView)findViewById(R.id.title_img);
        sensorText = (LoadingTextView)findViewById(R.id.sensor_status);
        final View status = statusView.findViewById(R.id.status_container);
        final TextView stausText = (TextView) statusView.findViewById(R.id.status_text);
        final ImageView statusImg = (ImageView)   statusView.findViewById(R.id.icon_status);
        statusView.setStatus(Status.LOADING);


        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Integer is_timbre_push = Integer.parseInt(dataSnapshot.child("btn_push").getValue().toString());
                Integer is_door_open = Integer.parseInt(dataSnapshot.child("is_door_open").getValue().toString());


                final String date = dataSnapshot.child("date").getValue().toString();
                final String prox = dataSnapshot.child("proximity_cm").getValue().toString();
                final String photoURl = dataSnapshot.child("photo_url").getValue().toString();


                setImgUrl(photoURl, date, prox);

                loadImageFormUrl(imgUrl,ImgCaptureView, date, prox);

                switch (is_timbre_push){
                    case 1:
                        showNotificationStateRequest("Nueva notificacion", "Estado 1");
                        break;

                    case 0:
                        showNotificationStateRequest("Nueva notificacion", "Estado 0");
                        break;

                }

                switch (is_door_open){
                    case 1:
                        showNotificationStateRequest("Nueva notificacion", "Puerta abierta");
                        status.setBackgroundColor(Color.parseColor("#FF5722"));
                        stausText.setText("Puerta abierta");
                        statusImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_open_outline_white_48dp));
                        stickySwitch.setDirection(StickySwitch.Direction.RIGHT);
                        statusView.setStatus(Status.ERROR);
                        break;

                    case 0:

                        showNotificationStateRequest("Nueva notificacion", "Puerta cerrada");
                        status.setBackgroundColor(Color.parseColor("#4CAF50"));
                        stausText.setText("Puerta cerrada");
                        statusImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_outline_white_48dp));
                        stickySwitch.setDirection(StickySwitch.Direction.LEFT);
                        statusView.setStatus(Status.ERROR);
                        break;
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
                        loadImageFormUrl(imgUrl,ImgCaptureView, dateString, proxString);
                        break;
                    case "LEFT":
                        loadImageFormUrl(imgUrl,ImgCaptureView, dateString, proxString);
                        break;

                }
            }
        });


    }


    private void loadImageFormUrl(String imageUri, final LoadingImageView ImgCaptureView, final String date, final String prox ){

        ImgCaptureView.startLoading();
        statusView.setStatus(Status.LOADING);


        dateText.startLoading();
        titleText.startLoading();
        sensorText.startLoading();

        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_camera_iris_grey600_48dp) // resource or drawable
                .showImageOnFail(R.drawable.ic_camera_iris_grey600_48dp) // resource or drawable
                .build();

        imageLoader.displayImage(imageUri, ImgCaptureView, options);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImgCaptureView.stopLoading();
                statusView.setStatus(Status.ERROR);
                dateText.stopLoading();
                titleText.stopLoading();
                sensorText.stopLoading();
                titleText.setText("Ultima Captura");
                sensorText.setText("Proximidad:" + " " + prox + "cm");
                dateText.setText(date);

            }
        }, 1700);

    }

    private void setImgUrl(String url, String date, String prox){
        imgUrl = url;
        proxString = prox;
        dateString = date;

    }


    private void showNotificationStateRequest(String ticker, String message){
        mNotification.setTicker(ticker);
        mNotification.setMessage(message);
        mNotification.setIntent(new Intent(this, MainActivity.class));
        mNotification.show(Notification.ID_NOTIFICATION_GLOBAL);
    }
}
