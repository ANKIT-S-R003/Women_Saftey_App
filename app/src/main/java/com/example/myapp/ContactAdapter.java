// In ContactAdapter.java

package com.example.myapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<Contact> contactList;
    private final OnDeleteClickListener deleteClickListener;
    private final OnItemClickListener itemClickListener; // ▼ ADD THIS LINE

    // ▼ ADD THIS NEW INTERFACE ▼
    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    // ▼ MODIFY THE CONSTRUCTOR ▼
    public ContactAdapter(List<Contact> contactList, OnDeleteClickListener deleteListener, OnItemClickListener itemListener) {
        this.contactList = contactList;
        this.deleteClickListener = deleteListener;
        this.itemClickListener = itemListener; // ▼ ADD THIS LINE
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvContactName.setText(contact.getName());
        holder.tvContactPhone.setText(contact.getPhone());

        holder.btnDeleteContact.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(holder.getAdapterPosition());
            }
        });

        // ▼ ADD THIS BLOCK to handle clicks on the entire card ▼
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(contactList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvContactName;
        TextView tvContactPhone;
        ImageButton btnDeleteContact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactPhone = itemView.findViewById(R.id.tvContactPhone);
            btnDeleteContact = itemView.findViewById(R.id.btnDeleteContact);
        }
    }
}
