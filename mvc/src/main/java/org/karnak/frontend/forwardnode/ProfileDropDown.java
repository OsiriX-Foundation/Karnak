package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.combobox.ComboBox;
import org.karnak.backend.data.entity.Profile;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.backend.service.profilepipe.ProfilePipeServiceImpl;

public class ProfileDropDown extends ComboBox<Profile> {

  private final ProfilePipeService profilePipeService;

    public ProfileDropDown() {
        profilePipeService = new ProfilePipeServiceImpl();
        updateList();
    }

    public void updateList() {
        setItems(profilePipeService.getAllProfiles());
        setItemLabelGenerator(Profile::getName);
    }
}
