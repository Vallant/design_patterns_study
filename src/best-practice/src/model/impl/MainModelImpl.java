/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.impl;

import controller.common.ControllerManager;
import controller.interfaces.MainController;
import data.User;
import db.common.DBManager;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.interfaces.*;

import javax.swing.*;

/**
 *
 * @author stephan
 */
public class MainModelImpl implements MainModel
{
    private final MainController controller;
    
    private final LoginModel login;
    private final ProjectModel project;
    private final ActivityBarModel activityBar;
    private final SideBarModel sideBar;
    private final StatisticsModel statistics;

    private User user;
    private final DBManager db;
    
    
    MainModelImpl(String driver, String url, String username, String password, String frontend) throws Exception
    {
        
        db = DBManager.get(driver, url, username, password);
        ControllerManager.initInstance(frontend);
        
        controller = ControllerManager.getInstance();
        controller.setModel(this);
        
        login = new LoginModelImpl();
        pairLogin();
        project = new ProjectModelImpl();
        pairProject();
        activityBar = new ActivityBarModelImpl();
        pairActivityBar();
        sideBar = new SideBarModelImpl();
        pairSideBar();
        statistics = new StatisticsModelImpl();
        pairStatistics();
        
        controller.switchToLogin();
    }


    public static void main(String[] args)
    {
        try
        {
            new MainModelImpl(args[0], args[1], args[2], args[3], args[4]);
        }
        catch (Exception ex)
        {
            Logger.getLogger(MainModelImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @Override
    public void loginSuccessfulFor(User user)
    {
        this.user = user;
        activityBar.setUser(user);
        statistics.setUser(user);
        project.setUser(user);

        controller.switchToProjectView();
        controller.showActivityBar();
        controller.showSideBar(user.getRole());


        project.refresh();
        activityBar.refresh();
        statistics.refresh();
        sideBar.refresh();
    }

    @Override
    public void showError(Exception ex)
    {
        controller.showError(ex);
    }

    private void pairLogin()
    {
        login.setMainModel(this);
        controller.pairLogin(login);
    }

    private void pairActivityBar()
    {
        activityBar.setMainModel(this);
        controller.pairActivityBar(activityBar);
    }

    private void pairProject()
    {
        project.setMainModel(this);
        controller.pairProject(project);   
    }

    private void pairSideBar()
    {
        sideBar.setMainModel(this);
        controller.pairSideBar(sideBar);
    }

    private void pairStatistics() {
        statistics.setMainModel(this);
        controller.pairStatistics(statistics);
    }

    @Override
    public DBManager DB()
    {
        return db;
    }

    @Override
    public void switchedToStatistics() {
        controller.switchToStatisticView();
    }

    @Override
    public void switchedToProjects() {
        controller.switchToProjectView();
    }

    @Override
    public void switchedToAdministration() {
        controller.switchToAdminView();
    }

    @Override
    public void switchedToSettings() {
        controller.switchToSettingsView();
    }

    @Override
    public void refreshActivityBar() {
        activityBar.refresh();
    }
}

