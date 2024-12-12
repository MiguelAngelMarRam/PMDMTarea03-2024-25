package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models.Pokemon;

public class PokemonRecyclerViewAdapter extends RecyclerView.Adapter<PokemonRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Pokemon> dataset;
    private final ArrayList<Integer> capturedPokemonIds; // Lista de IDs de Pokémon capturados

    public PokemonRecyclerViewAdapter() {
        dataset = new ArrayList<>();
        capturedPokemonIds = new ArrayList<>();
    }

    // Método para agregar la lista de Pokémon capturados desde Firebase
    public void setCapturedPokemonList(ArrayList<Integer> capturedPokemonIds) {
        this.capturedPokemonIds.clear();
        this.capturedPokemonIds.addAll(capturedPokemonIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokemon_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pokemon pokemon = dataset.get(position);

        holder.nameTextView.setText(pokemon.getName());
        holder.indexTextView.setText(holder.itemView.getContext().getString(R.string.pokemon_index_text) + pokemon.getIndex());
        holder.heightTextView.setText(holder.itemView.getContext().getString(R.string.pokemon_height_text) + pokemon.getHeight() + " dm");
        holder.weightTextView.setText(holder.itemView.getContext().getString(R.string.pokemon_weight_text) + pokemon.getWeight() + " hg");

        // Cargar imagen del Pokémon
        Picasso.get().load(pokemon.getImageUrl()).into(holder.imagenImageView);

        // Asignar iconos de tipos
        holder.typesContainer.removeAllViews(); // Limpiar contenedor de tipos
        if (pokemon.getTypes() != null) { // Validar que los tipos no sean null
            for (String type : pokemon.getTypes()) {
                ImageView typeIcon = new ImageView(holder.itemView.getContext());
                typeIcon.setImageResource(getDrawableForType(type)); // Obtener ícono desde drawable
                holder.typesContainer.addView(typeIcon);
            }
        }

        // Cambiar color de fondo si está capturado
        if (capturedPokemonIds.contains(pokemon.getIndex())) { // Verificar si el ID del Pokémon está en la lista de capturados
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.captured_background)); // Pokémon capturado
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.backgroundColor)); // Pokémon no capturado
        }

        // Detectar clic en un Pokémon
        holder.itemView.setOnClickListener(v -> {
            if (capturedPokemonIds.contains(pokemon.getIndex())) {
                // Mostrar mensaje si el Pokémon ya está capturado
                Toast.makeText(holder.itemView.getContext(), R.string.captured_before, Toast.LENGTH_SHORT).show();
            } else {
                // Guardar el Pokémon capturado en Firebase
                saveCapturedPokemon(pokemon);

                // Agregar el ID del Pokémon capturado a la lista local
                capturedPokemonIds.add(pokemon.getIndex());

                // Cambiar el fondo del Pokémon para indicar que está capturado
                holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.captured_background));

                // Mostrar mensaje de éxito
                Toast.makeText(holder.itemView.getContext(), R.string.captured_pokemon, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaPokemon(ArrayList<Pokemon> listaPokemon) {
        dataset.addAll(listaPokemon);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imagenImageView;
        private final TextView nameTextView;
        private final TextView indexTextView;
        private final TextView heightTextView;
        private final TextView weightTextView;
        private final LinearLayout typesContainer; // Contenedor para los íconos de tipos

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenImageView = itemView.findViewById(R.id.image);
            nameTextView = itemView.findViewById(R.id.name);
            indexTextView = itemView.findViewById(R.id.index);
            heightTextView = itemView.findViewById(R.id.height);
            weightTextView = itemView.findViewById(R.id.weight);
            typesContainer = itemView.findViewById(R.id.types_layout); // Vincula el contenedor
        }
    }

    // Método para guardar el Pokémon capturado en Firebase
    private void saveCapturedPokemon(Pokemon pokemon) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("Firestore", "Usuario desconocido");
            return;
        }

        // Crear el objeto Pokémon capturado
        CapturedPokemon capturedPokemon = new CapturedPokemon(
                pokemon.getName(),
                pokemon.getIndex(),
                pokemon.getHeight(),
                pokemon.getWeight(),
                pokemon.getTypes(),
                pokemon.getImageUrl(),
                true // Este campo indica que el Pokémon ha sido capturado
        );

        // Referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Guardar en la subcolección "pokemon" del usuario
        db.collection("captured_pokemon")
                .document(userId) // Documento del usuario
                .collection("pokemon") // Subcolección de Pokémon capturados
                .document(String.valueOf(pokemon.getIndex())) // Documento identificado por el índice del Pokémon
                .set(capturedPokemon)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "pokemon captured"))
                .addOnFailureListener(e -> Log.e("Firestore", "error saving pokemon", e));
    }

    // Método para obtener el ícono correspondiente a cada tipo
    private int getDrawableForType(String type) {
        if (type == null) {
            return R.drawable.ic_default; // Ícono predeterminado si el tipo es null
        }

        switch (type.toLowerCase()) {
            case "fire":
                return R.drawable.ic_fire;
            case "water":
                return R.drawable.ic_water;
            case "grass":
                return R.drawable.ic_grass;
            case "electric":
                return R.drawable.ic_electric;
            case "ice":
                return R.drawable.ic_ice;
            case "fighting":
                return R.drawable.ic_fighting;
            case "poison":
                return R.drawable.ic_poison;
            case "ground":
                return R.drawable.ic_ground;
            case "flying":
                return R.drawable.ic_flying;
            case "psychic":
                return R.drawable.ic_psychic;
            case "bug":
                return R.drawable.ic_bug;
            case "rock":
                return R.drawable.ic_rock;
            case "ghost":
                return R.drawable.ic_ghost;
            case "dark":
                return R.drawable.ic_dark;
            case "dragon":
                return R.drawable.ic_dragon;
            case "steel":
                return R.drawable.ic_steel;
            case "fairy":
                return R.drawable.ic_fairy;
            default:
                return R.drawable.ic_default; // Ícono predeterminado si el tipo es desconocido
        }
    }
}