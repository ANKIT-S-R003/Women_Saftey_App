package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// ▼▼▼ ADD THIS IMPORT ▼▼▼
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;

public class NearbyServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_services);

        // --- ▼▼▼ THIS IS THE CORRECTED TOOLBAR SETUP ▼▼▼ ---
        // 1. Find the toolbar from the layout
        Toolbar toolbar = findViewById(R.id.toolbarNearby);
        // 2. Set this toolbar as the Activity's action bar
        setSupportActionBar(toolbar);

        // 3. Enable the back arrow (Up button) and set the title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Nearby Services");
        }
        // --- END OF TOOLBAR SETUP ---

        // --- Find the cards by their IDs ---
        MaterialCardView cardFindHospitals = findViewById(R.id.cardFindHospitals);
        MaterialCardView cardFindPolice = findViewById(R.id.cardFindPolice);

        // --- Set click listeners ---
        cardFindHospitals.setOnClickListener(v -> openMapFor("hospitals near me"));
        cardFindPolice.setOnClickListener(v -> openMapFor("police stations near me"));
    }

    /**
     * Creates an Intent to search for a query in an external map application.
     * @param query The search string (e.g., "hospitals near me").
     */
    private void openMapFor(String query) {
        // Create a Uri for the search query
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));

        // Create an Intent with the action to view the Uri
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Set the package to Google Maps to ensure it opens there
        mapIntent.setPackage("com.google.android.apps.maps");

        // Verify that the Google Maps app is installed before starting the intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // If Google Maps is not installed, show a toast message
            Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_LONG).show();
        }
    }

    // --- ▼▼▼ THIS METHOD HANDLES THE BACK ARROW CLICK ▼▼▼ ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Check if the clicked item is the back arrow
        if (item.getItemId() == android.R.id.home) {
            finish(); // Closes this activity and returns to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
