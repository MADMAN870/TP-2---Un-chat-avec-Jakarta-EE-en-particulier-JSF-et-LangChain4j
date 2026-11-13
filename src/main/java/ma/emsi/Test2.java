package ma.emsi;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;

public class Test2{

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        // Cette version de chat() retourne un ChatResponse, pas juste un texte
        ChatResponse reponse = modele.chat(UserMessage.from("Quelle est la capitale de la France ?"));

        System.out.println(reponse.aiMessage().text());

        TokenUsage usage = reponse.tokenUsage();
        System.out.println("Tokens d'entrée : " + usage.inputTokenCount());
        System.out.println("Tokens de sortie : " + usage.outputTokenCount());
        System.out.println("Total : " + usage.totalTokenCount());
    }
}