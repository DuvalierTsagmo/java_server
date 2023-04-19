/*
  Titre      : Connexion a une Base de donnee
  Auteur     : Duvalier Tsagmo
  Date       : 22/01/2023
  Description: ce programme vas nous permettre d'effectuer les differentes operations sur la BD
  Version    : 0.0.1
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class BD {
    // Ici sava nous permmettre d'inserer les elements dans la base de donnee
    public void insert(String valeur) {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ESP32", "postgres", "1234");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            String sql = "INSERT INTO temperature (valeur) VALUES (?)";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, valeur);
            pstmt.executeUpdate();
            pstmt.close();

            c.commit();
            System.out.println("Records created successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
    }
}
