package com.example.posturalassessment;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FireBaseDatabaseHelper {
    FirebaseDatabase mDatabase;
    DatabaseReference mReference;
    private ArrayList<LoadCell> LoadCells = new ArrayList<>();

    public ArrayList<LoadCell> getLoadCells() {
        return LoadCells;
    }

    public interface DataStatus{
        void DataIsLoaded(ArrayList<LoadCell> loadcells, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();

    }

    public FireBaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("ESP32_Device");

    }

    // Method to received Load Cell data from Firebase
    public void readLoadCells(final DataStatus dataStatus){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> keys = new ArrayList<>();

                // Access each info node
                for(DataSnapshot keyNode: snapshot.getChildren()){
                    keys.add(keyNode.getKey()); // Time000, ...
                    // Create LoadCell object with Firebase information for this node
                    LoadCell loadcell = null;
                    try {
                        loadcell = new LoadCell(keyNode.child("LB").getValue(double.class),
                                keyNode.child("RB").getValue(double.class),
                                keyNode.child("t").getValue(String.class),
                                keyNode.child("F").getValue(double.class));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    LoadCells.add(loadcell);


                }
                dataStatus.DataIsLoaded(LoadCells, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
