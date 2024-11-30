package com.example.splashscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SavedDataActivity extends AppCompatActivity {

    private FirebaseFirestore db;  // Firestore database instance
    private ListView listViewSavedData;
    private CustomAdapter adapter;
    private ArrayList<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_data);

        // Initialize Firestore and ListView
        db = FirebaseFirestore.getInstance();
        listViewSavedData = findViewById(R.id.listViewSavedData);
        dataList = new ArrayList<>();

        // Setup adapter with custom layout
        adapter = new CustomAdapter(this, dataList);
        listViewSavedData.setAdapter(adapter);

        // Fetch and display saved data from Firestore
        fetchSavedData();
    }

    private void fetchSavedData() {
        db.collection("SplashScreen")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshots = task.getResult();
                        for (QueryDocumentSnapshot document : snapshots) {
                            String data = document.getData().toString();
                            dataList.add(data);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error getting data", Toast.LENGTH_SHORT).show();
                        Log.e("SavedDataActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Custom Adapter class to handle list item view and click actions
    private class CustomAdapter extends ArrayAdapter<String> {
        private final ArrayList<String> dataList;

        public CustomAdapter(SavedDataActivity context, ArrayList<String> data) {
            super(context, 0, data);
            this.dataList = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_with_share, parent, false);
            }

            // Get the current data item
            String data = dataList.get(position);

            // Set the data text
            TextView textViewData = convertView.findViewById(R.id.textViewData);
            textViewData.setText(data);

            // Set the share button action
            Button buttonShare = convertView.findViewById(R.id.buttonShare);
            buttonShare.setOnClickListener(v -> shareData(data));

            return convertView;
        }
    }

    // Function to share data via email or other platforms
    private void shareData(String data) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share data via");
        startActivity(shareIntent);
    }
}
