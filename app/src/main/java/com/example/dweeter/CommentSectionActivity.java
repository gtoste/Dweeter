package com.example.dweeter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CommentSectionActivity extends AppCompatActivity {

    TweetModel tweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_section);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Person person = new Person();
        final boolean logdin;
        if(firebaseAuth.getCurrentUser() != null)
        {
            person = new Person(firebaseAuth.getCurrentUser().getDisplayName(),
                    firebaseAuth.getCurrentUser().getPhotoUrl().toString(),
                    firebaseAuth.getCurrentUser().getUid()
            );
            logdin = true;
        }else{
            logdin = false;
        }

        DatabaseReference db = FirebaseDatabase.getInstance("https://dweeter-4e7ad-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        ImageView accountIcon = findViewById(R.id.account_icon);
        TextView accountNick = findViewById(R.id.account_nick);
        TextView tweetMessage = findViewById(R.id.message);

        EditText comment = findViewById(R.id.your_comment);
        TextView dateofc = findViewById(R.id.date_of_creation);
        Button sendComment = findViewById(R.id.send_comment);
        sendComment.setClickable(false);
        ListView otherComments = findViewById(R.id.other_comments);
        ArrayList<Comment> comments = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.tweet = (TweetModel) extras.get("tweet");
            Picasso.get()
                    .load(tweet.accountUrl)
                    .resize(48, 48).into(accountIcon);
            accountNick.setText(tweet.accountNick);
            tweetMessage.setText(tweet.message);
            dateofc.setText(tweet.dateOfCreation);
        }

        Context mContext = this;
        db.child("comments").child(tweet.path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for (DataSnapshot comment: snapshot.getChildren()) {
                    comments.add(comment.getValue(Comment.class));
                }

                Collections.reverse(comments);
                CommentAdapter commentAdapter = new CommentAdapter(comments, mContext);
                otherComments.setAdapter(commentAdapter);

                sendComment.setClickable(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!logdin)
        {
            comment.setEnabled(false);
            comment.setText("You must login first!");
            sendComment.setClickable(false);
            sendComment.setEnabled(false);
        }

        Person finalPerson = person;
        sendComment.setOnClickListener(view -> {
            String commentMessage = comment.getText().toString().trim();
            if(!commentMessage.equals("")){

                Date date = new Date();
                SimpleDateFormat simpleDateFormat = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    simpleDateFormat = new SimpleDateFormat(" HH:mm:ss dd/MM/YYYY");
                }
                String dateString = simpleDateFormat.format(date);
                Comment c = new Comment(finalPerson.name, finalPerson.url, commentMessage, dateString);
                comments.add(c);
                db.child("comments").child(tweet.path).setValue(comments);
                comment.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                comment.clearFocus();
            }else{
                comment.setError("Type something");
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}