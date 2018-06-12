package sample;

import controller.monitor.bjaku.com.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/MainWindow.fxml"));   //Wskazanie gdzie jest plik z GUI
        SplitPane mainSplitPane = loader.load();    //Otrzymanie wezla root z pliku fxml
        MainWindowController mainWindowController = loader.getController(); //Otrzymanie z pliku fxml kontrolera
        primaryStage.setOnCloseRequest( e -> mainWindowController.shutdown());  //Na zdarzenie zamkniecia okna odpowie metoda shutdown() z mainWindowController
        Scene scene = new Scene(mainSplitPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JMX Agent");  //Nazwa glownego okienka programu
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();    //Wyswietl glowne okienko programu
    }


    public static void main(String[] args) {
        launch(args);
    }
}
