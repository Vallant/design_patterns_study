package view.javafx.project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class ProjectViewPane extends GridPane
{

  final Button           btAddProject;
  final Button           btDeleteProject;
  final ListView<String> lstOwned;

  final Button           btLeaveProject;
  final ListView<String> lstInvolved;

  public ProjectViewPane()
  {
    setPadding(new Insets(5, 5, 5, 5));
    setHgap(5);
    setVgap(5);

    BorderPane pOwned = new BorderPane();
    GridPane pOwnedButtons = new GridPane();
    pOwnedButtons.setPadding(new Insets(0, 5, 5, 5));
    pOwnedButtons.setVgap(5);
    pOwnedButtons.setHgap(5);

    btAddProject = new Button("Add Project");
    int BUTTON_WIDTH = 130;
    btAddProject.setPrefWidth(BUTTON_WIDTH);
    btDeleteProject = new Button("Delete Project");
    btDeleteProject.setPrefWidth(BUTTON_WIDTH);
    lstOwned = new ListView<>();
    ScrollPane spOwned = new ScrollPane(lstOwned);

    add(pOwned, 0, 0);
    //pOwned.setRight(pFlowPanel1);
    pOwned.setRight(pOwnedButtons);
    pOwnedButtons.add(btAddProject, 0, 0);
    pOwnedButtons.add(btDeleteProject, 0, 1);
    pOwned.setCenter(spOwned);


    BorderPane pInvolved = new BorderPane();

    GridPane pInvolvedButtons = new GridPane();
    pInvolvedButtons.setVgap(5);
    pInvolvedButtons.setPadding(new Insets(0, 5, 5, 5));
    btLeaveProject = new Button("Leave Project");
    btLeaveProject.setPrefWidth(BUTTON_WIDTH);
    lstInvolved = new ListView<>();
    ScrollPane spInvolved = new ScrollPane(lstInvolved);

    add(pInvolved, 0, 1);
    pInvolved.setRight(pInvolvedButtons);
    pInvolvedButtons.add(btLeaveProject, 0, 0);
    pInvolved.setCenter(spInvolved);

    setHgrow(pOwned, Priority.ALWAYS);
    setVgrow(pOwned, Priority.ALWAYS);

    setHgrow(pInvolved, Priority.ALWAYS);
    setVgrow(pInvolved, Priority.ALWAYS);

    setHgrow(lstOwned, Priority.ALWAYS);
    setVgrow(lstOwned, Priority.ALWAYS);

    setHgrow(lstInvolved, Priority.ALWAYS);
    setVgrow(lstInvolved, Priority.ALWAYS);

    setHgrow(spOwned, Priority.ALWAYS);
    setVgrow(spOwned, Priority.ALWAYS);

    setHgrow(spInvolved, Priority.ALWAYS);
    setVgrow(spInvolved, Priority.ALWAYS);

    setHgrow(this, Priority.ALWAYS);
    setVgrow(this, Priority.ALWAYS);

    spInvolved.setFitToHeight(true);
    spInvolved.setFitToWidth(true);
    spOwned.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    spOwned.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


    spOwned.setFitToWidth(true);
    spOwned.setFitToHeight(true);
    spOwned.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    spOwned.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

  }
}
