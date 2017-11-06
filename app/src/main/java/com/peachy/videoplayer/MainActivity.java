package com.peachy.videoplayer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

import com.peachy.videoplayer.model.Media;
import com.peachy.videoplayer.model.Playlist;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String PATH = "/media/";
    public static final String JSON_FILE_NAME = "simulateJson.json";

    VideoView videoPlayer;
    ImageView imageShowIV;

    Map<Integer, Media> medias;
    Map<Integer, Playlist> playlists;
    ArrayList<Integer> playlistKeys;

    private int indexOfMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 999);
        videoPlayer = (VideoView) findViewById(R.id.videoPlayer);
        imageShowIV = (ImageView) findViewById(R.id.imageShowIV);

        try {
            readJSONfile();
        } catch (Exception e) {
            showSubmitDialog("Something went wrong!", "Please check your JSON file");
        }

        try {
            startPlayMedia(0);
        } catch (Exception e) {
            e.printStackTrace();
            showSubmitDialog("Something went wrong!", "Please check your Media file");
        }
    }

    private void readJSONfile() throws Exception {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Media> jsonMediaAdapter = moshi.adapter(Media.class);
        JsonAdapter<Playlist> jsonPlaylistAdapter = moshi.adapter(Playlist.class);
        String jsonData = JsonUtil.loadJSONFromFile(getPath() + PATH + JSON_FILE_NAME);
        JSONObject jsonObj = new JSONObject(jsonData);
        JSONArray playlist_arr = jsonObj.getJSONArray("playlist_arr");
        JSONArray media_info_arr = jsonObj.getJSONArray("media_info");

        medias = new HashMap<>();
        playlists = new HashMap<>();
        playlistKeys = new ArrayList<>();

        for (int i = 0; i < playlist_arr.length(); i++) {
            Playlist playlist = jsonPlaylistAdapter.fromJson(playlist_arr.get(i).toString());
            playlists.put(playlist.id, playlist);
            playlistKeys.add(playlist.id);
        }
        Collections.sort(playlistKeys);

        for (int i = 0; i < media_info_arr.length(); i++) {
            Media media = jsonMediaAdapter.fromJson(media_info_arr.get(i).toString());
            medias.put(media.id, media);
        }
        indexOfMedia = 0;

    }

    public String getPath() {
        File file = new File("storage/");
        File[] listOfStorages = file.listFiles();

        boolean isSDPresent = false;

        if (listOfStorages[1].getName().contains("emmulated")
                || listOfStorages[0].getName().contains("-")) {
            isSDPresent = true;
        }

        if (isSDPresent) {
            return listOfStorages[0].getAbsolutePath();
        } else {
            return Environment.getExternalStorageDirectory().toString();
        }
    }

    private void startPlayMedia(int indexOfMedia) throws Exception {
        if (indexOfMedia >= playlistKeys.size()) {
            indexOfMedia = 0;
        }
        Playlist playlist = playlists.get(playlistKeys.get(indexOfMedia));
        boolean isInProperTime = isInProperTime(playlist);
        if (!isInProperTime(playlist)) {
            startPlayMedia(indexOfMedia + 1);
            return;
        }
        Media media = medias.get(playlist.media_id);

        if (media.type.equals("image")) {
            setUpImageView(media);
        } else if (media.type.equals("video")) {
            setUpVideo(media);
        } else {
            throw new Exception();
        }

        this.indexOfMedia = indexOfMedia;
        handler.postDelayed(runnable, playlist.duration * 1000);
    }

    private boolean isInProperTime(Playlist playlist) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(playlist.start_date);
        Date endDate = sdf.parse(playlist.end_date);

        SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");
        Date currentTime = timeSdf.parse(
                timeSdf.format(currentDate)
        );
        Date startTime = timeSdf.parse(playlist.start_time);
        Date endTime = timeSdf.parse(playlist.end_time);

        boolean temp1 = currentDate.before(startDate);
        boolean temp2 = currentDate.after(endDate);
        if (currentDate.before(startDate) || currentDate.after(endDate)) {
            return false;
        } else if (currentTime.before(startTime) || currentTime.after(endTime)) {
            return false;
        } else {
            return true;
        }

    }

    private void setUpImageView(Media media) {
        videoPlayer.stopPlayback();
        videoPlayer.setVisibility(View.GONE);

        String path = getPath() + PATH + media.file_path;
        Uri uri = Uri.parse(path);
        imageShowIV.setImageURI(uri);
        imageShowIV.setVisibility(View.VISIBLE);
    }

    private void setUpVideo(Media media) {
        imageShowIV.setVisibility(View.GONE);

        String path = getPath() + PATH + media.file_path;
        Uri uri = Uri.parse(path);
        videoPlayer.setVideoURI(uri);
        videoPlayer.setVisibility(View.VISIBLE);
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoPlayer.start();
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                startPlayMedia(MainActivity.this.indexOfMedia + 1);
            } catch (Exception e) {
                e.printStackTrace();
                showSubmitDialog("Something went wrong!", "Please check your JSON file or Media file");
            }
        }
    };


    private void showSubmitDialog(String title, String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        } else {
            builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        }
        builder.setTitle(title)
                .setMessage(message)
                .show();
    }


}
