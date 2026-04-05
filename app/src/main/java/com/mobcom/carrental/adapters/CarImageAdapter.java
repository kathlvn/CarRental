package com.mobcom.carrental.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mobcom.carrental.R;
import java.util.List;

public class CarImageAdapter extends RecyclerView.Adapter<CarImageAdapter.ImageViewHolder> {

    public interface OnImageRemoveListener {
        void onRemove(int position);
    }

    private List<Uri> imageUris;
    private Context context;
    private OnImageRemoveListener listener;

    public CarImageAdapter(Context context, List<Uri> imageUris, OnImageRemoveListener listener) {
        this.context = context;
        this.imageUris = imageUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_car_image, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUris.get(position))
                .centerCrop()
                .into(holder.imgPreview);

        holder.btnRemove.setOnClickListener(v -> listener.onRemove(position));
    }

    @Override
    public int getItemCount() { return imageUris.size(); }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPreview;
        TextView btnRemove;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPreview = itemView.findViewById(R.id.imgPreview);
            btnRemove  = itemView.findViewById(R.id.btnRemove);
        }
    }
}