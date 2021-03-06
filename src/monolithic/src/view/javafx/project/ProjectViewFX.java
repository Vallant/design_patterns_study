package view.javafx.project;

import controller.standard.ProjectControllerStandard;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ProjectViewFX
{
  private final ProjectViewPane     pMain;
  private final ProjectDetailPane   pDetail;
  private BorderPane mainPane;
  private Stage      mainStage;
  private       ProjectControllerStandard controller;


  public ProjectViewFX()
  {
    pMain = new ProjectViewPane();
    pDetail = new ProjectDetailPane();
    setListener();
  }

  private void setListener()
  {
    pMain.btLeaveProject.setOnAction(actionEvent -> controller.leaveProjectClicked());
    pMain.btAddProject.setOnAction(actionEvent -> controller.addProjectClicked());
    pMain.btDeleteProject.setOnAction(actionEvent -> controller.deleteProjectClicked());

    pMain.lstOwned.setOnMouseClicked(listSelectionEvent ->
    {
      int index = pMain.lstOwned.getSelectionModel().getSelectedIndex();
      controller.ownedProjectsHasSelection(index != -1);
    });

    pMain.lstInvolved.setOnMouseClicked(listSelectionEvent ->
    {
      int index = pMain.lstInvolved.getSelectionModel().getSelectedIndex();
      controller.involvedProjectsHasSelection(index != -1);
    });

    pMain.lstOwned.setOnMouseClicked(clickEvent ->
    {
      if(clickEvent.getClickCount() == 2)
      {
        int index = pMain.lstOwned.getSelectionModel().getSelectedIndex();
        if(index == -1)
          return;
        controller.doubleClickedOn(index);
      }
    });

    pDetail.btBack.setOnAction(actionEvent -> controller.backClicked());

    pDetail.btAddPhase.setOnAction(actionEvent -> controller.addPhaseClicked());
    pDetail.btDeletePhase.setOnAction(actionEvent -> controller.deletePhaseClicked());

    pDetail.btAddMember.setOnAction(actionEvent -> controller.addMemberClicked());

    pDetail.btDeleteMember.setOnAction(actionEvent -> controller.deleteMemberClicked());


    pDetail.btPromoteToAdmin.setOnAction(actionEvent -> controller.promoteToAdminClicked());

    pDetail.btDegradeToMember.setOnAction(actionEvent -> controller.degradeToMemberClicked());

    pDetail.btUpdateDescription.setOnAction(actionEvent -> controller.updateDescriptionClicked());

    pDetail.lstPhases.setOnMouseClicked(listSelectionEvent ->
    {
      int index = pDetail.lstPhases.getSelectionModel().getSelectedIndex();
      controller.projectPhaseHasSelection(index != -1);
    });


    pDetail.lstMembers.setOnMouseClicked(listSelectionEvent ->
    {
      int index = pDetail.lstMembers.getSelectionModel().getSelectedIndex();
      controller.memberTableHasSelection(index != -1);
    });
  }


  public void setController(ProjectControllerStandard controller)
  {
    this.controller = controller;
  }


  public void showOverview()
  {
    mainPane.setCenter(pMain);
    if(pMain.lstInvolved.getSelectionModel().getSelectedIndex() == -1)
      pMain.btLeaveProject.setDisable(true);
    if(pMain.lstOwned.getSelectionModel().getSelectedIndex() == -1)
      pMain.btDeleteProject.setDisable(true);

    mainStage.show();
    mainStage.sizeToScene();
  }


  public void showDetail(String projectName, ArrayList<String> phases, ArrayList<String> members,
                         ArrayList<String> roles, String description)
  {

    pDetail.setProjectName(projectName);
    pDetail.setPhases(phases);
    pDetail.setMemberInformation(members, roles);
    pDetail.setDescription(description);
    mainPane.setCenter(pDetail);
    setMemberListButtonsEnabled(pDetail.lstMembers.getSelectionModel().getSelectedIndex() != -1);
    setProjectPhaseButtonsEnabled(pDetail.lstPhases.getSelectionModel().getSelectedIndex() != -1);
  }


  public void hide()
  {
    mainPane.getChildren().remove(pMain);
    mainPane.getChildren().remove(pDetail);
    mainStage.show();
  }


  public void setParticipatingProjects(ArrayList<String> participatingProjects)
  {
    pMain.lstInvolved.getItems().clear();
    pMain.lstInvolved.getItems().addAll(participatingProjects);
    pMain.lstInvolved.refresh();
  }


  public void setOwnedProjects(ArrayList<String> ownedProjects)
  {
    pMain.lstOwned.getItems().clear();
    pMain.lstOwned.getItems().addAll(ownedProjects);
    pMain.lstOwned.refresh();
  }


  public void showError(String localizedMessage)
  {
    Alert alert = new Alert(Alert.AlertType.ERROR, localizedMessage);
    alert.showAndWait();
  }


  public void showProjectCreationDialog()
  {
    ProjectAddDialog dlg = new ProjectAddDialog();
    dlg.showAndWait().ifPresent(response ->
    {

      if(response == ButtonType.OK)
      {
        String description = dlg.getDescription();
        String name = dlg.getName();
        controller.addProject(name, description);
      }
      else
      {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to cancel", ButtonType.YES,
          ButtonType.NO);
        alert.showAndWait();

        if(alert.getResult() == ButtonType.NO)
          showProjectCreationDialog();
      }
    });
  }


  public void setOwnedProjectsButtonsEnabled(boolean enabled)
  {
    pMain.btDeleteProject.setDisable(!enabled);
  }


  public void setInvolvedProjectsButtonsEnabled(boolean enabled)
  {
    pMain.btLeaveProject.setDisable(!enabled);
  }


  public int getSelectedOwnedProjectIndex()
  {
    return pMain.lstOwned.getSelectionModel().getSelectedIndex();
  }


  public int getSelectedInvolvedProjectIndex()
  {
    return pMain.lstInvolved.getSelectionModel().getSelectedIndex();
  }


  public int getSelectedPhaseIndex()
  {
    return pDetail.lstPhases.getSelectionModel().getSelectedIndex();
  }


  public void showAddMemberDialog(ArrayList<String> names)
  {
    ProjectAddMemberDialog dlg = new ProjectAddMemberDialog();
    dlg.setAvailableNames(names);
    dlg.showAndWait().ifPresent(response ->
    {

      if(response == ButtonType.OK)
      {
        ObservableList<Integer> list = dlg.lstAvailableUsers.getSelectionModel().getSelectedIndices();
        int[] array = new int[list.size()];
        for(int i = 0; i < list.size(); i++)
        {
          array[i] = list.get(i);
        }
        controller.addMembers(array);
      }
    });
  }


  public String getDescription()
  {
    return pDetail.taDescription.getText();
  }


  public void setProjectPhaseButtonsEnabled(boolean hasSelection)
  {
    pDetail.btDeletePhase.setDisable(!hasSelection);
  }


  public void setMemberListButtonsEnabled(boolean hasSelection)
  {
    pDetail.btDegradeToMember.setDisable(!hasSelection);
    pDetail.btPromoteToAdmin.setDisable(!hasSelection);
    pDetail.btDeleteMember.setDisable(!hasSelection);
  }


  public int getSelectedMemberIndex()
  {
    return pDetail.lstMembers.getSelectionModel().getSelectedIndex();
  }


  public void showAddPhaseDialog()
  {

    ProjectAddPhaseDialog dlg = new ProjectAddPhaseDialog();
    dlg.showAndWait().ifPresent(response ->
    {

      if(response == ButtonType.OK)
      {
        String name = dlg.getName();
        controller.addPhase(name);
      }
    });
  }

  public void setMainPane(BorderPane mainPane)
  {
    this.mainPane = mainPane;
  }

  public void setMainStage(Stage mainStage)
  {
    this.mainStage = mainStage;
  }
}
