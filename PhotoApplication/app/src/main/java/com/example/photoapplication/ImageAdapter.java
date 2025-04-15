package com.example.photoapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    Context context;
    ArrayList<ImageItem> imageList;

    public ImageAdapter(Context context, ArrayList<ImageItem> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_design, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageItem item = imageList.get(position);

        holder.title.setText(item.getName());
        holder.desc.setText(item.getDate());

        Uri imageUri = Uri.parse(item.getUri());
        holder.imageView.setImageURI(imageUri);

        // to open IndividualImage activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, IndividualImage.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("uri", item.getUri());
            intent.putExtra("date", item.getDate());
            intent.putExtra("size", item.getSize());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, desc;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_view);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
        }
    }
}
