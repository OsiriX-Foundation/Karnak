package org.karnak.ui.project;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.data.gateway.Project;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.ui.MainLayout;
import org.karnak.ui.data.ProjectDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@Route(value = "project", layout = MainLayout.class)
@PageTitle("KARNAK - Project")
public class MainViewProjects extends HorizontalLayout {
    public static final String VIEW_NAME = "Project";

    private ProjectDataProvider projectDataProvider;
    private NewProjectForm newProjectForm;
    private GridProject gridProject;
    private EditProject editProject;
    private Binder<Project> newResearchBinder;

    public MainViewProjects() {
        setWidthFull();
        newProjectForm = new NewProjectForm();
        projectDataProvider = new ProjectDataProvider();
        gridProject = new GridProject(projectDataProvider);
        VerticalLayout layoutNewProject = new VerticalLayout(newProjectForm, gridProject);
        layoutNewProject.setWidth("40%");
        editProject = new EditProject(projectDataProvider);
        editProject.setWidth("60%");
        newResearchBinder = newProjectForm.getBinder();

        add(layoutNewProject, editProject);
        setEventButtonNewProject();
        setEventGridSelection();
    }

    private void setEventButtonNewProject() {
        newProjectForm.getButtonAdd().addClickListener(event -> {
            Project newProject = new Project();
            if (newResearchBinder.writeBeanIfValid(newProject)) {
                newProject.setSecret(HMAC.generateRandomKey());
                projectDataProvider.save(newProject);
                newProjectForm.clear();
                gridProject.selectRow(newProject);
            }
        });
    }

    private void setEventGridSelection() {
        gridProject.asSingleSelect().addValueChangeListener(event -> {
            editProject.setProject(event.getValue());
        });
    }

    @Autowired
    private void addEventManager(ApplicationEventPublisher publisher) {
        projectDataProvider.setApplicationEventPublisher(publisher);
    }
}
