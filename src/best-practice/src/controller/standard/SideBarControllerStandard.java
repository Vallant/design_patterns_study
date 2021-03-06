package controller.standard;

import controller.interfaces.SideBarController;
import model.interfaces.SideBarModel;
import view.interfaces.SideBarView;

/**
 * Created by stephan on 08.07.17.
 */
public class SideBarControllerStandard implements SideBarController
{
  private SideBarView  view;
  private SideBarModel model;


  @Override
  public void setView(SideBarView view)
  {
    this.view = view;
  }

  @Override
  public void setModel(SideBarModel model)
  {
    this.model = model;
  }

  @Override
  public void projectsClicked()
  {
    model.projectsClicked();
  }

  @Override
  public void personalStatisticClicked()
  {
    model.personalStatisticClicked();
  }

  @Override
  public void administrationClicked()
  {
    model.administrationClicked();
  }

  @Override
  public void settingsClicked()
  {
    model.settingsClicked();
  }

  @Override
  public void projectStatisticClicked()
  {
    model.projectStatisticClicked();
  }

  @Override
  public void logoutClicked()
  {
    model.logoutClicked();
  }
}
