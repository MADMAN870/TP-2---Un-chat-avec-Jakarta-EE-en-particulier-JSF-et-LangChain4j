package ma.emsi;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

public class Test1 {

    public static void main(String[] args) {
        // La clé est lue dans une variable d'environnement, jamais écrite dans le code
        String cle = System.getenv("GEMINI_KEY");

        // Création du modèle avec le pattern "builder"
        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        String reponse = modele.chat("Quelle est la capitale de la France ?");
        System.out.println(reponse);
    }
}