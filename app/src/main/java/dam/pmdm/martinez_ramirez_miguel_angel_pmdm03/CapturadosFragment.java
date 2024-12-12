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
    private SwitchPreferenceCompat eliminateModePref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_capturados, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.capturados_recyclerview);  // Asegúrate de tener un RecyclerView en tu layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtener el Switch desde el archivo de preferencias
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
                            // Aquí se pasa el Context además de la lista de Pokémon
                            adapter = new CapturadosRecyclerViewAdapter(getContext(), capturedPokemons);
                            recyclerView.setAdapter(adapter);

                            // Configurar el listener de clic
                            adapter.setOnItemClickListener((pokemon, holder) -> {
                                deletePokemon(pokemon, holder, eliminateModeEnabled);
                            });

                        } else {
                            Log.e("Firebase", getString(R.string.query_error), task.getException());
                        }
                    });
        } else {
            Log.e("FirebaseAuth", getString(R.string.error_user_unknown));
        }
    }

    private void deletePokemon(CapturedPokemon pokemon, CapturadosRecyclerViewAdapter.ViewHolder holder, boolean eliminateModeEnabled) {
        if (eliminateModeEnabled) {
            // Crear un AlertDialog para confirmar la eliminación
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_confirm)
                    .setMessage(getString(R.string.confirm_delete_text1) + pokemon.getName() + getString(R.string.confirm_delete_text2))
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        // Si el usuario confirma, eliminar el Pokémon
                        String userId = FirebaseAuth.getInstance().getUid();
                        if (userId == null) {
                            Log.e("Firestore", getString(R.string.error_user_unknown));
                            return;
                        }

                        // Eliminar el documento del Pokémon usando su índice
                        db.collection("captured_pokemon")
                                .document(userId) // Documento del usuario
                                .collection("pokemon") // Subcolección de Pokémon capturados
                                .document(String.valueOf(pokemon.getIndex())) // Documento identificado por el índice del Pokémon
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", getString(R.string.pokemon_deleted));
                                    // Eliminar de la lista local y actualizar el RecyclerView
                                    int position = capturedPokemons.indexOf(pokemon);
                                    capturedPokemons.remove(pokemon);
                                    adapter.notifyItemRemoved(position);  // Notificar que el ítem fue eliminado
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", getString(R.string.delete_error), e));
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> {
                        // Si el usuario cancela, no hacer nada
                        dialog.dismiss();
                    })
                    .show();
        } else {
            // Si el modo de eliminación no está activado, mostrar un Toast informando al usuario
            Toast.makeText(getContext(), R.string.delete_blocked, Toast.LENGTH_SHORT).show();
        }
    }
}