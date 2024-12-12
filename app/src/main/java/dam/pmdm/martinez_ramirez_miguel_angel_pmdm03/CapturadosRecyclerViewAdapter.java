package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CapturadosRecyclerViewAdapter extends RecyclerView.Adapter<CapturadosRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CapturedPokemon> capturedPokemons;
    private boolean eliminateModeEnabled = false; // Variable para controlar el modo de eliminación

    // Constructor
    public CapturadosRecyclerViewAdapter(Context context, ArrayList<CapturedPokemon> capturedPokemons) {
        this.context = context;
        this.capturedPokemons = capturedPokemons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.capturados_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CapturedPokemon pokemon = capturedPokemons.get(position);

        holder.nameTextView.setText(pokemon.getName());

        // Usar los recursos de cadena para el índice, altura y peso
        holder.indexTextView.setText(holder.itemView.getContext().getString(R.string.pokemon_index_text) + pokemon.getIndex());
        holder.heightTextView.setText(holder.itemView.getContext().getString(R.string.pokemon_height_text) + pokemon.getHeight() + " dm");
        holder.weightTextView.setText(holder.itemView.getContext().getString(R.string.pokemon_weight_text) + pokemon.getWeight() + " hg");

        // Cargar imagen del Pokémon
        Picasso.get().load(pokemon.getImageUrl()).into(holder.imagenImageView);

        // Asignar iconos de tipos
        holder.typesContainer.removeAllViews();
        if (pokemon.getTypes() != null) {
            for (String type : pokemon.getTypes()) {
                ImageView typeIcon = new ImageView(context);
                typeIcon.setImageResource(getDrawableForType(type));
                holder.typesContainer.addView(typeIcon);
            }
        }

        // Configurar visibilidad del botón delete_button en función del modo de eliminación
        holder.deleteButton.setVisibility(eliminateModeEnabled ? View.VISIBLE : View.GONE);

        // Configurar clic en el botón delete_button
        holder.deleteButton.setOnClickListener(v -> {
            // Confirmar y eliminar Pokémon
            new AlertDialog.Builder(context)
                    .setTitle(R.string.delete_confirm)
                    .setMessage(context.getString(R.string.confirm_delete_text1) + pokemon.getName() + context.getString(R.string.confirm_delete_text2))
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        String userId = FirebaseAuth.getInstance().getUid();
                        if (userId != null) {
                            FirebaseFirestore.getInstance()
                                    .collection("captured_pokemon")
                                    .document(userId)
                                    .collection("pokemon")
                                    .document(String.valueOf(pokemon.getIndex()))
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        capturedPokemons.remove(position);
                                        notifyItemRemoved(position);
                                        Log.d("Firestore", context.getString(R.string.pokemon_deleted));
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", context.getString(R.string.delete_error), e));
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return capturedPokemons.size();
    }

    // Método para actualizar el modo de eliminación
    public void setEliminateModeEnabled(boolean enabled) {
        this.eliminateModeEnabled = enabled;
        notifyDataSetChanged(); // Notificar cambios en los datos para redibujar las vistas
    }

    // Método para obtener el ícono correspondiente a cada tipo
    private int getDrawableForType(String type) {
        if (type == null) {
            return R.drawable.ic_default;
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
                return R.drawable.ic_default;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imagenImageView;
        private final TextView nameTextView;
        private final TextView indexTextView;
        private final TextView heightTextView;
        private final TextView weightTextView;
        private final LinearLayout typesContainer;
        private final Button deleteButton; // Botón de eliminar

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenImageView = itemView.findViewById(R.id.image);
            nameTextView = itemView.findViewById(R.id.name);
            indexTextView = itemView.findViewById(R.id.index);
            heightTextView = itemView.findViewById(R.id.height);
            weightTextView = itemView.findViewById(R.id.weight);
            typesContainer = itemView.findViewById(R.id.types_layout);
            deleteButton = itemView.findViewById(R.id.delete_button); // Vincula el botón al ViewHolder
        }
    }
}