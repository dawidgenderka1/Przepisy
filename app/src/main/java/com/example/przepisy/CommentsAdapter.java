package com.example.przepisy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> commentsList;

    public CommentsAdapter(List<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        // Ustawianie tekstu itd.
        holder.commentText.setText(comment.getCommentText());
        String inputPattern = "yyyy-MM-dd"; // Załóżmy, że data jest w tym formacie
        String outputPattern = "dd-MM-yyyy"; // Żądany format wyjściowy
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.getDefault());

        try {
            Date date = inputFormat.parse(comment.getCommentDate());
            String formattedDate = outputFormat.format(date);
            holder.commentDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.commentDate.setText(comment.getCommentDate()); // Ustaw oryginalną datę, jeśli wystąpi błąd
        }
        holder.userID.setText(comment.getUsername());

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentText, commentDate, userID;

        public ViewHolder(View view) {
            super(view);
            userID = view.findViewById(R.id.usernameTextView);
            commentText = view.findViewById(R.id.commentTextView);
            commentDate = view.findViewById(R.id.dateTextView);
        }
    }

    public void updateData(List<Comment> newData) {
        commentsList.clear();
        commentsList.addAll(newData);
        notifyDataSetChanged();
    }
}
