package com.example.test;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DirectionsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AutoCompleteTextView startPoint, endPoint;
    private Button getDirectionsBtn;
    private ProgressBar progressBar;
    private Polyline currentPolyline;

    // Firebase reference for campus locations
    private DatabaseReference locationsRef;
    private List<String> campusLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        locationsRef = database.getReference("campus_locations");

        // Setup UI
        startPoint = findViewById(R.id.startPoint);
        endPoint = findViewById(R.id.endPoint);
        getDirectionsBtn = findViewById(R.id.getDirectionsBtn);
        progressBar = findViewById(R.id.progressBar);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Load campus locations from Firebase
        loadCampusLocations();

        // Set up button click listener
        getDirectionsBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                getDirections();
            }
        });

        // Current location button
        findViewById(R.id.currentLocationBtn).setOnClickListener(v -> {
            // Implement location detection here
            Toast.makeText(this, "Fetching current location...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Configure map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Add a marker for campus center and move camera
        LatLng campusCenter = new LatLng(YOUR_CAMPUS_LAT, YOUR_CAMPUS_LNG);
        mMap.addMarker(new MarkerOptions().position(campusCenter).title("Campus Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campusCenter, 15));
    }

    private void loadCampusLocations() {
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                campusLocations.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String locationName = snapshot.getKey();
                    campusLocations.add(locationName);
                }
                setupAutocomplete();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DirectionsActivity.this, "Failed to load locations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAutocomplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, campusLocations);

        startPoint.setAdapter(adapter);
        endPoint.setAdapter(adapter);
    }

    private boolean validateInputs() {
        if (endPoint.getText().toString().trim().isEmpty()) {
            endPoint.setError("Please enter destination");
            return false;
        }
        return true;
    }

    private void getDirections() {
        String destination = endPoint.getText().toString().trim();
        String start = startPoint.getText().toString().trim();

        showLoading(true);

        // TODO: Implement directions logic
        // 1. Get coordinates for start and end points from Firebase
        // 2. Use Google Maps Directions API to get route
        // 3. Draw polyline on map

        // For now, just show a marker at destination
        locationsRef.child(destination).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double lat = dataSnapshot.child("lat").getValue(Double.class);
                    double lng = dataSnapshot.child("lng").getValue(Double.class);

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(destination));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lat, lng), 16));
                } else {
                    Toast.makeText(DirectionsActivity.this,
                            "Location not found", Toast.LENGTH_SHORT).show();
                }
                showLoading(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showLoading(false);
                Toast.makeText(DirectionsActivity.this,
                        "Error fetching location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        getDirectionsBtn.setEnabled(!isLoading);
    }
}