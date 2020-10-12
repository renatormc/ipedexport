package go.sptc.sinf;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("/views/main-screen.fxml"));
        primaryStage.setTitle("Iped Export");
        Scene screen1 = new Scene(root, 800, 500);
//        screen1.getStylesheets().add("/css/screen1.css");
        primaryStage.setScene(screen1);
        primaryStage.show();
    }
}
