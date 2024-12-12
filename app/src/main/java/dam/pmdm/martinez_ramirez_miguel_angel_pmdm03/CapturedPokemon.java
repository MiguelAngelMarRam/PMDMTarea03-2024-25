package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03;

import java.util.List;

public class CapturedPokemon {
    private String name;
    private int index;
    private int height;
    private int weight;
    private List<String> types;
    private String imageUrl;
    private boolean captured; // Campo para indicar si el Pokémon ha sido capturado

    // Constructor completo
    public CapturedPokemon(String name, int index, int height, int weight, List<String> types, String imageUrl, boolean captured) {
        this.name = name;
        this.index = index;
        this.height = height;
        this.weight = weight;
        this.types = types;
        this.imageUrl = imageUrl;
        this.captured = captured;
    }

    // Constructor vacío (necesario para Firebase)
    public CapturedPokemon() {
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}
