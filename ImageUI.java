import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class ImageUI {
    private JFrame frame;   //An dieser Stelle wird das Design der GUI erstellt und die einzelnen Buttons und Funktionen werden definiert
    private JButton btnSaveImage;
    private JButton btnRefreshList;
    private JFileChooser fileChooser;
    private JList<ImageIcon> lstImageNames;
    private JLabel lblImage;
    private JButton btnDeleteImage;
    private List<String> imageNames;




    public ImageUI() // Hier wird die GUI erstellt
    {
        // Hier wird der Titel der GUI festgelegt
        frame = new JFrame("Gallery");
        // Hier wird die Größe der GUI festgelegt
        frame.setSize(1000, 600);
        // Hier wird festgelegt, dass die GUI geschlossen werden kann
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Hier wird der Container erstellt
        Container contentPane = frame.getContentPane();
        // Hier wird das Layout des Containers festgelegt
        contentPane.setLayout(new BorderLayout());

        // Hier wird der FileChooser erstellt
        fileChooser = new JFileChooser();

        // Hier wird der Button erstellt
        btnSaveImage = new JButton("Bild Hinzufügen");
        // Hier wird die Aktion des Buttons festgelegt
        btnSaveImage.addActionListener(new ActionListener()
        {
            // Hier wird die Aktion des Buttons festgelegt
            public void actionPerformed(ActionEvent e)
            {
                // Hier wird festgelegt, dass der FileChooser geöffnet wird
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String imageName = selectedFile.getName();
                    ImageDatabaseHandler.saveImage(imageName, selectedFile.getAbsolutePath());
                    refreshImageList();
                    JOptionPane.showMessageDialog(frame, "Bild erfolgreich gespeichert!");
                }
            }
        });
        // Hier wird der Button erstellt
        btnRefreshList = new JButton("Liste aktualisieren"); // Hier wird der Button erstellt
        // Hier wird die Aktion des Buttons festgelegt
        btnRefreshList.addActionListener(new ActionListener()
        {
            // Hier wird die Aktion des Buttons festgelegt
            public void actionPerformed(ActionEvent e) {
                refreshImageList();
            }
        });
        // Hier wird der Button erstellt
        btnDeleteImage = new JButton("Bild löschen");
        // Hier wird der Button erstellt
        lblImage = new JLabel();
        // Hier wird die Aktion des Buttons festgelegt
        btnDeleteImage.addActionListener(new ActionListener()
        {
            // Hier wird die Aktion des Buttons festgelegt
            public void actionPerformed(ActionEvent e) // Hier wird die Aktion des Buttons festgelegt
            {
                int selectedIndex = lstImageNames.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedImageName = imageNames.get(selectedIndex);
                    int confirm = JOptionPane.showConfirmDialog(
                            frame,
                            "Möchten Sie das ausgewählte Bild wirklich löschen?",
                            "Bild löschen",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        ImageDatabaseHandler.deleteImage(selectedImageName);
                        refreshImageList();
                        lblImage.setIcon(null);
                        JOptionPane.showMessageDialog(frame, "Bild erfolgreich gelöscht!");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Bitte wählen Sie ein Bild zum Löschen aus.");
                }
            }
        });


        // Hier wird die Liste erstellt
        lstImageNames = new JList<>();
        // Hier wird die Aktion der Liste festgelegt
        refreshImageList();
        // Hier wird die Aktion der Liste festgelegt
        lstImageNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hier wird die Aktion der Liste festgelegt
        lstImageNames.addMouseListener(new MouseAdapter() {
            // Hier wird die Aktion der Liste festgelegt
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedIndex = lstImageNames.getSelectedIndex();
                    if (selectedIndex != -1) {
                        String selectedImageName = imageNames.get(selectedIndex);
                        byte[] imageData = ImageDatabaseHandler.retrieveImageData(selectedImageName);
                        if (imageData != null) {
                            try {
                                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

                                int width = lblImage.getWidth();
                                int height = lblImage.getHeight();
                                Image scaledImage = getScaledImageWithAspectRatio(image, width, height);

                                ImageIcon imageIcon = new ImageIcon(scaledImage);
                                lblImage.setIcon(imageIcon);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } else if (e.getClickCount() == 2) {
                    int selectedIndex = lstImageNames.getSelectedIndex();
                    if (selectedIndex != -1) {
                        String selectedImageName = imageNames.get(selectedIndex);
                        ImageDatabaseHandler.openImageWithDefaultViewer(selectedImageName);
                    }
                }
            }
        });



        // Hier wird die ScrollPane erstellt
        JScrollPane listScrollPane = new JScrollPane(lstImageNames);

        JPanel southPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        southPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100; // Breite der Buttons
        gridBagConstraints.ipady = 10; // Höhe der Buttons
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        southPanel.add(btnSaveImage, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        southPanel.add(btnDeleteImage, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        southPanel.add(btnRefreshList, gridBagConstraints);

        contentPane.add(southPanel, BorderLayout.SOUTH);


        contentPane.add(listScrollPane, BorderLayout.WEST);
        contentPane.add(lblImage, BorderLayout.CENTER);

        frame.setVisible(true);
    }
    //Diese Methode erstellt ein Thumbnail
    private ImageIcon createThumbnail(BufferedImage image, int maxWidth, int maxHeight) {
        Image scaledImage = getScaledImageWithAspectRatio(image, maxWidth, maxHeight);
        return new ImageIcon(scaledImage);
    }

    // Diese Methode aktualisiert die Liste
    private void refreshImageList() {
        DefaultListModel<ImageIcon> listModel = new DefaultListModel<>();
        imageNames = ImageDatabaseHandler.getAllImageNames();

        int thumbnailWidth = 150;
        int thumbnailHeight = 150;

        for (String imageName : imageNames) {
            byte[] imageData = ImageDatabaseHandler.retrieveImageData(imageName);
            if (imageData != null) {
                try {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
                    ImageIcon thumbnail = createThumbnail(image, thumbnailWidth, thumbnailHeight);
                    listModel.addElement(thumbnail);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        //Aufruf der des Models
        lstImageNames.setModel(listModel);
    }

    // Diese Methode skaliert das Bild
    private Image getScaledImageWithAspectRatio(BufferedImage image, int maxWidth, int maxHeight) {
        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();
        double scaleFactor = Math.min(maxWidth / originalWidth, maxHeight / originalHeight);

        int newWidth = (int) (originalWidth * scaleFactor);
        int newHeight = (int) (originalHeight * scaleFactor);

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    // Diese Methode erstellt das Fenster
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageUI();
            }
        });
    }
}
