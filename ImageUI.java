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
import java.util.ArrayList;
import java.util.List;


public class ImageUI {
    private JFrame frame;
    private JButton btnSaveImage;
    private JButton btnRefreshList;
    private JFileChooser fileChooser;
    private JList<String> lstImageNames;
    private JLabel lblImage;
    private JButton btnDeleteImage;



    public ImageUI() {
        frame = new JFrame("Gallery");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        fileChooser = new JFileChooser();

        btnSaveImage = new JButton("Bild Hinzufügen");
        btnSaveImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

        btnRefreshList = new JButton("Liste aktualisieren");
        btnRefreshList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshImageList();
            }
        });

        btnDeleteImage = new JButton("Bild löschen");
        lblImage = new JLabel();

        btnDeleteImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedImageName = lstImageNames.getSelectedValue();
                if (selectedImageName != null) {
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


        lstImageNames = new JList<>();
        refreshImageList();
        lstImageNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        lstImageNames.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String selectedImageName = lstImageNames.getSelectedValue();
                    byte[] imageData = ImageDatabaseHandler.retrieveImageData(selectedImageName);
                    if (imageData != null) {
                        try {
                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

                            // Skalieren Sie das Bild, um es an das Programmfenster anzupassen
                            int width = lblImage.getWidth();
                            int height = lblImage.getHeight();
                            Image scaledImage = getScaledImageWithAspectRatio(image, width, height);

                            ImageIcon imageIcon = new ImageIcon(scaledImage);
                            lblImage.setIcon(imageIcon);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (e.getClickCount() == 2) {
                    String selectedImageName = lstImageNames.getSelectedValue();
                    ImageDatabaseHandler.openImageWithDefaultViewer(selectedImageName);
                }
            }
        });



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

    private void refreshImageList() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<String> imageNames = ImageDatabaseHandler.getAllImageNames();
        for (String imageName : imageNames) {
            listModel.addElement(imageName);
        }
        lstImageNames.setModel(listModel);
    }

    private Image getScaledImageWithAspectRatio(BufferedImage image, int maxWidth, int maxHeight) {
        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();
        double scaleFactor = Math.min(maxWidth / originalWidth, maxHeight / originalHeight);

        int newWidth = (int) (originalWidth * scaleFactor);
        int newHeight = (int) (originalHeight * scaleFactor);

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageUI();
            }
        });
    }
}
