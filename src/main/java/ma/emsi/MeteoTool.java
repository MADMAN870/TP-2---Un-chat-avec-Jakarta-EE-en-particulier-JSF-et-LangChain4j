package ma.emsi;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MeteoTool {

    @Tool("Donne les probabilités maximales de pluie (en pourcentage) pour chaque jour à venir, "
            + "jour courant compris, à un endroit défini par sa latitude et sa longitude. "
            + "Le résultat contient les dates et les probabilités.")
    public String previsionsPluie(
            @P("Latitude du lieu, par exemple 48.85 pour Paris") double latitude,
            @P("Longitude du lieu, par exemple 2.35 pour Paris") double longitude,
            @P("Nombre de jours de prévision, de 1 à 16, jour courant compris") int nombreJours) {

        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&daily=precipitation_probability_max"
                + "&forecast_days=" + nombreJours;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest requete = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> reponse = client.send(requete, HttpResponse.BodyHandlers.ofString());
            return reponse.body();
        } catch (Exception e) {
            return "Erreur lors de l'appel à l'API météo : " + e.getMessage();
        }
    }
}