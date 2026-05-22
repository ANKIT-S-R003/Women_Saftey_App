package com.example.myapp;

import android.content.Context;
import android.content.SharedPreferences;

// These are the correct imports
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ContactStorage {
    private static final String PREFS_NAME = "EmergencyContactsPrefs";
    private static final String CONTACTS_KEY = "contacts";
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public ContactStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveContacts(List<Contact> contactList) {
        String jsonContacts = gson.toJson(contactList);
        sharedPreferences.edit().putString(CONTACTS_KEY, jsonContacts).apply();
    }

    public List<Contact> loadContacts() {
        String jsonContacts = sharedPreferences.getString(CONTACTS_KEY, null);
        if (jsonContacts == null) {
            // If no contacts are saved, it's correct to return a new empty list.
            return new ArrayList<>();
        }

        // ▼ THIS IS THE CORRECTED LINE ▼
        // It uses the TypeToken directly from the 'com.google.gson.reflect' import.
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();

        // This will now work as expected.
        return gson.fromJson(jsonContacts, type);
    }
}
