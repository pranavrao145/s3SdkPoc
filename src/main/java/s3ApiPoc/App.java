package s3ApiPoc;

import javax.swing.SwingUtilities;
import view.S3FileUploader;

public class App {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new S3FileUploader();
      }
    });
  }
}
