package com.example.breesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breesapp.R;
import com.example.breesapp.adapters.TopicAdapter;
import com.example.breesapp.models.Topic;

import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    List<Topic> topics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqactivity);

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        topics = initdata();

        RecyclerView recyclerView = findViewById(R.id.topic_view);
        TopicAdapter adapter = new TopicAdapter(topics, getApplicationContext(), new TopicAdapter.OnTopicClickListener() {
            @Override
            public void onTopicClick(Topic state, int position) {
                Intent i = new Intent(getApplicationContext(), FAQ_Element_Activity.class);
                i.putExtra("tittle", state.getTittle());
                i.putExtra("content", state.getContent());
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.search_FAQ);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private List<Topic> initdata(){
        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic("How do I update my profile information?",
                "Go to the «Profile» menu, tap the 'My Account' button next to your details, and make the necessary changes. Don't forget to save your updates."));
        topics.add(new Topic("What happens to my data?", "We strictly follow our privacy policy. All your data is encrypted and used solely to provide services and improve your user experience."));
        topics.add(new Topic("Where should I go if I encounter any problems?", "If you run into any difficulties, contact our support team through the «Help» section inside the app or send an email to support@yourapp.com . We will respond within 24 hours."));
        topics.add(new Topic("How do I update my profile information?",
                "Go to the «Profile» menu, tap the 'My Account' button next to your details, and make the necessary changes. Don't forget to save your updates."));
        topics.add(new Topic("What happens to my data?", "We strictly follow our privacy policy. All your data is encrypted and used solely to provide services and improve your user experience."));
        topics.add(new Topic("Where should I go if I encounter any problems?", "If you run into any difficulties, contact our support team through the «Help» section inside the app or send an email to support@yourapp.com . We will respond within 24 hours."));
        topics.add(new Topic("How do I update my profile information?",
                "Go to the «Profile» menu, tap the 'My Account' button next to your details, and make the necessary changes. Don't forget to save your updates."));
        topics.add(new Topic("What happens to my data?", "We strictly follow our privacy policy. All your data is encrypted and used solely to provide services and improve your user experience."));
        topics.add(new Topic("Where should I go if I encounter any problems?", "If you run into any difficulties, contact our support team through the «Help» section inside the app or send an email to support@yourapp.com . We will respond within 24 hours."));
        return topics;
    }
}