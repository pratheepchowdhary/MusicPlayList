package in.androidhunt.musicDEmo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.androidhunt.musicDEmo.R;
import in.androidhunt.musicDEmo.model.Album;

public class AlbumListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Album> albumList;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public AlbumListAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {


            final Album m = albumList.get(position);

            ((ViewHolder) holder).title.setText(m.getName());
            ((ViewHolder) holder).gener.setText(m.getAbout());
            Picasso.with(context).load(m.getThumbnail()).into(((ViewHolder) holder).imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Album source = albumList.get(position);
                    String status = source.getStatus();
                    if (status.contains("inactive")) {
                        String mesg = "Sorry Currently Un Available";
                        Toast.makeText(context, mesg, Toast.LENGTH_LONG).show();
                    } else {
                        onItemClickListener.onItemClick(v, source);

                    }

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Album album);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail)
        ImageView imageView;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.count)
        TextView gener;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}








