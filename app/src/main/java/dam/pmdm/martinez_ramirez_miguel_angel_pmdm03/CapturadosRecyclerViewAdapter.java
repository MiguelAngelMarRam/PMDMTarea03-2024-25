package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CapturadosRecyclerViewAdapter extends RecyclerView.Adapter<CapturadosRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CapturedPokemon> capturedPokemons;
    private OnItemClickListener onItemClickListener;

    // Constructor
    public CapturadosRecyclerViewAdapter(Context context, ArrayList<CapturedPokemon> capturedPokemons) {
        this.context = context;
        this.capturedPokemons = capturedPokemons;
    }

    // Método para configurar el listener de clic
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pokemon_cardview, parent, false);
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

        // Detectar clic en un Pokémon y manejar el clic
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(pokemon, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return capturedPokemons.size();
    }

    // Método para obtener el ícono correspondiente a cada tipo
    private int getDrawableForType(String type) {
        if (type == null) {
            return R.drawable.ic_default;
        }

        switch (type.toLowerCase()) {
            case "fire": return R.drawable.ic_fire;
            case "water": return R.drawable.ic_water;
            case "grass": return R.drawable.ic_grass;
            case "electric": return R.drawable.ic_electric;
            case "ice": return R.drawable.ic_ice;
            case "fighting": return R.drawable.ic_fighting;
            case "poison": return R.drawable.ic_poison;
            case "ground": return R.drawable.ic_ground;
            case "flying": return R.drawable.ic_flying;
            case "psychic": return R.drawable.ic_psychic;
            case "bug": return R.drawable.ic_bug;
            case "rock": return R.drawable.ic_rock;
            case "ghost": return R.drawable.ic_ghost;
            case "dark": return R.drawable.ic_dark;
            case "dragon": return R.drawable.ic_dragon;
            case "steel": return R.drawable.ic_steel;
            case "fairy": return R.drawable.ic_fairy;
            default: return R.drawable.ic_default;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imagenImageView;
        private final TextView nameTextView;
        private final TextView indexTextView;
        private final TextView heightTextView;
        private final TextView weightTextView;
        private final LinearLayout typesContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenImageView = itemView.findViewById(R.id.image);
            nameTextView = itemView.findViewById(R.id.name);
            indexTextView = itemView.findViewById(R.id.index);
            heightTextView = itemView.findViewById(R.id.height);
            weightTextView = itemView.findViewById(R.id.weight);
            typesContainer = itemView.findViewById(R.id.types_layout);
        }
    }

    // Interfaz para el callback del clic en ítem
    public interface OnItemClickListener {
        void onItemClick(CapturedPokemon pokemon, ViewHolder holder);
    }
}
