package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CapturadosFragment extends Fragment {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CapturadosRecyclerViewAdapter adapter;
    private ArrayList<CapturedPokemon> capturedPokemons; // Lista global para capturados

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_capturados, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.capturados_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Accedemos a los SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean eliminateModeEnabled = preferences.getBoolean("eliminate_mode", false);

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            db.collection("captured_pokemon")
                    .document(userId)
                    .collection("pokemon")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            capturedPokemons = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CapturedPokemon pokemon = document.toObject(CapturedPokemon.class);
                                capturedPokemons.add(pokemon);
                            }
                            adapter = new CapturadosRecyclerViewAdapter(getContext(), capturedPokemons);
                            recyclerView.setAdapter(adapter);

                            // Configurar el estado inicial del modo eliminar
                            adapter.setEliminateModeEnabled(eliminateModeEnabled);

                            // Observador de cambios en SharedPreferences
                            preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
                                if ("eliminate_mode".equals(key)) {
                                    boolean eliminateMode = sharedPreferences.getBoolean(key, false);
                                    adapter.setEliminateModeEnabled(eliminateMode);
                                }
                            });
                        } else {
                            Log.e("Firebase", getString(R.string.query_error), task.getException());
                        }
                    });
        } else {
            Log.e("FirebaseAuth", getString(R.string.error_user_unknown));
        }
    }
}