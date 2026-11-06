package com.example.tpjsf;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenerateurResponse {

    public String genererReponse(String question, String roleSysteme) {

        if (question == null || question.isBlank()) {
            return "Veuillez saisir une question.";
        }

        return "Rôle système : " + roleSysteme
                + "\n\nVotre question : " + question
                + "\n\nRéponse : Ceci est une réponse de test. "
                + "Votre application Jakarta Faces fonctionne correctement.";
    }
}