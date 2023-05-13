import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;

// Diese Klasse ist für die Verbindung zur Datenbank zuständig
public class ImageDatabaseHandler {
    // Hier werden die Daten für die Verbindung zur Datenbank festgelegt
    private static final String DB_URL = "jdbc:mysql://bnfpbzfl49ffy485x4gh-mysql.services.clever-cloud.com:3306/bnfpbzfl49ffy485x4gh?useSSL=true&serverTimezone=UTC";
    private static final String DB_USER = "u3jf91cj3vfi46hw";
    private static final String DB_PASSWORD = "wTiD2blNRmBU0IwrVwEG";

    // Hier wird die Methode zum Speichern der Bilder erstellt

    public static void saveImage(String imageName, String imagePath) {
        String sql = "INSERT INTO imagetable (imagename, image) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            File imageFile = new File(imagePath);
            try (FileInputStream fis = new FileInputStream(imageFile)) {
                preparedStatement.setString(1, imageName);
                preparedStatement.setBinaryStream(2, fis, (int) imageFile.length());
                preparedStatement.executeUpdate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hier wird die Methode zum Abrufen der Bilder erstellt
    public static byte[] retrieveImageData(String imageName) {
        String sql = "SELECT image FROM imagetable WHERE imagename = ?";
        byte[] imageData = null;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, imageName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Blob imageBlob = resultSet.getBlob("image");
                imageData = imageBlob.getBytes(1, (int) imageBlob.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageData;
    }

    // Hier wird die Methode zum Aktualisieren der Bilder erstellt
    public static List<String> getAllImageNames() {
        String sql = "SELECT imagename FROM imagetable";
        List<String> imageNames = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                imageNames.add(resultSet.getString("imagename"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageNames;
    }

    // Hier wird die Methode zum Öffnen der Bilder erstellt
    public static void openImageWithDefaultViewer(String imageName) {
        byte[] imageData = retrieveImageData(imageName);
        if (imageData != null) {
            try {
                File tempFile = File.createTempFile("temp_image_" + imageName, ".tmp");
                tempFile.deleteOnExit();
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
                ImageIO.write(image, "jpg", tempFile);
                Desktop.getDesktop().open(tempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Hier wird die Methode zum Löschen der Bilder erstellt
    public static void deleteImage(String imageName) {
        String sql = "DELETE FROM imagetable WHERE imagename = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, imageName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
