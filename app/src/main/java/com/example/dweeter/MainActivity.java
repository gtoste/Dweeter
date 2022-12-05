package com.example.dweeter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {


    private ListView dashboard;
    private FloatingActionButton addMessage;
    FirebaseDatabase database;
    private ArrayList<TweetModel> tweets;
    private boolean searchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.dashboard = findViewById(R.id.dashboard);
        this.addMessage = findViewById(R.id.addMessage);
        database = FirebaseDatabase.getInstance("https://dweeter-4e7ad-default-rtdb.europe-west1.firebasedatabase.app");

        this.addMessage.setOnClickListener(this::newTweet);
        loadTweets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadTweets();
    }

    private void newTweet(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(this, CreateTweet.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "You must login first!", Toast.LENGTH_LONG).show();
        }

    }

    private void loadTweets(){
        Context mContext = this;
        this.database.getReference("tweets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tweets = new ArrayList<>();
                for (DataSnapshot snapTweet: snapshot.getChildren()) {
                    TweetModel tweet = snapTweet.getValue(TweetModel.class);
                    tweets.add(tweet);
                }
                Collections.reverse(tweets);
                DashboardAdapter dashboardAdapter = new DashboardAdapter(mContext, tweets);
                dashboard.setAdapter(dashboardAdapter);
                dashboard.invalidateViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search)
        {
            RelativeLayout main = findViewById(R.id.main);
            Button button = new Button(this);
            button.setText("Cancel...");
            button.setOnClickListener((v)->{
                loadTweets();
                main.removeView(button);
                searchMode = false;
            });
            if(!searchMode)
            {
                main.addView(button);
            }

            searchMode = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String inputText = input.getText().toString().trim().toLowerCase(Locale.ROOT);
                if (!inputText.equals(""))
                {
                    Pattern p = Pattern.compile("^.*"+inputText+".*$");
                    ArrayList<TweetModel> searchTweets = new ArrayList<>();
                    for (TweetModel tweet: tweets) {
                        Matcher m = p.matcher(tweet.message.toLowerCase(Locale.ROOT));
                        if(m.matches()) searchTweets.add(tweet);
                    }
                    Collections.reverse(searchTweets);
                    DashboardAdapter dashboardAdapter = new DashboardAdapter(this, searchTweets);
                    dashboard.setAdapter(dashboardAdapter);
                    dashboard.invalidateViews();
                }else{
                    input.setError("Type something...");
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
                main.removeView(button);
                searchMode = false;
            });

            builder.show();
        }else if(item.getItemId() == R.id.account)
        {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}