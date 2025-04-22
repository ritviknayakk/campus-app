package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView directions = findViewById(R.id.card_directions);
        CardView studySpace = findViewById(R.id.card_study_space);
        CardView sportsFacilities = findViewById(R.id.card_sports);
        CardView feedback = findViewById(R.id.card_feedback);
        CardView campusInfo = findViewById(R.id.card_campus_info);
        CardView lostAndFound = findViewById(R.id.card_lost_found);

        directions.setOnClickListener(v -> openActivity(DirectionsActivity.class));
        studySpace.setOnClickListener(v -> openActivity(StudySpaceActivity.class));
        sportsFacilities.setOnClickListener(v -> openActivity(SportsActivity.class));
        feedback.setOnClickListener(v -> openActivity(FeedbackActivity.class));
        campusInfo.setOnClickListener(v -> openActivity(CampusInfoActivity.class));
        lostAndFound.setOnClickListener(v -> openActivity(LostAndFoundActivity.class));
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
