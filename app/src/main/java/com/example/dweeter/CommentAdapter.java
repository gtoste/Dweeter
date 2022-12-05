package com.example.dweeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    ArrayList<Comment> comments;
    Context context;
    LayoutInflater inflater;

    public CommentAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DatabaseReference db = FirebaseDatabase.getInstance("https://dweeter-4e7ad-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        view = inflater.inflate(R.layout.comment, null);

        TextView nick = view.findViewById(R.id.account_nick);
        ImageView image = view.findViewById(R.id.account_icon);
        TextView message = view.findViewById(R.id.message);
        TextView dateOfCreation = view.findViewById(R.id.date_of_creation);

        Comment comment = comments.get(i);

        dateOfCreation.setText(comment.dateOfCreation);
        nick.setText(comment.accountName);
        Picasso.get()
                .load(comment.accountUrl)
                .resize(48, 48).into(image);
        message.setText(comment.message);


        return view;
    }
}
