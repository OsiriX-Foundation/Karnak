package org.karnak.frontend.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.util.Comparator;
import org.karnak.backend.data.entity.Profile;
import org.karnak.backend.data.entity.ProfileElement;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileComponent extends VerticalLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileComponent.class);

    private Profile profile;
    private final ProfilePipeService profilePipeService;
    private final ProfileNameGrid profileNameGrid;
    private Anchor download;
    private Button deleteButton;
    private final WarningDeleteProfileUsed dialogWarning;
    private final ProfileElementMainView profileElementMainView;

    ProfileComponent(ProfilePipeService profilePipeService, ProfileNameGrid profileNameGrid,
        ProfileElementMainView profileElementMainView) {
        setSizeFull();
        this.profileElementMainView = profileElementMainView;
        this.profilePipeService = profilePipeService;
        this.profileNameGrid = profileNameGrid;
        dialogWarning = new WarningDeleteProfileUsed();
    }

    public void setProfile() {
        removeAll();
        H2 title = new H2("Profile");
        ProfileMetadata name = new ProfileMetadata("Name", profile.getName(), profile.getBydefault());
        name.getValidateEditButton().addClickListener(event -> {
            profile.setName(name.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata version = new ProfileMetadata("Profile version", profile.getVersion(), profile.getBydefault());
        version.getValidateEditButton().addClickListener(event -> {
            profile.setVersion(version.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata minVersion = new ProfileMetadata("Min. version KARNAK required", profile.getMinimumkarnakversion(), profile.getBydefault());
        minVersion.getValidateEditButton().addClickListener(event -> {
            profile.setMinimumkarnakversion(minVersion.getValue());
            updatedProfilePipes();
        });

        ProfileMetadata defaultIssuerOfPatientID = new ProfileMetadata("Default issuer of PatientID", profile.getDefaultissueropatientid(), false);
        defaultIssuerOfPatientID.getValidateEditButton().addClickListener(event -> {
            profile.setDefaultissueropatientid(defaultIssuerOfPatientID.getValue());
            updatedProfilePipes();
        });
        createDownloadButton(profile);

        ProfileMasksView profileMasksView = new ProfileMasksView(profile.getMasks());

        if (profile.getBydefault()) {
            add(new HorizontalLayout(title, download), name, version, minVersion, defaultIssuerOfPatientID, profileMasksView);
        } else {
            createDeleteButton(profile);
            add(new HorizontalLayout(title, download, deleteButton), name, version, minVersion, defaultIssuerOfPatientID, profileMasksView);
        }


    }

    private void updatedProfilePipes() {
        profilePipeService.updateProfile(profile);
        profileNameGrid.updatedProfilePipesView();
        final StreamResource profileStreamResource = createStreamResource(profile);
        download.setHref(profileStreamResource);
        createDeleteButton(profile);
    }

    public void setEventValidate(ProfileMetadata metadata) {
        metadata.getValidateEditButton().addClickListener(event -> {
            profile.setName(metadata.getValue());
        });
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        if (profile != null) {
            this.profile = profile;
            setProfile();
        }
    }

    public void createDownloadButton(Profile profile) {
        final StreamResource profileStreamResource = createStreamResource(profile);
        download = new Anchor(profileStreamResource, "");
        download.getElement().setAttribute("download", true);
        download.add(new Button(new Icon(VaadinIcon.DOWNLOAD_ALT)));
        download.getStyle().set("margin-top","30px");
    }

    private void createDeleteButton(Profile profile){
        deleteButton = new Button((new Icon(VaadinIcon.TRASH)));
        deleteButton.setWidth("100%");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        deleteButton.getStyle().set("margin-top","34px");
        deleteButton.addClickListener(buttonClickEvent -> {
            if (profile.getProject() != null && profile.getProject().size() > 0) {
                dialogWarning.setText(profile);
                dialogWarning.open();
            } else {
                profilePipeService.deleteProfile(profile);
                profileNameGrid.updatedProfilePipesView();
                removeProfileInView();
            }
        });
    }

    public static StreamResource createStreamResource(Profile profile) {
        try{
            profile.getProfileElements().sort(Comparator.comparingInt(ProfileElement::getPosition));
            //https://stackoverflow.com/questions/61506368/formatting-yaml-with-jackson
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

            String strYaml = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(profile);
            StreamResource streamResource = new StreamResource(String.format("%s.yml",profile.getName()).replace(" ", "-"), () -> new ByteArrayInputStream(strYaml.getBytes()));
            return streamResource;
        } catch (final Exception e) {
            LOGGER.error("Cannot create the StreamResource for downloading the yaml profile", e);
        }
        return null;
    }

    public void removeProfileInView() {
        profileElementMainView.removeAll();
        removeAll();
    }
}
