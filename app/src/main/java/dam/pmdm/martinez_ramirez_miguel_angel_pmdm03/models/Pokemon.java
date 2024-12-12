package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models;

import java.util.ArrayList;

public class Pokemon {
    private String name; // Nombre del Pokémon
    private String url;  // URL para obtener detalles adicionales
    private int index;   // Índice del Pokémon
    private String imageUrl; // URL de la imagen
    private ArrayList<String> types; // Lista de tipos
    private int weight;   // Peso
    private int height;   // Altura

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}