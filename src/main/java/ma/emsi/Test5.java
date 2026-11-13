package ma.emsi;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;

public class Test5 {

    // Interface implémentée automatiquement par LangChain4j (service IA)
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

        // 1. Chargement du fichier texte
        Document document = FileSystemDocumentLoader.loadDocument(Path.of("infos.txt"));

        // 2. Ingestion : découpage en segments + embeddings, rangés dans une base en mémoire
        InMemoryEmbeddingStore<TextSegment> magasin = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(java.util.List.of(document), magasin);

        // 3. Retriever : retrouve les segments les plus proches de la question
        ContentRetriever retriever = EmbeddingStoreContentRetriever.from(magasin);

        // 4. Assistant avec mémoire (10 messages) et RAG
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(modele)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(retriever)
                .build();

        System.out.println(assistant.chat("Comment s'appelle le chat de Pierre ?"));
    }
}