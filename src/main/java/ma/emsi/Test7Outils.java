package ma.emsi;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;

public class Test7Outils {

    interface Assistant {
        String chat(String question);
    }

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.3)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(modele)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new MeteoTool())   // le LLM pourra appeler cet outil quand il en a besoin
                .build();

        String question = "Je dois partir dans 2 jours à Paris ; j'y resterai 3 jours. "
                + "Est-ce que je dois mettre un parapluie dans mes valises ?";
        System.out.println(assistant.chat(question));
    }
}