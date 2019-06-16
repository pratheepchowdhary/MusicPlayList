package in.androidhunt.musicDEmo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ohoussein.playpause.PlayPauseView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import dm.audiostreamer.AudioStreamingManager;
import dm.audiostreamer.CurrentSessionCallback;
import dm.audiostreamer.Logger;
import dm.audiostreamer.MediaMetaData;
import in.androidhunt.musicDEmo.adapter.PlayListAdapter;
import in.androidhunt.musicDEmo.model.Album;
import in.androidhunt.musicDEmo.view.LineProgress;
import in.androidhunt.musicDEmo.view.Slider;
import in.androidhunt.musicDEmo.view.SquareImageView;

public class PlaylistActivity extends AppCompatActivity implements CurrentSessionCallback, View.OnClickListener, Slider.OnValueChangedListener {

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.playlist_layout)
    RecyclerView playList;
    @BindView(R.id.avi_loader)
    ProgressBar avi;
    @BindView(R.id.image_album)
    SquareImageView albumImage;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout mLayout;
    @BindView(R.id.btn_play)
    PlayPauseView btn_play;
    @BindView(R.id.image_songAlbumArt)
    ImageView image_songAlbumArt;
    @BindView(R.id.img_bottom_albArt)
    ImageView img_bottom_albArt;
    @BindView(R.id.image_songAlbumArtBlur)
    ImageView image_songAlbumArtBlur;
    @BindView(R.id.slidepanel_time_progress)
    TextView time_progress_slide;
    @BindView(R.id.slidepanel_time_total)
    TextView time_total_slide;
    @BindView(R.id.slidepanel_time_progress_bottom)
    TextView time_progress_bottom;
    @BindView(R.id.slidepanel_time_total_bottom)
    TextView time_total_bottom;
    @BindView(R.id.pgPlayPauseLayout)
    RelativeLayout pgPlayPauseLayout;
    @BindView(R.id.lineProgress)
    LineProgress lineProgress;
    @BindView(R.id.audio_progress_control)
    Slider audioPg;
    @BindView(R.id.btn_backward)
    ImageView btn_backward;
    @BindView(R.id.text_songName)
    TextView text_songName;
    @BindView(R.id.text_songAlb)
    TextView text_songAlb;
    @BindView(R.id.txt_bottom_SongName)
    TextView txt_bottom_SongName;
    @BindView(R.id.txt_bottom_SongAlb)
    TextView txt_bottom_SongAlb;
    @BindView(R.id.slideBottomView)
    RelativeLayout slideBottomView;
    @BindView(R.id.cover_mirror)
    ImageView image_mirror;
    private PlayListAdapter adapter;
    private boolean isExpand = false;
    private Context context;
    @BindView(R.id.btn_forward)
     ImageView btn_forward;
    //For  Implementation
    private AudioStreamingManager streamingManager;
    private MediaMetaData currentSong;
    private List<MediaMetaData> listOfSongs = new ArrayList<MediaMetaData>();
    private String title;
    private String musicDir;
    private String platList_Id;
    private String albumImg;
    private boolean isPlaylistAdded;
    public  static  final String PLAYLIST_ID="playlist_id";
    public static  final String MUSIC_DIR="music_dir";
    public static final  String ALBUM_ART="album_art";
    public static final String ALBUM_NAME="album_name";
    public static void navigate(SongsActivity songsActivity, View viewById, Album album) {
        String about = album.getMusicDir();
        String imgurl = album.getThumbnail();
        String title = album.getName();
        String playlistid = album.getId();
        Intent intent = new Intent(songsActivity, PlaylistActivity.class);
        intent.putExtra(PLAYLIST_ID, playlistid);
        intent.putExtra(MUSIC_DIR, about);
        intent.putExtra(ALBUM_ART, imgurl);
        intent.putExtra(ALBUM_NAME, title);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(songsActivity, viewById, imgurl);
        ActivityCompat.startActivity(songsActivity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.BaseTheme_playlist);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        initActivityTransitions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);
        ButterKnife.bind(this);
        Intent i = getIntent();
        title = i.getStringExtra(ALBUM_NAME);
        musicDir = i.getStringExtra(MUSIC_DIR);
        platList_Id = i.getStringExtra(PLAYLIST_ID);
        albumImg = i.getStringExtra(ALBUM_ART);
        //Used  or Element View Transitions
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), albumImg);
        supportPostponeEnterTransition();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new PlayListAdapter(this, listOfSongs, title, musicDir);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        playList.setLayoutManager(mLayoutManager);
        playList.setItemAnimator(new DefaultItemAnimator());
        playList.setAdapter(adapter);
        Picasso.with(getBaseContext()).load(albumImg).into(albumImage);
        Picasso.with(this).load(albumImg).into(albumImage, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) albumImage.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        adapter.setOnClickListener(new PlayListAdapter.SetOnclickListner() {
            @Override
            public void onClickSong(MediaMetaData album, int postion) {
                if (!isPlaylistAdded) {
                    streamingManager.setMediaList(listOfSongs);
                    isPlaylistAdded = true;
                    checkAlreadyPlaying();
                    streamingManager.setShowPlayerNotification(true);
                    streamingManager.setPendingIntentAct(getNotificationPendingIntent());
                }
                playSong(album);

            }
        });
        ImageButton closeDrawer = findViewById(R.id.down_arrow);
        closeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpand) {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        });
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOfSongs.size() > 0) {
                    MediaMetaData album = listOfSongs.get(0);
                    if (!isPlaylistAdded) {
                        streamingManager.setMediaList(listOfSongs);
                        isPlaylistAdded = true;
                        checkAlreadyPlaying();
                        streamingManager.setShowPlayerNotification(true);
                        streamingManager.setPendingIntentAct(getNotificationPendingIntent());
                    }
                    playSong(album);
                }
            }
        });
        this.context = PlaylistActivity.this;
        configAudioStreamer();
        uiInitialization();
        onLoadPlaylist(platList_Id, albumImg);
        checkAlreadyPlaying();


    }

    @Override
    public void onResume() {
        super.onResume();
        checkAlreadyPlaying();
        if (streamingManager.isPlaying()) {
            btn_play.change(false, true);
        } else {
            btn_play.change(true, true);
        }
    }

    public void onLoadPlaylist(String movie, final String img) {
        if (isNetworkAvailable()) {
            OfflineMode();
            if (!movie.equals("")) {
                avi.setVisibility(View.VISIBLE);
                String songs = "https://api.soundcloud.com/playlists/" + movie + "?client_id=95f22ed54a5c297b1c41f72d713623ef";
                JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, songs,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        storeResponse(response.toString());
                        avi.setVisibility(View.GONE);
                        listOfSongs.clear();
                        try {
                            JSONArray obj = response.getJSONArray("tracks");
                            // Parsing json
                            for (int i = 0; i < obj.length(); i++) {
                                JSONObject media = obj.getJSONObject(i);
                                MediaMetaData mediaMetaData = new MediaMetaData();
                                mediaMetaData.setMediaAlbum(title);
                                mediaMetaData.setMediaArt(img);
                                mediaMetaData.setMediaArtist(musicDir);
                                String songUrl = media.getString("stream_url") + "?client_id=95f22ed54a5c297b1c41f72d713623ef";
                                String songtitle = media.getString("title").replaceAll(".mp3", "").replaceAll("[0-9]", "").replaceAll("\\[.*?\\]", "").replaceAll("-", "").replaceAll("_", "").replaceAll("\\.", "").replaceAll("\\p{P}", "").replaceAll("Kbps", "");
                                mediaMetaData.setMediaUrl(songUrl);
                                long duration = Long.parseLong(media.getString("duration"));
                                mediaMetaData.setMediaDuration(TimeUnit.MILLISECONDS.toSeconds(duration) + "");
                                mediaMetaData.setMediaTitle(songtitle);
                                mediaMetaData.setMediaId(songtitle);
                                listOfSongs.add(mediaMetaData);
                            }

                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avi.setVisibility(View.GONE);

                    }
                });
                // Adding request to request queue
                App.getInstance().addToRequestQueue(movieReq);
            }
        } else {
            OfflineMode();
            avi.setVisibility(View.GONE);
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.sliding_layout), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.setAction("Offline", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }


    }

    private void storeResponse(String response) {
        SharedPreferences sharedPref = getSharedPreferences(title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString(title + "123", response);
        editor.commit();
    }

    public void OfflineMode() {
        listOfSongs.clear();
        SharedPreferences sharedPref = getSharedPreferences(title, Context.MODE_PRIVATE);
        String res = sharedPref.getString(title + "123", "");
        try {
            JSONObject response = new JSONObject(res);
            JSONArray obj = response.getJSONArray("tracks");
            for (int i = 0; i < obj.length(); i++) {
                JSONObject media = obj.getJSONObject(i);
                MediaMetaData mediaMetaData = new MediaMetaData();
                mediaMetaData.setMediaAlbum(title);
                mediaMetaData.setMediaArt(albumImg);
                mediaMetaData.setMediaArtist(musicDir);
                String songUrl = media.getString("stream_url") + "?client_id=bd6d136e05d880eea1bc0b1a7bcad42f";
                String songtitle = media.getString("title").replaceAll(".mp3", "").replaceAll("[0-9]", "").replaceAll("\\[.*?\\]", "").replaceAll("-", "").replaceAll("_", "").replaceAll("\\.", "").replaceAll("\\p{P}", "").replaceAll("Kbps", "");
                mediaMetaData.setMediaId(songtitle);
                mediaMetaData.setMediaUrl(songUrl);
                long duration = Long.parseLong(media.getString("duration"));
                mediaMetaData.setMediaDuration(TimeUnit.MILLISECONDS.toSeconds(duration) + "");
                mediaMetaData.setMediaTitle(songtitle);
                listOfSongs.add(mediaMetaData);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground(findViewById(R.id.fab), palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));
        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    private void uiInitialization() {
        btn_backward.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        pgPlayPauseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        if (streamingManager.isPlaying()) {
            btn_play.change(false, true);

        } else {
            btn_play.change(true, true);
        }

        slideBottomView.setVisibility(View.VISIBLE);
        slideBottomView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        audioPg.setMax(0);
        audioPg.setOnValueChangedListener(this);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 0.0f) {
                    isExpand = false;
                    slideBottomView.setVisibility(View.VISIBLE);
                    //slideBottomView.getBackground().setAlpha(0);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    //slideBottomView.getBackground().setAlpha((int) slideOffset * 255);
                } else {
                    //slideBottomView.getBackground().setAlpha(100);
                    isExpand = true;
                    slideBottomView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (SlidingUpPanelLayout.PanelState.EXPANDED == newState) {
                    isExpand = true;
                } else if (SlidingUpPanelLayout.PanelState.COLLAPSED == newState) {
                    isExpand = false;
                }
            }
        });
    }


    private void configAudioStreamer() {
        streamingManager = AudioStreamingManager.getInstance(context);
        //Set PlayMultiple 'true' if want to playing sequentially one by one songs
        // and provide the list of songs else set it 'false'
        streamingManager.setPlayMultiple(true);
//        streamingManager.setMediaList(listOfSongs);
        //If you want to show the Player Notification then set ShowPlayerNotification as true
        //and provide the pending intent so that after click on notification it will redirect to an activity

    }


    @Override
    public void onBackPressed() {
        if (isExpand) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (streamingManager != null) {
                streamingManager.subscribesCallBack(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        try {
            if (streamingManager != null) {
                streamingManager.unSubscribeCallBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            if (streamingManager != null) {
                streamingManager.unSubscribeCallBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void updatePlaybackState(int state) {
        Logger.e("updatePlaybackState: ", "" + state);
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                pgPlayPauseLayout.setVisibility(View.INVISIBLE);
                btn_play.change(false, true);
                if (currentSong != null) {
                    currentSong.setPlayState(PlaybackStateCompat.STATE_PLAYING);
                    notifyAdapter(currentSong);
                }
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                pgPlayPauseLayout.setVisibility(View.INVISIBLE);
                btn_play.change(true, true);
                if (currentSong != null) {
                    currentSong.setPlayState(PlaybackStateCompat.STATE_PAUSED);
                    notifyAdapter(currentSong);
                }
                break;
            case PlaybackStateCompat.STATE_NONE:
                currentSong.setPlayState(PlaybackStateCompat.STATE_NONE);
                notifyAdapter(currentSong);
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                pgPlayPauseLayout.setVisibility(View.INVISIBLE);
                btn_play.change(true, true);
                audioPg.setValue(0);
                if (currentSong != null) {
                    currentSong.setPlayState(PlaybackStateCompat.STATE_NONE);
                    notifyAdapter(currentSong);
                }
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                pgPlayPauseLayout.setVisibility(View.VISIBLE);
                if (currentSong != null) {
                    btn_play.change(true, true);
                    currentSong.setPlayState(PlaybackStateCompat.STATE_NONE);
                    notifyAdapter(currentSong);
                }
                break;
        }
    }

    @Override
    public void playSongComplete() {
        String timeString = "00.00";
        time_total_bottom.setText(timeString);
        time_total_slide.setText(timeString);
        time_progress_bottom.setText(timeString);
        time_progress_slide.setText(timeString);
        lineProgress.setLineProgress(0);
        audioPg.setValue(0);
    }

    @Override
    public void currentSeekBarPosition(int progress) {
        audioPg.setValue(progress);
        setPGTime(progress);
    }

    @Override
    public void playCurrent(int indexP, MediaMetaData currentAudio) {
        showMediaInfo(currentAudio);
        notifyAdapter(currentAudio);
    }

    @Override
    public void playNext(int indexP, MediaMetaData CurrentAudio) {
        showMediaInfo(CurrentAudio);
    }

    @Override
    public void playPrevious(int indexP, MediaMetaData currentAudio) {
        showMediaInfo(currentAudio);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forward:
                streamingManager.onSkipToNext();
                break;
            case R.id.btn_backward:
                streamingManager.onSkipToPrevious();
                break;
            case R.id.btn_play:
                if (currentSong != null) {
                    playPauseEvent(view);
                }
                break;

        }
    }

    @Override
    public void onValueChanged(int value) {
        streamingManager.onSeekTo(value);
        streamingManager.scheduleSeekBarUpdate();
    }

    private void notifyAdapter(MediaMetaData media) {
        adapter.notifyPlayState(media);
    }

    private void playPauseEvent(View v) {
        if (streamingManager.isPlaying()) {
            streamingManager.onPause();
            ((PlayPauseView) v).change(true, true);
        } else {
            streamingManager.onPlay(currentSong);
            ((PlayPauseView) v).change(false, true);
        }
    }

    private void playSong(MediaMetaData media) {
        if (streamingManager != null) {
            streamingManager.onPlay(media);
            showMediaInfo(media);
        }
    }

    private void showMediaInfo(MediaMetaData media) {
        currentSong = media;
        audioPg.setValue(0);
        audioPg.setMin(0);
        audioPg.setMax(Integer.valueOf(media.getMediaDuration()) * 1000);
        setPGTime(0);
        setMaxTime();
        loadSongDetails(media);
    }

    private void checkAlreadyPlaying() {
        if (streamingManager.isPlaying()) {
            currentSong = streamingManager.getCurrentAudio();
            if (currentSong != null) {
                currentSong.setPlayState(streamingManager.mLastPlaybackState);
                showMediaInfo(currentSong);
                notifyAdapter(currentSong);
                if (streamingManager.mLastPlaybackState == PlaybackState.STATE_BUFFERING) {
                    btn_play.change(true, true);
                    pgPlayPauseLayout.setVisibility(View.VISIBLE);
                }


            }

        }
    }

    private void loadSongDetails(MediaMetaData metaData) {
        text_songName.setText(metaData.getMediaTitle());
        text_songAlb.setText(metaData.getMediaArtist());
        txt_bottom_SongName.setText(metaData.getMediaTitle());
        txt_bottom_SongAlb.setText(metaData.getMediaArtist());
        Picasso.with(PlaylistActivity.this).load(metaData.getMediaArt()).into(img_bottom_albArt);
        Picasso.with(PlaylistActivity.this).load(metaData.getMediaArt()).into(image_songAlbumArt);
        Picasso.with(getBaseContext()).load(metaData.getMediaArt()).into(new Target() {

            @Override
            public void onBitmapFailed(Drawable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onBitmapLoaded(Bitmap arg0, Picasso.LoadedFrom arg1) {
                image_songAlbumArtBlur.setImageBitmap(ImageUtils.fastblur(arg0, 0.1f, 10));
                int h = 300;
                int s = 80;
                image_mirror.setImageBitmap(ImageUtils.createReflectionBitmapForSingle(arg0, h, s));


            }

            @Override
            public void onPrepareLoad(Drawable arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void setPGTime(int progress) {
        try {
            String timeString = "00.00";
            int linePG = 0;
            currentSong = streamingManager.getCurrentAudio();
            if (currentSong != null && progress != Long.parseLong(currentSong.getMediaDuration())) {
                timeString = DateUtils.formatElapsedTime(progress / 1000);
                Long audioDuration = Long.parseLong(currentSong.getMediaDuration());
                linePG = (int) (((progress / 1000) * 100) / audioDuration);
            }
            time_progress_bottom.setText(timeString);
            time_progress_slide.setText(timeString);
            lineProgress.setLineProgress(linePG);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private void setMaxTime() {
        try {
            String timeString = DateUtils.formatElapsedTime(Long.parseLong(currentSong.getMediaDuration()));
            time_total_bottom.setText(timeString);
            time_total_slide.setText(timeString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getNotificationPendingIntent() {
        Intent intent = new Intent(context, PlaylistActivity.class);
        intent.putExtra(PLAYLIST_ID, platList_Id);
        intent.putExtra(MUSIC_DIR, musicDir);
        intent.putExtra(ALBUM_ART, albumImg);
        intent.putExtra(ALBUM_NAME, title);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

}
