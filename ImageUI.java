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

    public ImageUI() {
        frame = new JFrame("Bild-Datenbank");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        fileChooser = new JFileChooser();

        btnSaveImage = new JButton("Bild speichern");
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
                            ImageIcon imageIcon = new ImageIcon(image);
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

        lblImage = new JLabel();

        contentPane.add(btnSaveImage, BorderLayout.NORTH);
        contentPane.add(btnRefreshList, BorderLayout.SOUTH);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageUI();
            }
        });
    }
}
