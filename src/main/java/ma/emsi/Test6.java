
package ma.emsi;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Scanner;

public class Test6 {

    interface Assistant {
        String chat(String question);
    }

    public static void main(String[] args) {

        System.out.println("[1] Début du programme");

        String cle = System.getenv("GEMINI_KEY");

        if (cle == null || cle.isBlank()) {
            throw new IllegalStateException(
                    "La variable d'environnement GEMINI_KEY n'est pas définie."
            );
        }

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .temperature(0.3)
                .build();

        GoogleGenAiEmbeddingModel modeleEmbedding =
                GoogleGenAiEmbeddingModel.builder()
                        .apiKey(cle)
                        .modelName("gemini-embedding-001")
                        .taskType(
                                GoogleGenAiEmbeddingModel.TaskTypeEnum.RETRIEVAL_DOCUMENT
                        )
                        .timeout(Duration.ofSeconds(30))
                        .build();

        System.out.println("[2] Chargement du PDF...");

        Document document = FileSystemDocumentLoader.loadDocument(
                Path.of("langchain4j.pdf")
        );

        System.out.println("[3] PDF chargé");

        InMemoryEmbeddingStore<TextSegment> magasin =
                new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(modeleEmbedding)
                .embeddingStore(magasin)
                .build();

        System.out.println("[4] Début de l'indexation du PDF...");

        ingestor.ingest(document);

        System.out.println("[5] Indexation terminée");

        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(magasin)
                .embeddingModel(modeleEmbedding)
                .build();

        System.out.println("[6] Retriever créé");

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(modele)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(retriever)
                .build();

        System.out.println("[7] Assistant initialisé");

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {

                System.out.println("==================================================");
                System.out.println("Posez votre question (ou tapez 'fin') : ");

                String question = scanner.nextLine();

                if (question.isBlank()) {
                    continue;
                }

                if ("fin".equalsIgnoreCase(question.trim())) {
                    System.out.println("Fin de la conversation.");
                    break;
                }

                System.out.println("==================================================");
                System.out.println("Assistant : " + assistant.chat(question));
            }
        }
    }
}
