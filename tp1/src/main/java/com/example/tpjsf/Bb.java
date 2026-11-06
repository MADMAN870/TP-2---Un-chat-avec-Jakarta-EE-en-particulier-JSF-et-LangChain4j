
package com.example.tpjsf;

import com.example.tpjsf.llm.JsonAdapterPourGemini;
import com.example.tpjsf.llm.LlmInteraction;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class Bb implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Rôle "système" attribué au LLM.
     */
    private String roleSysteme;

    /**
     * Indique si le rôle système peut encore être modifié.
     */
    private boolean roleSystemeChangeable = true;

    /**
     * Liste des rôles système prédéfinis.
     */
    private List<SelectItem> listeRolesSysteme;

    /**
     * Dernière question posée par l'utilisateur.
     */
    private String question;

    /**
     * Dernière réponse du LLM.
     */
    private String reponse;

    /**
     * Historique de la conversation.
     */
    private StringBuilder conversation = new StringBuilder();

    /**
     * Active ou désactive le mode debug.
     */
    private boolean debug = false;

    /**
     * Requête JSON envoyée au LLM (mode debug).
     */
    private String texteRequeteJson;

    /**
     * Réponse JSON reçue du LLM (mode debug).
     */
    private String texteReponseJson;

    /**
     * Adaptateur JSON pour communiquer avec Gemini.
     */
    @Inject
    private JsonAdapterPourGemini jsonAdapter;

    /**
     * Constructeur sans argument.
     */
    public Bb() {
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

    public String getRoleSysteme() {
        return this.roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return this.roleSystemeChangeable;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return this.reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(
                conversation == null ? "" : conversation
        );
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    // ============================================================
    // ACTION METHODS
    // ============================================================

    /**
     * Envoie la question au serveur Gemini.
     */
    public String envoyer() {

        if (this.question == null || this.question.isBlank()) {

            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Texte question vide",
                    "Il manque le texte de la question"
            );

            FacesContext.getCurrentInstance()
                    .addMessage(null, message);

            return null;
        }

        // Si la conversation n'a pas encore commencé.
        if (this.conversation.isEmpty()) {
            this.roleSystemeChangeable = false;
        }

        try {
            LlmInteraction interaction =
                    jsonAdapter.envoyerRequete(question, this.roleSysteme);

            this.reponse = interaction.reponseExtraite();
            this.texteRequeteJson = interaction.questionJson();
            this.texteReponseJson = interaction.reponseJson();

        } catch (Exception e) {

            this.reponse = "Erreur : " + e.getMessage();

            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM",
                    "Problème de connexion avec l'API du LLM"
                            + e.getMessage() + "\n"
                            + Arrays.toString(e.getStackTrace())
            );

            FacesContext.getCurrentInstance()
                    .addMessage(null, message);
        }

        // Ajoute la question et la réponse à l'historique.
        afficherConversation();

        return null;
    }

    /**
     * Commence une nouvelle conversation.
     */
    public String nouveauChat() {
        return "index?faces-redirect=true";
    }

    /**
     * Affiche l'historique de la conversation.
     */
    private void afficherConversation() {

        this.conversation
                .append("== User:\n")
                .append(question)
                .append("\n== Serveur:\n")
                .append(reponse)
                .append("\n");
    }

    // ============================================================
    // SYSTEM ROLES
    // ============================================================

    public List<SelectItem> getRolesSysteme() {

        if (this.listeRolesSysteme == null) {

            this.listeRolesSysteme = new ArrayList<>();

            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Assistant")
            );

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Traducteur Anglais-Français")
            );

            role = """
                    You are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    and you tell them the average price of a meal.
                    """;

            this.listeRolesSysteme.add(
                    new SelectItem(role, "Guide touristique")
            );
        }

        return this.listeRolesSysteme;
    }
}
