package com.example.dweeter;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class DashboardAdapter extends BaseAdapter {
        Context context;
        ArrayList<TweetModel> tweets;
        LayoutInflater inflater;
        int teal;
        int defaultColor;
        public DashboardAdapter(Context applicationContext, ArrayList<TweetModel> tweets) {
            this.context = applicationContext;
            this.tweets = tweets;
            inflater = (LayoutInflater.from(applicationContext));
            this.teal = context.getResources().getColor(R.color.teal_200);
            this.defaultColor = context.getResources().getColor(R.color.gray);
        }

        @Override
        public int getCount() {
            return tweets.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


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
            view = inflater.inflate(R.layout.tweet, null);
            TweetModel tweet = tweets.get(i);
            TextView nick = view.findViewById(R.id.account_nick);
            ImageView image = view.findViewById(R.id.account_icon);
            TextView message = view.findViewById(R.id.message);
            TextView likesCount = view.findViewById(R.id.likes_count);

            String like_counts = String.valueOf(tweet.likesCount);
            likesCount.setText(like_counts);

            //TODO IMAGE

            ImageButton like = view.findViewById(R.id.like);
            ImageButton comment = view.findViewById(R.id.comment);
            ImageButton reply = view.findViewById(R.id.reply);

            Person finalPerson = person;
            db.child("likes").child(tweet.path).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean liked = false;
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        Person p = snapshot1.getValue(Person.class);
                        if(logdin && Objects.equals(p.uid, finalPerson.uid))
                        {
                            liked = true;
                        }
                    }
                    if(liked) {
                        like.setBackgroundColor(teal);
                    }
                    else {
                        like.setBackgroundColor(defaultColor);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            like.setOnClickListener(view1 -> {

                if(logdin)
                {
                    db.child("likes").child(tweet.path).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean liked = false;
                            ArrayList<Person> peopleWhoLiked = new ArrayList<>();
                            for (DataSnapshot snapshot1: snapshot.getChildren()) {
                                Person p = snapshot1.getValue(Person.class);
                                if(Objects.equals(p.uid, finalPerson.uid))
                                {
                                    liked = true;
                                }else{
                                    peopleWhoLiked.add(p);
                                }

                            }
                            int likes = tweet.likesCount;
                            if(!liked) {
                                like.setBackgroundColor(teal);
                                likes++;
                                peopleWhoLiked.add(finalPerson);
                            }
                            else {
                                like.setBackgroundColor(defaultColor);
                                likes--;
                                peopleWhoLiked.remove(finalPerson);
                            }
                            db.child("tweets").child(tweet.path).child("likesCount").setValue(likes);
                            db.child("likes").child(tweet.path).setValue(peopleWhoLiked);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(context, "You must login first!", Toast.LENGTH_SHORT).show();
                }
            });

            comment.setOnClickListener(view1 -> {
                Intent intent = new Intent(context, CommentSectionActivity.class);
                intent.putExtra("tweet", tweet);
                context.startActivity(intent);
            });


            reply.setOnClickListener((v)->{
                if(!logdin) {Toast.makeText(context, "You must login first!", Toast.LENGTH_SHORT).show(); return;}
                Intent intent = new Intent(context, CreateTweet.class);
                intent.putExtra("linkTo", tweet.path);
                context.startActivity(intent);
            });


            nick.setText(tweet.accountNick);
            message.setText(tweet.message);
            Picasso.get()
                    .load(tweet.accountUrl)
                    .resize(48, 48).into(image);

            if(!Objects.equals(tweet.linkTo, "")) 
            {

                View finalView = view;
                db.child("tweets").child(tweet.linkTo).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TweetModel tw = snapshot.getValue(TweetModel.class);
                        LinearLayout replyTweet = finalView.findViewById(R.id.reply_tweet);
                        int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());
                        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(dimensionInDp,dimensionInDp);
                        ImageView replyIcon = new ImageView(context);
                        replyIcon.setLayoutParams(parms);
                        Picasso.get()
                                .load(tw.accountUrl)
                                .resize(32, 32).into(replyIcon);

                        replyTweet.addView(replyIcon);

                        LinearLayout replyContainer = new LinearLayout(context);
                        LinearLayout.LayoutParams replyCointainerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        replyContainer.setLayoutParams(replyCointainerParams);
                        replyContainer.setOrientation(LinearLayout.VERTICAL);

                        TextView replyNick = new TextView(context);
                        replyNick.setLayoutParams(replyCointainerParams);
                        replyNick.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        replyNick.setText(tw.accountNick);

                        replyContainer.addView(replyNick);

                        TextView replyMessage = new TextView(context);
                        replyMessage.setLayoutParams(replyCointainerParams);
                        replyMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                        replyMessage.setText(tw.message);

                        replyContainer.addView(replyMessage);

                        replyTweet.addView(replyContainer);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            return view;
        }

}
