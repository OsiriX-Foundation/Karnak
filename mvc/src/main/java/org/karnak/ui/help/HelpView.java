package org.karnak.ui.help;


import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.ui.MainLayout;
import org.springframework.security.access.annotation.Secured;

@Route(value = "help", layout = MainLayout.class)
@PageTitle("KARNAK - Help")
@Tag("help-view")
@Secured({"ROLE_ADMIN"})
public class HelpView extends VerticalLayout {
    public static final String VIEW_NAME = "Help";


    public HelpView() {
        setSizeFull();
        H1 heading = new H1( "Help" );

        Anchor generalDoc = new Anchor( "https://osirix-foundation.github.io/karnak-documentation/" , "General documentation");
        generalDoc.setTarget( "_blank" );

        Anchor installation = new Anchor( "https://osirix-foundation.github.io/karnak-documentation/docs/installation" , "Installation and configuration with Docker");
        installation.setTarget( "_blank" );

        Anchor profile = new Anchor( "https://osirix-foundation.github.io/karnak-documentation/docs/deidentification/profiles" , "Build your own profile for de-identification or for tag morphing");
        profile.setTarget( "_blank" );
        VerticalLayout layout = new VerticalLayout();
        layout.add(heading, generalDoc,  installation, profile );
        this.add( layout);
    }
}
