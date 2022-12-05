package com.example.dweeter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateTweet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tweet);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://dweeter-4e7ad-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference tweetsRef = firebaseDatabase.getReference("tweets");
        EditText tweetContent = findViewById(R.id.tweetMessage);
        Button btnOk = findViewById(R.id.button_ok);
        Button btnCancel = findViewById(R.id.button_cancel);


        String linkTo = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            linkTo = extras.getString("linkTo");
        }

        //TODO
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Person person = new Person(firebaseAuth.getCurrentUser().getDisplayName(),
                    firebaseAuth.getCurrentUser().getPhotoUrl().toString(),
                    firebaseAuth.getCurrentUser().getUid()
            );


        String finalLinkTo = linkTo;
        btnOk.setOnClickListener((v)->{
            String uid = String.valueOf(new Date().getTime());
            String tweet = tweetContent.getText().toString().trim();
            if(!tweet.equals(""))
            {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    simpleDateFormat = new SimpleDateFormat(" HH:mm:ss dd/MM/YYYY");
                }
                String dateString = simpleDateFormat.format(date);
                TweetModel tweetModel = new TweetModel(uid, person.name, person.url, tweet, finalLinkTo, 0, dateString);
                tweetsRef.child(uid).setValue(tweetModel);
                goToMain();
            }else{
                tweetContent.setError("Type something");
            }
        });

        btnCancel.setOnClickListener((v)->goToMain());
    }

    private void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}