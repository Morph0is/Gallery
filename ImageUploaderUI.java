import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageUploaderUI extends JFrame {
    private JLabel imagePathLabel;
    private JTextField imagePathTextField;
    private JButton chooseImageButton;
    private JLabel descriptionLabel;
    private JTextArea descriptionTextArea;
    private JButton uploadButton;

    public ImageUploaderUI() {
        setTitle("Bild hochladen");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout-Manager festlegen
        setLayout(new BorderLayout());

        // Komponenten initialisieren
        imagePathLabel = new JLabel("Bildpfad:");
        imagePathTextField = new JTextField(20);
        chooseImageButton = new JButton("Auswählen");
        chooseImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseImage();
            }
        });

        descriptionLabel = new JLabel("Beschreibung:");
        descriptionTextArea = new JTextArea(3, 20);

        uploadButton = new JButton("Hochladen");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String imagePath = imagePathTextField.getText();
                String description = descriptionTextArea.getText();
                // Hier wird der Code zum Hochladen des Bildes in die Datenbank aufgerufen
            }
        });

        // Komponenten zum Fenster hinzufügen
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        inputPanel.add(imagePathLabel);
        inputPanel.add(imagePathTextField);
        inputPanel.add(chooseImageButton);
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionTextArea);

        add(inputPanel, BorderLayout.CENTER);
        add(uploadButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePathTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        new ImageUploaderUI();
    }
}
