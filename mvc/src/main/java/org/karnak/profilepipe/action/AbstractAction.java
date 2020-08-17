package org.karnak.profilepipe.action;

import org.dcm4che6.data.VR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract class AbstractAction implements ActionItem {

    protected final String symbol;
    protected String dummyValue;
    protected VR vr;
    protected final Logger LOGGER = LoggerFactory.getLogger(AbstractAction.class);
    protected final Marker CLINICAL_MARKER = MarkerFactory.getMarker("CLINICAL");
    protected final String PATTERN_WITH_INOUT = "TAGHEX={} TAGINT={} DEIDENTACTION={} TAGVALUEIN={} TAGOUT={}";
    protected final String PATTERN_WITH_IN = "TAGHEX={} TAGINT={} DEIDENTACTION={} TAGVALUEIN={}";
    protected final String ADD_METHOD = "a";

    public AbstractAction(String symbol) {
        this.symbol = symbol;
        this.dummyValue = null;
        this.vr = null;
    }

    public AbstractAction(String symbol, String dummyValue) {
        this.symbol = symbol;
        this.dummyValue = dummyValue;
        this.vr = null;
    }

    public AbstractAction(String symbol, String dummyValue, VR vr){
        this.symbol = symbol;
        this.dummyValue = dummyValue;
        this.vr = vr;
    }

    public static AbstractAction convertAction(String action) {
        if (action == null) {
            return null;
        }
        return switch (action) {
            case "Z" -> new ReplaceNull("Z");
            case "X" -> new Remove("X");
            case "K" -> new Keep("K");
            case "U" -> new UID("U");
            case "DDum" -> new DefaultDummy("DDum");
            case "D" -> new Replace("D");
            default -> null;
        };
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDummyValue() {
        return dummyValue;
    }

    public void setDummyValue(String dummyValue) {
        this.dummyValue = dummyValue;
    }

    public VR getVr() { return vr; }

    public void setVr(VR vr) { this.vr = vr; }
}
