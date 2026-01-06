package dev.quentintyr.visiblearmorslots.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.quentintyr.visiblearmorslots.config.ConfigScreen;

/**
 * ModMenu integration - provides config screen
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
