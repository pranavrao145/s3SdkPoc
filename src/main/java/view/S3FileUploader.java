package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3FileUploader extends JFrame {

  private JTextField filePathField;

  public S3FileUploader() {
    super("S3 File Uploader");

    // Set up the UI components
    JPanel panel = new JPanel();
    filePathField = new JTextField(20);
    JButton browseButton = new JButton("Browse");
    JButton uploadButton = new JButton("Upload");

    // Add components to the panel
    panel.add(filePathField);
    panel.add(browseButton);
    panel.add(uploadButton);

    // Add the panel to the frame
    this.add(panel);

    // Set up action listeners
    browseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        browseFile();
      }
    });

    uploadButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        uploadFile();
      }
    });

    // Set frame properties
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 100);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void browseFile() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
      filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }
  }

  private void uploadFile() {
    String filePath = filePathField.getText();

    if (filePath.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please select a file to upload.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
    } else if (!filePath.endsWith(".jpg")) {
      JOptionPane.showMessageDialog(
          this, "Only .jpg files are allowed to upload. Please try again.",
          "Error", JOptionPane.ERROR_MESSAGE);
    } else {
      Region region = Region.CA_CENTRAL_1;
      String bucketName = System.getenv("AWS_S3_BUCKET_NAME");

      S3Client s3 = S3Client.builder()
                        .region(region)
                        .credentialsProvider(
                            EnvironmentVariableCredentialsProvider.create())
                        .build();

      String key = "1.jpg"; // TODO: we have to change this to make a MongoDB
                            // call and get the highest image ID we have so far,
                            // and then add 1

      PutObjectRequest request =
          PutObjectRequest.builder().bucket(bucketName).key(key).build();

      try {
        s3.putObject(request, new java.io.File(filePath).toPath());
        JOptionPane.showMessageDialog(this, "File uploaded successfully!",
                                      "Success",
                                      JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
            this, "Error uploading file: " + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
      } finally {
        s3.close();
      }
    }
  }
}
