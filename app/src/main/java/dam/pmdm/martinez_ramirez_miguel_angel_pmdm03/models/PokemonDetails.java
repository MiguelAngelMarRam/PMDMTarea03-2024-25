package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models;

import java.util.ArrayList;

public class PokemonDetails {
    private int id;
    private int weight;
    private int height;
    private ArrayList<TypeEntry> types;
    private Sprites sprites;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ArrayList<TypeEntry> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<TypeEntry> types) {
        this.types = types;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    public static class TypeEntry {
        private Type type;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

    public static class Type {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Sprites {
        private String front_default;

        public String getFrontDefault() {
            return front_default;
        }
    }
}

