
package com.example.tpjsf.llm;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

@Dependent
public class LlmClientPourGemini implements Serializable {

    private static final long serialVersionUID = 1L;

    // API key
    private final String key;

    // REST client
    private Client clientRest;

    // Gemini endpoint
    private final WebTarget target;

    public LlmClientPourGemini() {

        // Retrieve the API key from the environment
        this.key = System.getenv("GEMINI_KEY");

        if (this.key == null || this.key.isBlank()) {
            throw new IllegalStateException(
                    "Environment variable GEMINI_KEY is missing"
            );
        }

        this.clientRest = ClientBuilder.newBuilder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        this.target = clientRest.target(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"
        );
    }

    /**
     * Sends a JSON request to Gemini.
     */
    public Response envoyerRequete(Entity requestEntity) {

        Invocation.Builder request =
                target.request(MediaType.APPLICATION_JSON_TYPE);

        request.header("x-goog-api-key", key);

        return request.post(requestEntity);
    }

    @PreDestroy
    public void closeClient() {
        if (this.clientRest != null) {
            this.clientRest.close();
        }
    }
}
