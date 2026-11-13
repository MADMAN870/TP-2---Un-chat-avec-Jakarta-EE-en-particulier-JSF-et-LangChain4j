package ma.emsi;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

public class Test4 {

    // Au moins 6 couples de phrases : similaires, opposées, sans rapport...
    static final String[][] COUPLES = {
            {"Le chat dort sur le canapé.", "Un chat fait la sieste sur le sofa."},
            {"J'adore ce film.", "Je déteste ce film."},
            {"Il fait très beau aujourd'hui.", "Le soleil brille et le ciel est dégagé."},
            {"Paris est la capitale de la France.", "Je vais préparer un gâteau au chocolat."},
            {"Le train part à huit heures.", "Le départ du train est prévu à 8 h."},
            {"Mon ordinateur est en panne.", "La mer est calme ce matin."}
    };

    public static void main(String[] args) {

        // --- Modèle 1 : modèle intégré, gratuit (entraîné surtout sur de l'anglais) ---
        EmbeddingModel modeleIntegre = new BgeSmallEnV15EmbeddingModel();
        System.out.println("=== Modèle intégré (BgeSmallEnV15) ===");
        comparer(modeleIntegre);

        // --- Modèle 2 : modèle externe de Google (meilleur en français) ---
        String cle = System.getenv("GEMINI_KEY");
        EmbeddingModel modeleGoogle = GoogleGenAiEmbeddingModel.builder()
                .apiKey(cle)
                .modelName("gemini-embedding-001")
                .taskType(GoogleGenAiEmbeddingModel.TaskTypeEnum.SEMANTIC_SIMILARITY)
                .timeout(Duration.ofSeconds(10))   // à augmenter si ça ne suffit pas
                .build();
        System.out.println("\n=== Modèle Google (gemini-embedding-001) ===");
        comparer(modeleGoogle);
    }

    static void comparer(EmbeddingModel modele) {
        for (String[] couple : COUPLES) {
            Embedding e1 = modele.embed(couple[0]).content();
            Embedding e2 = modele.embed(couple[1]).content();
            double similarite = CosineSimilarity.between(e1, e2);
            System.out.printf("%.4f  |  %s  <->  %s%n", similarite, couple[0], couple[1]);
        }
    }
}