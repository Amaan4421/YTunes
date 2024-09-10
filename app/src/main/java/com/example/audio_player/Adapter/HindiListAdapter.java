package com.example.audio_player.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HindiListAdapter extends RecyclerView.Adapter<HindiListAdapter.ViewHolder> {

    private final Context context;
    private final ClickEvent onItemClickListener;
    private final List<YoutubeModel> youtubeModels;

    public HindiListAdapter(Context context, List<YoutubeModel> youtubeModels, ClickEvent onItemClickListener) {
        this.context = context;
        this.youtubeModels = youtubeModels;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_hindi_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        YoutubeModel youtubeModel = youtubeModels.get(position);
        holder.audioTitle.setText(youtubeModel.getVideoTitle());
        holder.audioDuration.setText(youtubeModel.getDuration());
        Picasso.get().load(youtubeModel.getVideoImageUrl()).into(holder.audioImage);


        //pass the onClick event to main activity for playing that song
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.onItemClick(youtubeModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return youtubeModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioTitle, audioDuration;
        ImageView audioImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioTitle = itemView.findViewById(R.id.audio_title);
            audioDuration = itemView.findViewById(R.id.audio_duration);
            audioImage = itemView.findViewById(R.id.videoImage);
        }
    }

    public interface ClickEvent {
        void onItemClick(YoutubeModel youtubeModel);
    }
}
