package controller.interfaces;

import model.interfaces.SideBarModel;
import view.interfaces.SideBarView;

/**
 * Created by stephan on 08.07.17.
 */
public interface SideBarController
{
    void setView(SideBarView view);
    void setModel(SideBarModel model);

    void projectsClicked();

    void statisticsClicked();

    void administrationClicked();

    void settingsClicked();
}