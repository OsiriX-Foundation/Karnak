package org.karnak;

import java.util.Map;
import org.dcm4che3.data.Attributes;

public interface ExternalIDProvider {
  public String getExternalID(Attributes dcm);

  public String getDescription();

  public Map<String, String> getInformation(Attributes dcm);
}
