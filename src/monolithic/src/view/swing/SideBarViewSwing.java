package view.swing;

import controller.standard.SideBarControllerStandard;
import data.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by stephan on 08.07.17.
 */
public class SideBarViewSwing
{

  private final JFrame frame;

  private final JButton btProjects;
  private final JButton btPersonalStatistic;
  private final JButton btProjectStatistic;
  private final JButton btAdministration;
  private final JButton btSettings;
  private final JButton btLogout;

  private final JPanel pSideBar;
  private final JPanel pFloatPanel;
  //private final JButton btHelp;

  private SideBarControllerStandard controller;

  public SideBarViewSwing(JFrame frame)
  {
    this.frame = frame;

    pSideBar = new JPanel(new GridLayout(5, 1, 5, 5));
    pSideBar.setBorder(new EmptyBorder(5, 5, 5, 5));
    btProjects = new JButton("Manage Projects");
    btPersonalStatistic = new JButton("Personal Statistic");
    btProjectStatistic = new JButton("Project Statistic");
    btAdministration = new JButton("Administration");
    btSettings = new JButton("Settings");
    btLogout = new JButton("Logout");

    pFloatPanel = new JPanel(new FlowLayout(5));
    pSideBar.add(btProjects);
    pSideBar.add(btPersonalStatistic);
    pSideBar.add(btProjectStatistic);
    pSideBar.add(btSettings);
    pSideBar.add(btLogout);

    pFloatPanel.add(pSideBar);
    setListeners();
    pFloatPanel.setBorder(new EtchedBorder());
    //pSideBar.add(btAdministration);

  }

  private void setListeners()
  {
    btProjects.addActionListener(actionEvent -> controller.projectsClicked());
    btPersonalStatistic.addActionListener(actionEvent -> controller.personalStatisticClicked());
    btAdministration.addActionListener(actionEvent -> controller.administrationClicked());

    btSettings.addActionListener(actionEvent -> controller.settingsClicked());

    btProjectStatistic.addActionListener(actionEvent -> controller.projectStatisticClicked());
    btLogout.addActionListener(actionEvent -> controller.logoutClicked());
  }


  public void setController(SideBarControllerStandard controller)
  {
    this.controller = controller;
  }


  public void show(User.ROLE role)
  {
    if(role == User.ROLE.ADMIN)
      pSideBar.add(btAdministration);
    else
      pSideBar.remove(btAdministration);

    frame.getContentPane().add(pFloatPanel, BorderLayout.WEST);
  }


  public void hide()
  {
    frame.remove(pFloatPanel);
  }
}
