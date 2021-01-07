package org.karnak.backend.model.standard;

import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.ProfileElement;
import org.karnak.backend.enums.ProfileItemType;
import org.karnak.backend.model.action.ActionItem;
import org.karnak.backend.model.dicominnolitics.StandardConfidentialityProfiles;
import org.karnak.backend.model.dicominnolitics.jsonConfidentialityProfiles;
import org.karnak.backend.model.profilepipe.TagActionMap;
import org.karnak.backend.model.profiles.AbstractProfileItem;
import org.karnak.backend.model.profiles.PrivateTags;
import org.karnak.backend.model.profiles.ProfileItem;
import org.karnak.backend.util.PrivateTagPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfidentialityProfiles {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfidentialityProfiles.class);

  private final TagActionMap actionMap = new TagActionMap();
  private final List<ProfileItem> listProfiles = new ArrayList<>();

    public ConfidentialityProfiles() {
      final StandardConfidentialityProfiles standardConfidentialityProfiles = new StandardConfidentialityProfiles();
      jsonConfidentialityProfiles[] confidentialityProfiles = StandardConfidentialityProfiles
          .getConfidentialityProfiles();

        for (jsonConfidentialityProfiles confidentialityProfilesTag : confidentialityProfiles) {
            String tag = confidentialityProfilesTag.getTag();
            ActionItem action = confidentialityProfilesTag.getBasicProfile();
            String name = confidentialityProfilesTag.getName();
            AbstractProfileItem item;
            if (PrivateTagPattern.TAG_PATTERN.equals(tag)) {
                try {
                  final ProfileElement profileElement = new ProfileElement(name,
                      ProfileItemType.ACTION_PRIVATETAGS.getClassAlias(), null, "X", null, null,
                      null);
                    item = new PrivateTags(profileElement);
                } catch (Exception e) {
                    item = null;
                    LOGGER.error("Cannot build the profile: PrivateTags", e);
                }
            } else {
                actionMap.put(tag, action);
                item = null;
            }

            if (item != null) {
                listProfiles.add(item);
            }
        }
    }

    public TagActionMap getActionMap() {
        return actionMap;
    }

    public List<ProfileItem> getListProfiles() {
        return listProfiles;
    }

}
