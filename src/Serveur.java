/*
  Titre      : Server java
  Auteur     : Duvalier Tsagmo
  Date       : 12/04/2023
  Description: recois les donnees provenant du client Arduino et les stockes dans la base de donnee
  Version    : 0.0.1
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    // Remplacer "8080" par le port que vous souhaitez utiliser pour la connexion au
    // server
    private static final int PORT = 2222;
    private static String temperature;
    private static String humidite;
    // Création d'une instance de la classe BD qui permet de se connecter à la base
    // de données
    private static BD base = new BD();

    public static void main(String[] args) {

        try {
            // Création d'un objet ServerSocket qui écoute sur le port spécifié
            ServerSocket serveur = new ServerSocket(PORT);
            System.out.println("Le serveur est en attente de connexions sur le port " + PORT);
            // Boucle infinie pour accepter les connexions entrantes en continu
            while (true) {
                // Accepter une connexion entrante et créer un objet Socket pour communiquer
                // avec le client
                Socket socket = serveur.accept();
                System.out.println("Nouvelle connexion entrante");
                // Création d'un objet BufferedReader pour lire les données reçues via le socket
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    // Lire la première ligne du message envoyé par le client
                    String message = in.readLine();
                    System.out.println("Message reçu : " + message);
                    // Extraire la température et l'humidité à partir du message reçu
                    decryptData(message);
                    // Insérer la température dans la base de données
                    base.insert(temperature);
                    // Insérer l'humidité dans la base de données
                    base.insert(humidite);
                    // Envoi de la réponse au client HTML
                    sendResponse(socket);
                } catch (IOException e) {
                    System.err.println("Erreur lors de la lecture du message: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println(
                            "Erreur lors de l'insertion des données dans la base de données: " + e.getMessage());
                } finally {
                    // Fermer le socket de communication avec le client
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la création du serveur : " + e.getMessage());
        }
    }

    private static void decryptData(String data) {
        // Rechercher les positions des caractères ":" et "C" pour extraire la
        // température
        int posTemperature = data.indexOf(":");
        int posC = data.indexOf("C");
        temperature = data.substring(posTemperature + 2, posC).trim();

        // Rechercher les positions des caractères ":" et "%" pour extraire l'humidité
        int posHumidity = data.lastIndexOf(":");
        int posPercent = data.lastIndexOf("%");
        humidite = data.substring(posHumidity + 2, posPercent).trim();

        // affichage de la temperature et l'humidité
        System.out.println("Temp: " + temperature + " C");
        System.out.println("Hum: " + humidite + " %");
    }

    private static void sendResponse(Socket socket) throws IOException {
        // Création d'un objet PrintWriter pour envoyer des données via le socket
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Envoi de l'en-tête de la réponse HTTP
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Connection: close");
        out.println();

        // Envoi de la réponse HTML
        out.println("<html><head><title>Server Java</title></head><body>");
        out.println("<h1>Données reçues</h1>");
        out.println("<p>Température : " + temperature + " C</p>");
        out.println("<p>Humidité : " + humidite + " %</p>");
        out.println("</body></html>");

        // Fermeture du flux PrintWriter
        out.close();
    }

}