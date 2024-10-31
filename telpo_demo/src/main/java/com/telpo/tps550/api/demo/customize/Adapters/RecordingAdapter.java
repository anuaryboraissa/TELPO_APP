package com.telpo.tps550.api.demo.customize.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softnet.devicetester.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {
    private final List<File> recordings;
    private final Context context;

    public RecordingAdapter(List<File> recordings, Context context) {
        this.recordings = recordings;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File recording = recordings.get(position);
        holder.tvFileName.setText(recording.getName());
        holder.tvFileSize.setText(getReadableFileSize(recording.length()));

        holder.btnPlay.setOnClickListener(v -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(context, Uri.fromFile(recording));
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvFileSize;
        ImageButton btnPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFileSize = itemView.findViewById(R.id.tv_file_size);
            btnPlay = itemView.findViewById(R.id.btn_play);
        }
    }

    // Helper function to convert file size to a readable format
    private String getReadableFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
