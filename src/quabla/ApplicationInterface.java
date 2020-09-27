package quabla;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;


public class ApplicationInterface extends Application  {

	public void start(Stage stage) {
		stage.setTitle("Input Parameters");
		stage.setWidth(300);
		stage.setHeight(200);

		Button button = new Button("calculate");

		Label label = new Label("落下分散");

		FlowPane root = new FlowPane();
		root.getChildren().addAll(button,label);


		Scene scene = new Scene(root);

		stage.setScene(scene);

		stage.show();


	}

	public static void  main(String[] args) {
		launch();
	}

}
