package in.androidhunt.musicDEmo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dm.audiostreamer.MediaMetaData;
import in.androidhunt.musicDEmo.R;

public class PlayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final List<MediaMetaData> songsList;
    private final String title;
    private final String musicDir;
    private final Context context;
    private SetOnclickListner onClickListener;

    public PlayListAdapter(Context context, List<MediaMetaData> songList, String title, String musicDir) {
        this.songsList = songList;
        this.title = title;
        this.musicDir = musicDir;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.albuminfo_item, parent, false);
            return new ArtistViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final MediaMetaData m = songsList.get(position - 1);
            Long duration = Long.parseLong(m.getMediaDuration());
            int milliseconds = (int) TimeUnit.SECONDS.toMillis(duration);
            int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
            int seconds = (int) ((milliseconds / 1000) % 60);
            if (Integer.toString(seconds).length() == 1) {
                String sec = "0" + Integer.toString(seconds);
                ((ViewHolder) holder).duration.setText(Integer.toString(minutes) + ":" + sec);
            } else {
                ((ViewHolder) holder).duration.setText(Integer.toString(minutes) + ":" + Integer.toString(seconds));
            }
//        Picasso.with(context).load(m.getThumbnailUrl()).error(R.mipmap.ic_launcher)
//                .into(((ViewHolder) holder).imageView);
            ((ViewHolder) holder).songNo.setText(position + "");
            String tit = m.getMediaTitle();
            ((ViewHolder) holder).title.setText(tit);
            ((ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickSong(m, position - 1);
                }
            });
        } else {
            ((ArtistViewHolder) holder).title.setText(title);
            ((ArtistViewHolder) holder).artist.setText(musicDir);
            ((ArtistViewHolder) holder).songs.setText(songsList.size() + " songs");
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return songsList.size() + 1;
    }

    public void setOnClickListener(SetOnclickListner onClickListener) {
        this.onClickListener = onClickListener;

    }

    public void notifyPlayState(MediaMetaData metaData) {
        if (this.songsList != null && metaData != null) {
            int index = this.songsList.indexOf(metaData);
            //TODO SOMETIME INDEX RETURN -1 THOUGH THE OBJECT PRESENT IN THIS LIST
            if (index == -1) {
                for (int i = 0; i < this.songsList.size(); i++) {
                    if (this.songsList.get(i).getMediaId().equalsIgnoreCase(metaData.getMediaId())) {
                        index = i;
                        break;
                    }
                }
            }
            if (index > 0 && index < this.songsList.size()) {
                this.songsList.set(index, metaData);
            }
        }
        notifyDataSetChanged();
    }

    public interface SetOnclickListner {
        void onClickSong(MediaMetaData album, int postion);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView songNo;
        TextView title;
        TextView duration;

        public ViewHolder(View v) {
            super(v);
            songNo = (TextView) v.findViewById(R.id.itemThumbnail_View);
            title = (TextView) v.findViewById(R.id.itemVideoTitle_View);
            duration = (TextView) v.findViewById(R.id.dur_ation);
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView artist;
        TextView title;
        TextView songs;

        public ArtistViewHolder(View v) {
            super(v);
            artist = (TextView) v.findViewById(R.id.artist_name);
            title = (TextView) v.findViewById(R.id.album_name);
            songs = (TextView) v.findViewById(R.id.s_info);
        }
    }

}
