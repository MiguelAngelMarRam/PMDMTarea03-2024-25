package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models.Pokemon;
import dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models.PokemonDetails;
import dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models.PokemonRespuesta;
import dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.pokeapi.PokeapiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokedexFragment extends Fragment {

    private Retrofit retrofit;
    private static final String TAG = String.valueOf(R.string.pokedex);
    private RecyclerView recyclerView;
    private PokemonRecyclerViewAdapter pokemonRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pokemon_list_fragment, container, false);

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.pokemon_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pokemonRecyclerViewAdapter = new PokemonRecyclerViewAdapter();
        recyclerView.setAdapter(pokemonRecyclerViewAdapter);

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("captured_pokemon")
                    .document(userId)
                    .collection("pokemon")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<Integer> capturedIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int pokemonIndex = document.getLong("index").intValue(); // Manejar index como Integer
                                capturedIds.add(pokemonIndex);
                            }
                            pokemonRecyclerViewAdapter.setCapturedPokemonList(capturedIds);
                        } else {
                            Log.e("Firebase", getString(R.string.query_error), task.getException());
                        }
                    });
        } else {
            Log.e("FirebaseAuth", getString(R.string.error_user_unknown));
        }

        // Inicializar Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerDatos();

        return view;
    }

    private void obtenerDatos() {
        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonRespuesta> pokemonRespuestaCall = service.obtenerListaPokemon();

        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                if (response.isSuccessful()) {
                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon> listaPokemon = pokemonRespuesta.getResults();

                    for (Pokemon pokemon : listaPokemon) {
                        // Extraer ID desde la URL
                        String[] urlParts = pokemon.getUrl().split("/");
                        int id = Integer.parseInt(urlParts[urlParts.length - 1]);

                        // Segunda llamada para obtener detalles
                        service.obtenerDetallesPokemon(id).enqueue(new Callback<PokemonDetails>() {
                            @Override
                            public void onResponse(Call<PokemonDetails> call, Response<PokemonDetails> response) {
                                if (response.isSuccessful()) {
                                    PokemonDetails details = response.body();
                                    pokemon.setIndex(details.getId());
                                    pokemon.setWeight(details.getWeight());
                                    pokemon.setHeight(details.getHeight());
                                    pokemon.setImageUrl(details.getSprites().getFrontDefault());

                                    // Obtener tipos
                                    ArrayList<String> tipos = new ArrayList<>();
                                    for (PokemonDetails.TypeEntry typeEntry : details.getTypes()) {
                                        tipos.add(typeEntry.getType().getName());
                                    }
                                    pokemon.setTypes(tipos);

                                    // Notificar al adaptador
                                    pokemonRecyclerViewAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(Call<PokemonDetails> call, Throwable t) {
                                Log.e(TAG, R.string.detail_error + t.getMessage());
                            }
                        });
                    }

                    pokemonRecyclerViewAdapter.adicionarListaPokemon(listaPokemon);
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                Log.e(TAG, R.string.call_error + t.getMessage());
            }
        });
    }
}
