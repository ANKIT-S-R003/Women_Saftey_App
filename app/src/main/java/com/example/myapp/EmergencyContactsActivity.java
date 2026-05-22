package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

// The class declaration remains the same
public class EmergencyContactsActivity extends AppCompatActivity implements ContactAdapter.OnDeleteClickListener, ContactAdapter.OnItemClickListener {

    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private ContactStorage contactStorage; // ▼ ADD: The storage helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        // Initialize the storage helper
        contactStorage = new ContactStorage(this);

        Toolbar toolbar = findViewById(R.id.toolbarContacts);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Emergency Contacts");
        }

        setupRecyclerView();

        FloatingActionButton fabAddContact = findViewById(R.id.fabAddContact);
        fabAddContact.setOnClickListener(v -> showAddContactDialog());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));

        // ▼ UPDATE: Load contacts from storage instead of creating a new list
        contactList = contactStorage.loadContacts();

        contactAdapter = new ContactAdapter(contactList, this, this);
        recyclerViewContacts.setAdapter(contactAdapter);
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_contact, null);
        builder.setView(dialogView);

        final EditText etContactName = dialogView.findViewById(R.id.etContactName);
        final EditText etContactPhone = dialogView.findViewById(R.id.etContactPhone);

        builder.setTitle("Add New Contact")
                .setPositiveButton("Add", (dialog, id) -> {
                    String name = etContactName.getText().toString().trim();
                    String phone = etContactPhone.getText().toString().trim();
                    if (!name.isEmpty() && !phone.isEmpty()) {
                        contactList.add(new Contact(name, phone));
                        contactAdapter.notifyItemInserted(contactList.size() - 1);
                        contactStorage.saveContacts(contactList); // ▼ ADD: Save the updated list
                        Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Name and phone cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDeleteClick(int position) {
        contactList.remove(position);
        contactAdapter.notifyItemRemoved(position);
        contactStorage.saveContacts(contactList); // ▼ ADD: Save the updated list
        Toast.makeText(this, "Contact removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Contact contact) {
        // This functionality is correct and remains unchanged
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contact.getPhone()));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
