package com.example.myapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomePageActivity extends AppCompatActivity {

    // --- UI elements ---
    private MaterialButton btnSos;
    private MaterialCardView cardEmergencyContacts, cardSelfDefence, cardNearbyServices;
    private TextView tvContactsCount;

    // --- SOS Logic ---
    private final Handler sosHandler = new Handler(Looper.getMainLooper());
    private FusedLocationProviderClient fusedLocationClient;
    private ContactStorage contactStorage;
    // This helps prevent showing "SOS Canceled" after a successful trigger
    private final AtomicBoolean sosTriggered = new AtomicBoolean(false);

    // --- Permission Handling ---
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private boolean hasLocationPermission = false;
    private boolean hasSmsPermission = false;

    // The Runnable that contains the core SOS logic
    private final Runnable sosRunnable = this::triggerSosAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        setupClickListeners();

        // Initialize location client and contact storage
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        contactStorage = new ContactStorage(this);

        // Setup the modern way of handling permission requests
        setupPermissionLauncher();
        // Check for permissions as soon as the activity starts
        checkAndRequestPermissions();
    }

    // When the activity resumes (e.g., after adding a contact), update the count
    @Override
    protected void onResume() {
        super.onResume();
        updateContactCount();
    }


    // --- Core SOS and Permission Logic ---

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    hasLocationPermission = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    hasSmsPermission = permissions.getOrDefault(Manifest.permission.SEND_SMS, false);

                    if (!hasLocationPermission) {
                        Toast.makeText(this, "Location permission is required for SOS.", Toast.LENGTH_LONG).show();
                    }
                    if (!hasSmsPermission) {
                        Toast.makeText(this, "SMS permission is required for SOS.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkAndRequestPermissions() {
        hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        hasSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionsToRequest = new ArrayList<>();
        if (!hasLocationPermission) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!hasSmsPermission) {
            permissionsToRequest.add(Manifest.permission.SEND_SMS);
        }

        if (!permissionsToRequest.isEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }

    @SuppressLint("MissingPermission")
    private void triggerSosAlert() {
        // Set the flag to true so we know the alert has been triggered
        sosTriggered.set(true);

        if (!hasLocationPermission || !hasSmsPermission) {
            Toast.makeText(this, "Permissions are required to send SOS alert.", Toast.LENGTH_LONG).show();
            checkAndRequestPermissions(); // Ask again if permissions were denied
            return;
        }

        List<Contact> contacts = contactStorage.loadContacts();
        if (contacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts found. Please add contacts first.", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(HomePageActivity.this, "SOS Alert Triggered! Getting location...", Toast.LENGTH_LONG).show();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        sendAlerts(contacts, location);
                    } else {
                        Toast.makeText(this, "Could not get location. Cannot send alerts.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void sendAlerts(List<Contact> contacts, Location location) {
        String message = "EMERGENCY! This is an automated SOS alert from SafeGuard. I need help. My current location is: "
                + "http://maps.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (Contact contact : contacts) {
                smsManager.sendTextMessage(contact.getPhone(), null, message, null, null);
            }
            Toast.makeText(this, "Alerts sent to all emergency contacts.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS. Please check permissions and network.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // --- Menu and Navigation ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- UI Initialization and Listeners ---

    private void initializeViews() {
        btnSos = findViewById(R.id.btnSos);
        cardEmergencyContacts = findViewById(R.id.cardEmergencyContacts);
        cardSelfDefence = findViewById(R.id.cardSelfDefence);
        cardNearbyServices = findViewById(R.id.cardNearbyServices);
        tvContactsCount = findViewById(R.id.tvContactsCount);
    }

    private void updateContactCount() {
        int count = contactStorage.loadContacts().size();
        tvContactsCount.setText(count + " available");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupClickListeners() {
        // SOS Button 5-Second Hold Logic
        btnSos.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Reset the flag each time the button is pressed
                    sosTriggered.set(false);
                    sosHandler.postDelayed(sosRunnable, 5000); // 5 seconds
                    Toast.makeText(HomePageActivity.this, "Hold for 5 seconds...", Toast.LENGTH_SHORT).show();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Remove the pending timer
                    sosHandler.removeCallbacks(sosRunnable);
                    // ▼▼▼ IMPROVED LOGIC ▼▼▼
                    // Only show "SOS Canceled" if the alert was NOT triggered.
                    if (!sosTriggered.get()) {
                        Toast.makeText(HomePageActivity.this, "SOS Canceled", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        });

        // Card Click Listeners
        cardEmergencyContacts.setOnClickListener(v -> startActivity(new Intent(HomePageActivity.this, EmergencyContactsActivity.class)));
        cardSelfDefence.setOnClickListener(v -> startActivity(new Intent(HomePageActivity.this, SelfDefenceActivity.class)));
        cardNearbyServices.setOnClickListener(v -> startActivity(new Intent(HomePageActivity.this, NearbyServicesActivity.class)));
    }

    // --- Lifecycle Management ---

    @Override
    protected void onStop() {
        super.onStop();
        // Ensure the timer is cancelled if the user leaves the app
        sosHandler.removeCallbacks(sosRunnable);
    }
}
