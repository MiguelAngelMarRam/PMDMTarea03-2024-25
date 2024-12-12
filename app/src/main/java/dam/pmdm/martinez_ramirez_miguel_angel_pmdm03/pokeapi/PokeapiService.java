package dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.pokeapi;

import dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models.PokemonDetails;
import dam.pmdm.martinez_ramirez_miguel_angel_pmdm03.models.PokemonRespuesta;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PokeapiService {

    @GET("pokemon?offset=0&limit=150")
    Call<PokemonRespuesta> obtenerListaPokemon();
    @GET("pokemon/{id}")
    Call<PokemonDetails> obtenerDetallesPokemon(@Path("id") int id);

}
