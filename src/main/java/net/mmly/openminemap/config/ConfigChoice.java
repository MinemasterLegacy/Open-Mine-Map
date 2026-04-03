package net.mmly.openminemap.config;

import net.mmly.openminemap.gui.AnchorWidget;

public interface ConfigChoice {

    void setAnchor(AnchorWidget anchor);

    void writeParameterToFile();

}
