import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class BildInMySQL_Einfuegen {

    public BildInMySQL_Einfuegen(String imagePath, String imageName, String description) {
        String driverName = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/imagedb";
        String userName = "root";
        String password = ""; // Setzen Sie hier Ihr Passwort ein.

        try {
            Class.forName(driverName);
            Connection con = DriverManager.getConnection(url, userName, password);

            String sql = "INSERT INTO imagetable (imagename, image) values (?, ?)";
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, imageName); // Verwenden Sie imageName anstelle von name

            File imgFile = new File(imagePath);
            FileInputStream fis = new FileInputStream(imgFile);
            pre.setBinaryStream(2, (InputStream) fis, (int) imgFile.length());

            pre.executeUpdate();

            System.out.println("Das Bild wurde erfolgreich in die Datenbank eingefügt.");

            pre.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
