package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener {

    private Button addCityButton;
    private RecyclerView cityRecyclerView;

    private ArrayList<City> cityArrayList;
    private CityAdapter cityAdapter;

    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set views
        addCityButton = findViewById(R.id.buttonAddCity);
        cityRecyclerView = findViewById(R.id.listviewCities);

        // create city array
        cityArrayList = new ArrayList<>();
        cityAdapter = new CityAdapter(cityArrayList, position -> {
            City city = cityArrayList.get(position);
            CityDialogFragment dialog =
                    CityDialogFragment.newInstance(city);
            dialog.show(getSupportFragmentManager(), "City Details");
        });

        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cityRecyclerView.setAdapter(cityAdapter);

        // enable swipe to delete
        ItemTouchHelper.SimpleCallback swipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int pos = viewHolder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            City removed = cityArrayList.remove(pos);
                            cityAdapter.notifyItemRemoved(pos);

                            // remove from Firestore
                            if (removed != null && removed.getName() != null) {
                                citiesRef.document(removed.getName()).delete()
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully deleted!"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document", e));
                            }
                        }
                    }
                };
        new ItemTouchHelper(swipeCallback).attachToRecyclerView(cityRecyclerView);

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
            }
            if (value != null && !value.isEmpty()) {
                cityArrayList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    String name = snapshot.getString("name");
                    String province = snapshot.getString("province");

                    cityArrayList.add(new City(name, province));
                }
                cityAdapter.notifyDataSetChanged();
            }
        });

        // set listeners
        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(), "Add City");
        });
    }

    @Override
    public void updateCity(City city, String title, String year) {
        String oldName = city.getName();
        city.setName(title);
        city.setProvince(year);

        int idx = cityArrayList.indexOf(city);
        if (idx != -1) {
            cityAdapter.notifyItemChanged(idx);
        } else {
            cityAdapter.notifyDataSetChanged();
        }

        if (!oldName.equals(title)) {
            citiesRef.document(oldName).delete();
        }
        citiesRef.document(city.getName()).set(city).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore", "DocumentSnapshot successfully written!");
            }
        });
    }

    @Override
    public void addCity(City city){
        cityArrayList.add(city);
        cityAdapter.notifyItemInserted(cityArrayList.size() - 1);

        DocumentReference docRef = citiesRef.document(city.getName());
        docRef.set(city).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore", "DocumentSnapshot successfully written!");
            }
        });
    }
}