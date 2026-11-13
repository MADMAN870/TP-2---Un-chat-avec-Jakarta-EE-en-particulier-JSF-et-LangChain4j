package ma.emsi;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test3 {

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        // --- Partie 1 : template avec 1 paramètre (le texte) ---
        PromptTemplate template1 = PromptTemplate.from(
                "Traduis le texte suivant en anglais : {{texte}}");

        Prompt prompt1 = template1.apply(Map.of("texte", "Bonjour, comment allez-vous aujourd'hui ?"));
        System.out.println("Prompt 1 : " + prompt1.text());
        System.out.println("Réponse  : " + modele.chat(prompt1.text()));

        // --- Partie 2 : template avec 2 paramètres (la langue et le texte) ---
        PromptTemplate template2 = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}} : {{texte}}");

        Prompt prompt2 = template2.apply(Map.of(
                "langue", "espagnol",
                "texte", "Je voudrais réserver une table pour deux personnes."));
        System.out.println("Prompt 2 : " + prompt2.text());
        System.out.println("Réponse  : " + modele.chat(prompt2.text()));
    }
}