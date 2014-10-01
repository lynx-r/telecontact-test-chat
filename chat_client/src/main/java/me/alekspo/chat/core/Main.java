package me.alekspo.chat.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import me.alekspo.chat.controller.MainWindowController;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 12:49
 */
public class Main extends Application {
  private static final String MAIN_WINDOW_FXML = "/fxml/MainWindow.fxml";
  private static final int MAIN_WINDOW_WIDTH = 1140;
  private static final int MAIN_WINDOW_HEIGHT = 520;
  private static MainWindowController mainWindowController;

  public static MainWindowController getMainWindowController() {
    return mainWindowController;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Scene scene = createMainScene(MAIN_WINDOW_FXML);
    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();
  }

  public Scene createMainScene(String fxml) {
    FXMLLoader loader = new FXMLLoader();
    URL location = getClass().getResource(fxml);
    loader.setBuilderFactory(new JavaFXBuilderFactory());
    loader.setLocation(ClassLoader.getSystemResource(fxml));
    try {
      VBox page = loader.load(location.openStream());
      mainWindowController = loader.getController();
      return new Scene(page, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT, Color.WHITE);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
