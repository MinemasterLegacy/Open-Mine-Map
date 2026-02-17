package net.mmly.openminemap;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class OpenMineMapDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(OmmEnglishLanguageProvider::new);
        pack.addProvider(OmmTraditionalTaiwaneseChineseLanguageProvider::new);
        pack.addProvider(OmmTraditionalHongKongChineseLanguageProvider::new);
        pack.addProvider(OmmSimplifiedChineseLanguageProvider::new);
        pack.addProvider(OmmArgentinianSpanishLanguageProvider::new);
        pack.addProvider(OmmChileanSpanishLanguageProvider::new);
        pack.addProvider(OmmEcuadorianSpanishLanguageProvider::new);
        pack.addProvider(OmmEuropeanSpanishLanguageProvider::new);
        pack.addProvider(OmmMexicanSpanishLanguageProvider::new);
        pack.addProvider(OmmUruguayanSpanishLanguageProvider::new);
        pack.addProvider(OmmVenezuelanSpanishLanguageProvider::new);
        pack.addProvider(OmmEuropeanFrenchLanguageProvider::new);
        pack.addProvider(OmmCanadianFrenchLanguageProvider::new);
        pack.addProvider(OmmModernRussianLanguageProvider::new);
        pack.addProvider(OmmPreRevolutionaryRussianLanguageProvider::new);
        pack.addProvider(OmmEuropeanGermanLanguageProvider::new);
        pack.addProvider(OmmSwissGermanLanguageProvider::new);
        pack.addProvider(OmmAustrianGermanLanguageProvider::new);
        pack.addProvider(OmmBrazilianPortugueseLanguageProvider::new);
        pack.addProvider(OmmEuropeanPortugueseLanguageProvider::new);
        pack.addProvider(OmmModernIndonesianLanguageProvider::new);
        pack.addProvider(OmmPreReformIndonesianLanguageProvider::new);
        pack.addProvider(OmmJapaneseLanguageProvider::new);
        pack.addProvider(OmmItalianLanguageProvider::new);
    }
}

class OmmEnglishLanguageProvider extends FabricLanguageProvider {
    protected OmmEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    // ----- ENGLISH -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        //language-wide translations
        translationBuilder.add("omm.config.state.tpll", "/tpll");
        translationBuilder.add("omm.config.state.tp", "/tp");
        translationBuilder.add("omm.category.openminemap", "OpenMineMap");

        //english-only translations
        translationBuilder.add("omm.config.option.artificial-zoom", "Artificial Zoom");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "Artificial Zoom allows for higher zoom levels than normal (+6 levels) by oversizing the smallest tile size.");
        translationBuilder.add("omm.osm-attribution", "© {OpenStreetMap Contributors}");
        translationBuilder.add("omm.config.option.configure-hud", "Configure HUD...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Change positioning and size of HUD elements");
        translationBuilder.add("omm.config.category.general", "General");
        translationBuilder.add("omm.config.gui.save-and-exit", "Save and Exit");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Exit without Saving");
        translationBuilder.add("omm.config.option.players", "Players");
        translationBuilder.add("omm.config.category.overlays", "Overlays");
        translationBuilder.add("omm.config.tooltip.players", "Show Players on all maps");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Reverse the scroll wheel.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Reverse Scroll");
        translationBuilder.add("omm.config.option.zoom-strength", "Zoom Strength");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "The amount zoom changes for each zoom input");
        translationBuilder.add("omm.config.option.snap-angle", "Snap Angle");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Set an angle that can be snapped to using a keybind. Can be used to help make straight lines. (Use a Minecraft angle)");
        translationBuilder.add("omm.config.option.rcm-uses", "RCM Uses");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "The command that will be used to teleport when using the Fullscreen Right Click Menu.");
        translationBuilder.add("omm.config.option.directions", "Directions");
        translationBuilder.add("omm.config.tooltip.directions", "Show Direction Indicators on all maps");
        translationBuilder.add("omm.config.option.altitude-shading", "Altitude Shading");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Shade other players white when they are above you and black when they are below you.");
        translationBuilder.add("omm.config.category.tile-source", "Tile Source");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Mouse: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Player: ");
        translationBuilder.add("omm.config.gui.previous-source", "Previous Source");
        translationBuilder.add("omm.config.gui.next-source", "Next Source");
        translationBuilder.add("omm.config.gui.reset-to-default", "Reset to Default");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Permanent");
        translationBuilder.add("omm.text.name", "Name");
        translationBuilder.add("omm.text.latitude", "Latitude");
        translationBuilder.add("omm.text.longitude", "Longitude");
        translationBuilder.add("omm.waypoints.button.create", "Create Waypoint");
        translationBuilder.add("omm.waypoints.button.save", "Save Waypoint");
        translationBuilder.add("omm.waypoints.button.delete", "Delete Waypoint");
        translationBuilder.add("omm.waypoints.button.edit", "Edit Waypoint");
        translationBuilder.add("omm.waypoints.button.view", "View Waypoint");
        translationBuilder.add("omm.waypoints.button.pin", "Pin Waypoint");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "Toggle Hud Elements");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Dominant over the toggle keybind");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Currently Enabled");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Currently Disabled");
        translationBuilder.add("omm.hud.out-of-bounds", "Out Of Bounds");
        translationBuilder.add("omm.error.tile-url.start", "OpenMineMap Tile Urls");
        translationBuilder.add("omm.error.tile-source-json-formatting", "tileSources.json is formatted incorrectly.");
        translationBuilder.add("omm.error.blank-tile-url", "Blank TileUrl detected. This may be due to invalid file formatting.");
        translationBuilder.add("omm.error.blank-field", "At least one required field is blank.");
        translationBuilder.add("omm.error.source-link-invalid", "Source Url is not a valid link.");
        translationBuilder.add("omm.error.attribution-link-invalid", "At least one Attribution Link is not a valid link.");
        translationBuilder.add("omm.error.source-bracket-placement", "Bracket placement for Source Url is invalid.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "Bracket placement for Attribution is invalid.");
        translationBuilder.add("omm.error.link-number-mismatch", "Mismatched number of links between Attribution Links list and Attribution string.");
        translationBuilder.add("omm.error.field-missing-x", "Source Url is missing an X field.");
        translationBuilder.add("omm.error.field-missing-y", "Source Url is missing a Y field.");
        translationBuilder.add("omm.error.field-missing-zoom", "Source Url is missing a zoom field.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Open Fullscreen Map");
        translationBuilder.add("omm.key.zoom-in", "Zoom In (HUD)");
        translationBuilder.add("omm.key.zoom-out", "Zoom Out (HUD)");
        translationBuilder.add("omm.key.toggle-map", "Toggle Map (HUD)");
        translationBuilder.add("omm.key.copy-coordinates", "Copy Coordinates to Clipboard");
        translationBuilder.add("omm.key.snap-angle", "Snap to Angle");
        translationBuilder.add("omm.key.execute.error.snap-angle", "An error occurred.");
        translationBuilder.add("omm.key.execute.snap-angle", "Snap!");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Seems like you're outside the bounds of the projection. Please re-enter into reality and try again.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Coordinates copied to clipboard");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "There was an error while doing that.");
        translationBuilder.add("omm.fullscreen.report-bugs", "Report Bugs");
        translationBuilder.add("omm.config.gui.omm-wiki", "OpenMineMap Wiki");
        translationBuilder.add("omm.error.incomplete-coordinates", "An error occurred. You likely entered incomplete coordinates.");
        translationBuilder.add("omm.error.formatted-coordinates", "An error occurred. You likely entered coordinates with invalid formatting.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "An error occurred. You many have entered coordinates that are invalid or out of bounds.");
        translationBuilder.add("omm.error.out-of-bounds", "An error occurred. You likely entered coordinates that are out of bounds.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Error parsing coordinates; The player you are trying to teleport to may be out of bounds of the projection.");
        translationBuilder.add("omm.error.cannot-find-player-start", "Could not find player \"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\" within rendered area.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: Waypoint property change failed");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: Waypoint delete failed");
        translationBuilder.add("omm.config.state.on", "On");
        translationBuilder.add("omm.config.state.off", "Off");
        translationBuilder.add("omm.config.state.none", "None");
        translationBuilder.add("omm.config.state.self", "Self");
        translationBuilder.add("omm.config.state.local", "Local");
        translationBuilder.add("omm.config.option.hover-names", "Hover Names");
        translationBuilder.add("omm.config.tooltip.hover-names", "Show player names when hovering the mouse over players in the fullscreen map");
        translationBuilder.add("omm.waypoints.editing", "(Editing...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Set a custom URL for tiles to be loaded from. Click for more information.");
        translationBuilder.add("omm.rcm.teleport-here", "Teleport Here");
        translationBuilder.add("omm.rcm.copy-coordinates", "Copy Coordinates");
        translationBuilder.add("omm.rcm.open-in", "Open In...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Edit Waypoint");
        translationBuilder.add("omm.rcm.set-snap-angle", "Set Snap Angle");
        translationBuilder.add("omm.rcm.view-on-map", "View On Map");
        translationBuilder.add("omm.rcm.unpin", "Unpin");
        translationBuilder.add("omm.rcm.create-waypoint", "Create Waypoint");
        translationBuilder.add("omm.error.tile-url.parse", "Error Parsing Tile Source");
        translationBuilder.add("omm.notification.something-wrong", "Something went wrong");
        translationBuilder.add("omm.notification.location-copied", "Copied location to clipboard.");
        translationBuilder.add("omm.notification.searching", "Searching...");
        translationBuilder.add("omm.notification.snap-angle-set", "Snap Angle set to ");
        translationBuilder.add("omm.search.anything", "Search anything...");
        translationBuilder.add("omm.search.blocks-away", " meters away");
        translationBuilder.add("omm.search.places", "Search Places");
        translationBuilder.add("omm.search.no-results", "No web results");
        translationBuilder.add("omm.rcm.reverse-search", "Reverse Search");
        translationBuilder.add("omm.error.distortion", "Cannot calculate distortion: Out of Bounds");
        translationBuilder.add("omm.text.distortion", "Distortion:");
    }
}

abstract class OmmSpanishLanguageProvider extends FabricLanguageProvider {
    protected OmmSpanishLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- SPANISH -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "Zoom artificial");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "El zoom artificial permite niveles de zoom más altos de lo normal (+6 niveles) aumentando el tamaño del tile más pequeño.");
        translationBuilder.add("omm.osm-attribution", "© {Colaboradores de OpenStreetMap}");
        translationBuilder.add("omm.config.option.configure-hud", "Configurar HUD...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Cambiar la posición y tamaño de los elementos del HUD");
        translationBuilder.add("omm.config.category.general", "General");
        translationBuilder.add("omm.config.gui.save-and-exit", "Guardar y Salir");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Salir sin guardar");
        translationBuilder.add("omm.config.option.players", "Jugadores");
        translationBuilder.add("omm.config.category.overlays", "Capas");
        translationBuilder.add("omm.config.tooltip.players", "Mostrar jugadores en todos los mapas");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Invertir la rueda del mouse.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Invertir desplazamiento");
        translationBuilder.add("omm.config.option.zoom-strength", "Intensidad del zoom");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "La cantidad que cambia el zoom con cada acción de acercar o alejar.");
        translationBuilder.add("omm.config.option.snap-angle", "Ajustar el ángulo");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Configura un ángulo al que se puede ajustar usando una tecla. Puede ayudar a crear líneas rectas (usa ángulos de Minecraft).");
        translationBuilder.add("omm.config.option.rcm-uses", "Usos del RCM");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "El comando que se utilizará para teletransportarse al utilizar el menú de clic derecho en pantalla completa.");
        translationBuilder.add("omm.config.option.directions", "Direcciones");
        translationBuilder.add("omm.config.tooltip.directions", "Mostrar indicadores de dirección en todos los mapas");
        translationBuilder.add("omm.config.option.altitude-shading", "Sombreado de altitud");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Sombrea a otros jugadores de blanco cuando están por encima de ti y de negro cuando están por debajo.");
        translationBuilder.add("omm.config.category.tile-source", "Fuente de mapas");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Mouse: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Jugador: ");
        translationBuilder.add("omm.config.gui.previous-source", "Mapa anterior");
        translationBuilder.add("omm.config.gui.next-source", "Siguiente mapa");
        translationBuilder.add("omm.config.gui.reset-to-default", "Restablecer valores predeterminados");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Permanente");
        translationBuilder.add("omm.text.name", "Nombre");
        translationBuilder.add("omm.text.latitude", "Latitud");
        translationBuilder.add("omm.text.longitude", "Longitud");
        translationBuilder.add("omm.waypoints.button.create", "Crear un Waypoint");
        translationBuilder.add("omm.waypoints.button.save", "Guardar Waypoint");
        translationBuilder.add("omm.waypoints.button.delete", "Eliminar Waypoint");
        translationBuilder.add("omm.waypoints.button.edit", "Editar Waypoint");
        translationBuilder.add("omm.waypoints.button.view", "Ver Waypoint");
        translationBuilder.add("omm.waypoints.button.pin", "Fijar Waypoint");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "Alternar elementos del HUD");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Tiene prioridad sobre la tecla de alternar");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Actualmente activado");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Actualmente desactivado");
        translationBuilder.add("omm.hud.out-of-bounds", "Fuera del límite");
        translationBuilder.add("omm.error.tile-url.start", "OpenMineMap Tile Urls");
        translationBuilder.add("omm.error.tile-source-json-formatting", "<tileSources.json> no tiene un formato válido.");
        translationBuilder.add("omm.error.blank-tile-url", "Se detectó una URL de tiles vacía. Esto puede deberse a un formato de archivo inválido.");
        translationBuilder.add("omm.error.blank-field", "Al menos un campo obligatorio está vacío.");
        translationBuilder.add("omm.error.source-link-invalid", "La URL de la fuente no es un enlace válido.");
        translationBuilder.add("omm.error.attribution-link-invalid", "Al menos un enlace de atribución no es válido.");
        translationBuilder.add("omm.error.source-bracket-placement", "La colocación de corchetes para la URL de la fuente no es válida.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "La colocación de corchetes para la atribución no es válida.");
        translationBuilder.add("omm.error.link-number-mismatch", "El número de enlaces no coincide entre la lista de enlaces de atribución y el texto de atribución.");
        translationBuilder.add("omm.error.field-missing-x", "A la URL de la fuente le falta el campo X.");
        translationBuilder.add("omm.error.field-missing-y", "A la URL de la fuente le falta el campo Y.");
        translationBuilder.add("omm.error.field-missing-zoom", "A la URL de la fuente le falta el campo de zoom.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Abrir mapa en pantalla completa");
        translationBuilder.add("omm.key.zoom-in", "Acercar (HUD)");
        translationBuilder.add("omm.key.zoom-out", "Alejar (HUD)");
        translationBuilder.add("omm.key.toggle-map", "Alternar Mapa (HUD)");
        translationBuilder.add("omm.key.copy-coordinates", "Copiar Coordenadas al Portapapeles");
        translationBuilder.add("omm.key.snap-angle", "Ajustar el ángulo");
        translationBuilder.add("omm.key.execute.error.snap-angle", "Ocurrió un error.");
        translationBuilder.add("omm.key.execute.snap-angle", "Ajustado!");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Parece que estás fuera de los límites de la proyección. Vuelve a una zona válida e inténtalo de nuevo.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Coordenadas copiadas al portapapeles");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "Ocurrió un error al realizar la acción");
        translationBuilder.add("omm.fullscreen.report-bugs", "Reportar Bugs");
        translationBuilder.add("omm.config.gui.omm-wiki", "OpenMineMap Wiki");
        translationBuilder.add("omm.error.incomplete-coordinates", "Ocurrió un error. Es probable que hayas ingresado coordenadas incompletas.");
        translationBuilder.add("omm.error.formatted-coordinates", "Ocurrió un error. Es probable que hayas ingresado coordenadas con un formato inválido.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "Ocurrió un error. Puede que hayas ingresado coordenadas inválidas o fuera de los límites.");
        translationBuilder.add("omm.error.out-of-bounds", "Ocurrió un error. Es probable que hayas ingresado coordenadas fuera de los límites.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Error al procesar las coordenadas; el jugador al que intentas teletransportarte puede estar fuera de los límites de la proyección.");
        translationBuilder.add("omm.error.cannot-find-player-start", "No se pudo encontrar al jugador ");
        translationBuilder.add("omm.error.cannot-find-player-end", " dentro del área renderizada.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: Ocurrió un error en el cambio de propiedad del Waypoint");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: Ocurrió un error al eliminar el Waypoint");
        translationBuilder.add("omm.config.state.on", "Activado");
        translationBuilder.add("omm.config.state.off", "Desactivado");
        translationBuilder.add("omm.config.state.none", "Ninguno");
        translationBuilder.add("omm.config.state.self", "Uno mismo");
        translationBuilder.add("omm.config.state.local", "Local");
        translationBuilder.add("omm.config.option.hover-names", "Nombres al pasar el cursor");
        translationBuilder.add("omm.config.tooltip.hover-names", "Mostrar los nombres de los jugadores al pasar el cursor sobre ellos en el mapa de pantalla completa.");
        translationBuilder.add("omm.waypoints.editing", "(Editando...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Configura una URL personalizada para cargar los tiles. Haz clic para mas información");
        translationBuilder.add("omm.rcm.teleport-here", "Teletransportarse Aquí");
        translationBuilder.add("omm.rcm.copy-coordinates", "Copiar Coordenadas");
        translationBuilder.add("omm.rcm.open-in", "Abrir En...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Editar Waypoint");
        translationBuilder.add("omm.rcm.set-snap-angle", "Configurar ángulo de ajuste");
        translationBuilder.add("omm.rcm.view-on-map", "Ver en el mapa");
        translationBuilder.add("omm.rcm.unpin", "Quitar Waypoint fijado");
        translationBuilder.add("omm.rcm.create-waypoint", "Crear un Waypoint");
        translationBuilder.add("omm.error.tile-url.parse", "Error al procesar la fuente de mapas");
        translationBuilder.add("omm.notification.something-wrong", "Algo salió mal");
        translationBuilder.add("omm.notification.location-copied", "Ubicación copiada al portapapeles");
        translationBuilder.add("omm.notification.searching", "Buscando...");
        translationBuilder.add("omm.notification.snap-angle-set", "Ángulo de ajuste establecido en ");
        translationBuilder.add("omm.search.anything", "Buscar cualquier cosa");
        translationBuilder.add("omm.search.blocks-away", " metros de distancia");
        translationBuilder.add("omm.search.places", "Buscando Lugares");
        translationBuilder.add("omm.search.no-results", "Sin resultados en la web");
        translationBuilder.add("omm.rcm.reverse-search", "Búsqueda inversa");
        translationBuilder.add("omm.error.distortion", "No se puede calcular la distorsión: fuera de los límites");
        translationBuilder.add("omm.text.distortion", "Distorsión:");
    }
}
class OmmArgentinianSpanishLanguageProvider extends OmmSpanishLanguageProvider {
    protected OmmArgentinianSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_ar", registryLookup);
    }
}
class OmmChileanSpanishLanguageProvider extends OmmSpanishLanguageProvider {
    protected OmmChileanSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_cl", registryLookup);
    }
}
class OmmEcuadorianSpanishLanguageProvider extends OmmSpanishLanguageProvider {
    protected OmmEcuadorianSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_ec", registryLookup);
    }
}
class OmmEuropeanSpanishLanguageProvider extends OmmSpanishLanguageProvider {
    protected OmmEuropeanSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_es", registryLookup);
    }
}
class OmmMexicanSpanishLanguageProvider extends OmmSpanishLanguageProvider {
    protected OmmMexicanSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_mx", registryLookup);
    }
}
class OmmUruguayanSpanishLanguageProvider extends OmmSpanishLanguageProvider {
    protected OmmUruguayanSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_uy", registryLookup);
    }
}
class OmmVenezuelanSpanishLanguageProvider extends OmmSpanishLanguageProvider {
    protected OmmVenezuelanSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_ve", registryLookup);
    }
}

class OmmSimplifiedChineseLanguageProvider extends FabricLanguageProvider {
    protected OmmSimplifiedChineseLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }

    // ----- SIMPLIFIED CHINESE -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "人工缩放");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "通过人为手段放大图块，使可用的缩放等级突破正常范围（可再多 6 级）。");
        translationBuilder.add("omm.osm-attribution", "© {OpenStreetMap 贡献者}");
        translationBuilder.add("omm.config.option.configure-hud", "配置小地图...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "变更小地图元素的位置与大小");
        translationBuilder.add("omm.config.category.general", "一般");
        translationBuilder.add("omm.config.gui.save-and-exit", "保存并退出");
        translationBuilder.add("omm.config.gui.exit-without-saving", "不保存并退出");
        translationBuilder.add("omm.config.option.players", "玩家");
        translationBuilder.add("omm.config.category.overlays", "覆盖物");
        translationBuilder.add("omm.config.tooltip.players", "在所有地图上显示玩家");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "反转鼠标滚轮缩放地图的方向。");
        translationBuilder.add("omm.config.option.reverse-scroll", "反转滚轮");
        translationBuilder.add("omm.config.option.zoom-strength", "缩放强度");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "每次执行缩放时的缩放变化幅度");
        translationBuilder.add("omm.config.option.snap-angle", "吸附角");
        translationBuilder.add("omm.config.tooltip.snap-angle", "设置一个可通过快捷键吸附的角度，用于辅助绘制直线。（使用 Minecraft 中的角度）");
        translationBuilder.add("omm.config.option.rcm-uses", "传送指令");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "全屏地图的右键菜单中用来传送的指令。");
        translationBuilder.add("omm.config.option.directions", "朝向");
        translationBuilder.add("omm.config.tooltip.directions", "在所有地图上显示玩家朝向");
        translationBuilder.add("omm.config.option.altitude-shading", "高度着色");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "其他玩家在你上方时会呈现白色，在你下方时则呈现黑色。");
        translationBuilder.add("omm.config.category.tile-source", "图块资源");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "鼠标：");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "玩家：");
        translationBuilder.add("omm.config.gui.previous-source", "上一个资源");
        translationBuilder.add("omm.config.gui.next-source", "下一个资源");
        translationBuilder.add("omm.config.gui.reset-to-default", "重设为默认值");
        translationBuilder.add("omm.waypoints.delete-tooltip", "常驻");
        translationBuilder.add("omm.text.name", "名称");
        translationBuilder.add("omm.text.latitude", "纬度");
        translationBuilder.add("omm.text.longitude", "经度");
        translationBuilder.add("omm.waypoints.button.create", "创建路标");
        translationBuilder.add("omm.waypoints.button.save", "保存路标");
        translationBuilder.add("omm.waypoints.button.delete", "删除路标");
        translationBuilder.add("omm.waypoints.button.edit", "编辑路标");
        translationBuilder.add("omm.waypoints.button.view", "查看路标");
        translationBuilder.add("omm.waypoints.button.pin", "标记路标");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "小地图总开关");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "停用时无法以快捷键开启小地图");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "目前已启用");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "目前已停用");
        translationBuilder.add("omm.hud.out-of-bounds", "超出范围");
        translationBuilder.add("omm.error.tile-url.start", "OpenMineMap 图块网址");
        translationBuilder.add("omm.error.tile-source-json-formatting", "格式不正确。");
        translationBuilder.add("omm.error.blank-tile-url", "检测到空白的图块网址。这可能是文件格式不正确造成的。");
        translationBuilder.add("omm.error.blank-field", "有一个以上的必要栏位为空。");
        translationBuilder.add("omm.error.source-link-invalid", "\"source_url\" 不是有效连结。");
        translationBuilder.add("omm.error.attribution-link-invalid", "\"attribution_links\" 中有一个以上的无效连结。");
        translationBuilder.add("omm.error.source-bracket-placement", "\"source_url\" 的括号位置不正确。");
        translationBuilder.add("omm.error.attribution-bracket-placement", "\"attribution\" 的括号位置不正确。");
        translationBuilder.add("omm.error.link-number-mismatch", "\"attribution_links\" 列表与 \"attribution\" 字串中的连结数量不匹配。");
        translationBuilder.add("omm.error.field-missing-x", "\"source_url\" 缺少 {x}。");
        translationBuilder.add("omm.error.field-missing-y", "\"source_url\" 缺少 {y}。");
        translationBuilder.add("omm.error.field-missing-zoom", "\"source_url\" 缺少 {z}。");
        translationBuilder.add("omm.key.open-fullscreen-map", "开启全屏地图");
        translationBuilder.add("omm.key.zoom-in", "放大（小地图）");
        translationBuilder.add("omm.key.zoom-out", "缩小（小地图）");
        translationBuilder.add("omm.key.toggle-map", "地图开关（小地图）");
        translationBuilder.add("omm.key.copy-coordinates", "复制坐标到剪贴板");
        translationBuilder.add("omm.key.snap-angle", "吸附到该角");
        translationBuilder.add("omm.key.execute.error.snap-angle", "发生错误。");
        translationBuilder.add("omm.key.execute.snap-angle", "吸附！");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "你似乎已超出投影范围。请回到现实范围再试一次。");
        translationBuilder.add("omm.key.execute.copy-coordinates", "坐标已复制到剪贴板");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "执行时发生错误。");
        translationBuilder.add("omm.fullscreen.report-bugs", "回报错误");
        translationBuilder.add("omm.config.gui.omm-wiki", "OpenMineMap 维基");
        translationBuilder.add("omm.error.incomplete-coordinates", "发生错误。你可能输入了不完整的坐标。");
        translationBuilder.add("omm.error.formatted-coordinates", "发生错误。你可能输入了格式不正确的坐标。");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "发生错误。你可能输入了无效或超出范围的坐标。");
        translationBuilder.add("omm.error.out-of-bounds", "发生错误。你可能输入了超出范围的坐标。");
        translationBuilder.add("omm.error.player-out-of-bounds", "解析坐标时发生错误；你尝试传送的玩家可能位于投影范围之外。");
        translationBuilder.add("omm.error.cannot-find-player-start", "无法在已渲染区域内找到玩家「");
        translationBuilder.add("omm.error.cannot-find-player-end", "」。");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap：路标属性修改失败");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap：路标删除失败");
        translationBuilder.add("omm.config.state.on", "开启");
        translationBuilder.add("omm.config.state.off", "关闭");
        translationBuilder.add("omm.config.state.none", "无");
        translationBuilder.add("omm.config.state.self", "自身");
        translationBuilder.add("omm.config.state.local", "附近");
        translationBuilder.add("omm.config.option.hover-names", "悬停名称");
        translationBuilder.add("omm.config.tooltip.hover-names", "在全屏地图上将鼠标悬停在玩家身上时显示玩家名称");
        translationBuilder.add("omm.waypoints.editing", "（编辑…）");
        translationBuilder.add("omm.config.tooltip.tile-source", "设置欲加载图块的网址。点击了解更多信息。");
        translationBuilder.add("omm.rcm.teleport-here", "传送至此");
        translationBuilder.add("omm.rcm.copy-coordinates", "复制坐标");
        translationBuilder.add("omm.rcm.open-in", "打开于...");
        translationBuilder.add("omm.rcm.edit-waypoint", "编辑路标");
        translationBuilder.add("omm.rcm.set-snap-angle", "设置吸附角");
        translationBuilder.add("omm.rcm.view-on-map", "在地图上查看");
        translationBuilder.add("omm.rcm.unpin", "取消标记");
        translationBuilder.add("omm.rcm.create-waypoint", "创建路标");
        translationBuilder.add("omm.error.tile-url.parse", "解析图砖来源错误");
    }
}

abstract class OmmTraditionalChineseLanguageProvider extends FabricLanguageProvider {
    protected OmmTraditionalChineseLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- TRADITIONAL CHINESE -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "人工縮放");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "透過人為手段放大圖磚，使可用的縮放等級突破正常範圍（可再多 6 級）。");
        translationBuilder.add("omm.osm-attribution", "© {開放街圖貢獻者}");
        translationBuilder.add("omm.config.option.configure-hud", "配置小地圖...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "變更小地圖元素的位置與大小");
        translationBuilder.add("omm.config.category.general", "一般");
        translationBuilder.add("omm.config.gui.save-and-exit", "儲存並退出");
        translationBuilder.add("omm.config.gui.exit-without-saving", "不儲存並退出");
        translationBuilder.add("omm.config.option.players", "玩家");
        translationBuilder.add("omm.config.category.overlays", "覆蓋物");
        translationBuilder.add("omm.config.tooltip.players", "在所有地圖上顯示玩家");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "反轉滑鼠滾輪縮放地圖的方向。");
        translationBuilder.add("omm.config.option.reverse-scroll", "反轉滾輪");
        translationBuilder.add("omm.config.option.zoom-strength", "縮放強度");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "每次執行縮放時的縮放變化幅度");
        translationBuilder.add("omm.config.option.snap-angle", "吸附角");
        translationBuilder.add("omm.config.tooltip.snap-angle", "設定一個可透過快捷鍵吸附的角度，用於輔助繪製直線。（使用 Minecraft 中的角度）");
        translationBuilder.add("omm.config.option.rcm-uses", "傳送指令");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "全螢幕地圖的右鍵選單中用來傳送的指令。");
        translationBuilder.add("omm.config.option.directions", "朝向");
        translationBuilder.add("omm.config.tooltip.directions", "在所有地圖上顯示玩家朝向");
        translationBuilder.add("omm.config.option.altitude-shading", "高度著色");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "其他玩家在你上方時會呈現白色，在你下方時則呈現黑色。");
        translationBuilder.add("omm.config.category.tile-source", "圖磚資源");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "鼠標：");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "玩家：");
        translationBuilder.add("omm.config.gui.previous-source", "上一個資源");
        translationBuilder.add("omm.config.gui.next-source", "下一個資源");
        translationBuilder.add("omm.config.gui.reset-to-default", "重設為預設值");
        translationBuilder.add("omm.waypoints.delete-tooltip", "常駐");
        translationBuilder.add("omm.text.name", "名稱");
        translationBuilder.add("omm.text.latitude", "緯度");
        translationBuilder.add("omm.text.longitude", "經度");
        translationBuilder.add("omm.waypoints.button.create", "建立路標");
        translationBuilder.add("omm.waypoints.button.save", "儲存路標");
        translationBuilder.add("omm.waypoints.button.delete", "刪除路標");
        translationBuilder.add("omm.waypoints.button.edit", "編輯路標");
        translationBuilder.add("omm.waypoints.button.view", "查看路標");
        translationBuilder.add("omm.waypoints.button.pin", "標記路標");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "小地圖總開關");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "停用時無法以快捷鍵開起小地圖");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "目前已啟用");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "目前已停用");
        translationBuilder.add("omm.hud.out-of-bounds", "超出範圍");
        translationBuilder.add("omm.error.tile-url.start", "OpenMineMap 圖磚網址");
        translationBuilder.add("omm.error.tile-source-json-formatting", "格式不正確。");
        translationBuilder.add("omm.error.blank-tile-url", "偵測到空白的圖磚網址。這可能是檔案格式不正確造成的。");
        translationBuilder.add("omm.error.blank-field", "有一個以上的必要欄位為空。");
        translationBuilder.add("omm.error.source-link-invalid", "\"source_url\" 不是有效連結。");
        translationBuilder.add("omm.error.attribution-link-invalid", "\"attribution_links\" 中有一個以上的無效連結。");
        translationBuilder.add("omm.error.source-bracket-placement", "\"source_url\" 的括號位置不正確。");
        translationBuilder.add("omm.error.attribution-bracket-placement", "\"attribution\" 的括號位置不正確。");
        translationBuilder.add("omm.error.link-number-mismatch", "\"attribution_links\" 列表與 \"attribution\" 字串中的連結數量不匹配。");
        translationBuilder.add("omm.error.field-missing-x", "\"source_url\" 缺少 {x}。");
        translationBuilder.add("omm.error.field-missing-y", "\"source_url\" 缺少 {y}。");
        translationBuilder.add("omm.error.field-missing-zoom", "\"source_url\" 缺少 {z}。");
        translationBuilder.add("omm.key.open-fullscreen-map", "開啟全螢幕地圖");
        translationBuilder.add("omm.key.zoom-in", "放大（小地圖）");
        translationBuilder.add("omm.key.zoom-out", "縮小（小地圖）");
        translationBuilder.add("omm.key.toggle-map", "地圖開關（小地圖）");
        translationBuilder.add("omm.key.copy-coordinates", "複製座標到剪貼簿");
        translationBuilder.add("omm.key.snap-angle", "吸附到該角");
        translationBuilder.add("omm.key.execute.error.snap-angle", "發生錯誤。");
        translationBuilder.add("omm.key.execute.snap-angle", "吸附！");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "你似乎已超出投影範圍。請回到現實範圍再試一次。");
        translationBuilder.add("omm.key.execute.copy-coordinates", "座標已複製到剪貼簿");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "執行時發生錯誤。");
        translationBuilder.add("omm.fullscreen.report-bugs", "回報錯誤");
        translationBuilder.add("omm.config.gui.omm-wiki", "OpenMineMap 維基");
        translationBuilder.add("omm.error.incomplete-coordinates", "發生錯誤。你可能輸入了不完整的座標。");
        translationBuilder.add("omm.error.formatted-coordinates", "發生錯誤。你可能輸入了格式不正確的座標。");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "發生錯誤。你可能輸入了無效或超出範圍的座標。");
        translationBuilder.add("omm.error.out-of-bounds", "發生錯誤。你可能輸入了超出範圍的座標。");
        translationBuilder.add("omm.error.player-out-of-bounds", "解析座標時發生錯誤；你嘗試傳送的玩家可能位於投影範圍之外。");
        translationBuilder.add("omm.error.cannot-find-player-start", "無法在已彩現區域內找到玩家「");
        translationBuilder.add("omm.error.cannot-find-player-end", "」。");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap：路標屬性修改失敗");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap：路標刪除失敗");
        translationBuilder.add("omm.config.state.on", "開啟");
        translationBuilder.add("omm.config.state.off", "關閉");
        translationBuilder.add("omm.config.state.none", "無");
        translationBuilder.add("omm.config.state.self", "自身");
        translationBuilder.add("omm.config.state.local", "附近");
        translationBuilder.add("omm.config.option.hover-names", "懸停名稱");
        translationBuilder.add("omm.config.tooltip.hover-names", "在全螢幕地圖上將滑鼠懸停在玩家身上時顯示玩家名稱");
        translationBuilder.add("omm.waypoints.editing", "（編輯...）");
        translationBuilder.add("omm.config.tooltip.tile-source", "設定欲載入圖磚的網址。點擊了解更多資訊。");
        translationBuilder.add("omm.rcm.teleport-here", "傳送至此");
        translationBuilder.add("omm.rcm.copy-coordinates", "複製座標");
        translationBuilder.add("omm.rcm.open-in", "開啟於...");
        translationBuilder.add("omm.rcm.edit-waypoint", "編輯路標");
        translationBuilder.add("omm.rcm.set-snap-angle", "設定吸附角");
        translationBuilder.add("omm.rcm.view-on-map", "在地圖上查看");
        translationBuilder.add("omm.rcm.unpin", "取消標記");
        translationBuilder.add("omm.rcm.create-waypoint", "建立路標");
        translationBuilder.add("omm.error.tile-url.parse", "解析圖磚來源錯誤");
        //translationBuilder.add("omm.notification.something-wrong", "Something went wrong");
        //translationBuilder.add("omm.notification.location-copied", "Copied location to clipboard.");
        translationBuilder.add("omm.notification.searching", "搜尋中...");
        translationBuilder.add("omm.notification.snap-angle-set", "吸附角設為 ");
        //translationBuilder.add("omm.search.anything", "Search anything...");
        translationBuilder.add("omm.search.blocks-away", " 公尺遠");
        //translationBuilder.add("omm.search.places", "Search Places");
        //translationBuilder.add("omm.search.no-results", "No web results");
        //translationBuilder.add("omm.rcm.reverse-search", "Reverse Search");
        translationBuilder.add("omm.error.distortion", "無法計算變形量：超出範圍");
        translationBuilder.add("omm.text.distortion", "變形量：");
    }
}
class OmmTraditionalHongKongChineseLanguageProvider extends OmmTraditionalChineseLanguageProvider {
    protected OmmTraditionalHongKongChineseLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_hk", registryLookup);
    }
}
class OmmTraditionalTaiwaneseChineseLanguageProvider extends OmmTraditionalChineseLanguageProvider {
    protected OmmTraditionalTaiwaneseChineseLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_tw", registryLookup);
    }
}

abstract class OmmFrenchLanguageProvider extends FabricLanguageProvider {
    protected OmmFrenchLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- FRENCH -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "Zoom artificiel");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "Le zoom artificiel permet des niveaux de zoom plus élevés que la normale (+6 niveaux) en agrandissant la taille minimale des tuiles.");
        translationBuilder.add("omm.osm-attribution", "© {Contributeurs OpenStreetMap}");
        translationBuilder.add("omm.config.option.configure-hud", "Configurer l'ATH...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Modifier le positionnement et la taille des éléments de l'ATH");
        translationBuilder.add("omm.config.category.general", "Général");
        translationBuilder.add("omm.config.gui.save-and-exit", "Sauvegarder et quitter");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Quitter sans sauvegarder");
        translationBuilder.add("omm.config.option.players", "Joueurs");
        translationBuilder.add("omm.config.category.overlays", "Calques");
        translationBuilder.add("omm.config.tooltip.players", "Afficher les joueurs sur toutes les cartes");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Inverser la molette de défilement.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Inverser le défilement");
        translationBuilder.add("omm.config.option.zoom-strength", "Intensité du zoom");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "La quantité de zoom appliquée à chaque entrée de zoom");
        translationBuilder.add("omm.config.option.snap-angle", "Angle d'accrochage");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Définir un angle sur lequel s'accrocher via une touche. Utile pour tracer des lignes droites. (Utiliser un angle Minecraft)");
        translationBuilder.add("omm.config.option.rcm-uses", "Utilisation du clic droit");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "La commande utilisée pour se téléporter via le menu clic droit en plein écran.");
        translationBuilder.add("omm.config.option.directions", "Directions");
        translationBuilder.add("omm.config.tooltip.directions", "Afficher les indicateurs de direction sur toutes les cartes");
        translationBuilder.add("omm.config.option.altitude-shading", "Ombrage d'altitude");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Colore les autres joueurs en blanc lorsqu'ils sont au-dessus de vous et en noir lorsqu'ils sont en dessous.");
        translationBuilder.add("omm.config.category.tile-source", "Source de tuiles");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Souris : ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Joueur : ");
        translationBuilder.add("omm.config.gui.previous-source", "Source précédente");
        translationBuilder.add("omm.config.gui.next-source", "Source suivante");
        translationBuilder.add("omm.config.gui.reset-to-default", "Réinitialiser par défaut");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Permanent");
        translationBuilder.add("omm.text.name", "Nom");
        translationBuilder.add("omm.text.latitude", "Latitude");
        translationBuilder.add("omm.text.longitude", "Longitude");
        translationBuilder.add("omm.waypoints.button.create", "Créer un point de repère");
        translationBuilder.add("omm.waypoints.button.save", "Sauvegarder le point de repère");
        translationBuilder.add("omm.waypoints.button.delete", "Supprimer le point de repère");
        translationBuilder.add("omm.waypoints.button.edit", "Modifier le point de repère");
        translationBuilder.add("omm.waypoints.button.view", "Voir le point de repère");
        translationBuilder.add("omm.waypoints.button.pin", "Épingler le point de repère");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "Basculer les éléments de l'ATH");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Prioritaire sur la touche de basculement");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Actuellement activé");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Actuellement désactivé");
        translationBuilder.add("omm.hud.out-of-bounds", "Hors limites");
        translationBuilder.add("omm.error.tile-url.start", "URLs de tuiles OpenMineMap");
        translationBuilder.add("omm.error.tile-source-json-formatting", "Le fichier tileSources.json est mal formaté.");
        translationBuilder.add("omm.error.blank-tile-url", "URL de tuile vide détectée. Cela peut être dû à un formatage de fichier invalide.");
        translationBuilder.add("omm.error.blank-field", "Au moins un champ requis est vide.");
        translationBuilder.add("omm.error.source-link-invalid", "L'URL source n'est pas un lien valide.");
        translationBuilder.add("omm.error.attribution-link-invalid", "Au moins un lien d'attribution n'est pas un lien valide.");
        translationBuilder.add("omm.error.source-bracket-placement", "Le placement des crochets pour l'URL source est invalide.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "Le placement des crochets pour l'attribution est invalide.");
        translationBuilder.add("omm.error.link-number-mismatch", "Nombre de liens différent entre la liste des liens d'attribution et la chaîne d'attribution.");
        translationBuilder.add("omm.error.field-missing-x", "L'URL source ne contient pas de champ X.");
        translationBuilder.add("omm.error.field-missing-y", "L'URL source ne contient pas de champ Y.");
        translationBuilder.add("omm.error.field-missing-zoom", "L'URL source ne contient pas de champ zoom.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Ouvrir la carte en plein écran");
        translationBuilder.add("omm.key.zoom-in", "Zoomer (ATH)");
        translationBuilder.add("omm.key.zoom-out", "Dézoomer (ATH)");
        translationBuilder.add("omm.key.toggle-map", "Basculer la carte (ATH)");
        translationBuilder.add("omm.key.copy-coordinates", "Copier les coordonnées dans le presse-papiers");
        translationBuilder.add("omm.key.snap-angle", "Accrocher à l'angle");
        translationBuilder.add("omm.key.execute.error.snap-angle", "Une erreur s'est produite.");
        translationBuilder.add("omm.key.execute.snap-angle", "Accroché !");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Il semble que vous soyez en dehors des limites de la projection. Veuillez revenir dans la réalité et réessayer.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Coordonnées copiées dans le presse-papiers");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "Une erreur s'est produite lors de cette action.");
        translationBuilder.add("omm.fullscreen.report-bugs", "Signaler des bugs");
        translationBuilder.add("omm.config.gui.omm-wiki", "Wiki OpenMineMap");
        translationBuilder.add("omm.error.incomplete-coordinates", "Une erreur s'est produite. Vous avez probablement entré des coordonnées incomplètes.");
        translationBuilder.add("omm.error.formatted-coordinates", "Une erreur s'est produite. Vous avez probablement entré des coordonnées avec un formatage invalide.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "Une erreur s'est produite. Vous avez peut-être entré des coordonnées invalides ou hors limites.");
        translationBuilder.add("omm.error.out-of-bounds", "Une erreur s'est produite. Vous avez probablement entré des coordonnées hors limites.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Erreur lors de l'analyse des coordonnées ; Le joueur vers lequel vous essayez de vous téléporter est peut-être hors des limites de la projection.");
        translationBuilder.add("omm.error.cannot-find-player-start", "Impossible de trouver le joueur \"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\" dans la zone rendue.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap : Échec de la modification des propriétés du point de repère");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap : Échec de la suppression du point de repère");
        translationBuilder.add("omm.config.state.on", "Activé");
        translationBuilder.add("omm.config.state.off", "Désactivé");
        translationBuilder.add("omm.config.state.none", "Aucun");
        translationBuilder.add("omm.config.state.self", "Soi-même");
        translationBuilder.add("omm.config.state.local", "Local");
        translationBuilder.add("omm.config.option.hover-names", "Afficher les noms au survol");
        translationBuilder.add("omm.config.tooltip.hover-names", "Afficher les noms des joueurs au survol de la souris sur les joueurs dans la carte en plein écran");
        translationBuilder.add("omm.waypoints.editing", "(Édition en cours...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Définir une URL personnalisée pour charger les tuiles. Cliquez pour plus d'informations.");
        translationBuilder.add("omm.rcm.teleport-here", "Se téléporter ici");
        translationBuilder.add("omm.rcm.copy-coordinates", "Copier les coordonnées");
        translationBuilder.add("omm.rcm.open-in", "Ouvrir dans...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Modifier le point de repère");
        translationBuilder.add("omm.rcm.set-snap-angle", "Définir l'angle d'accrochage");
        translationBuilder.add("omm.rcm.view-on-map", "Voir sur la carte");
        translationBuilder.add("omm.rcm.unpin", "Détacher");
        translationBuilder.add("omm.rcm.create-waypoint", "Créer un point de repère");
        translationBuilder.add("omm.error.tile-url.parse", "Erreur lors de l'analyse de la source de tuiles");
    }
}
class OmmEuropeanFrenchLanguageProvider extends OmmFrenchLanguageProvider {
    protected OmmEuropeanFrenchLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "fr_fr", registryLookup);
    }
}
class OmmCanadianFrenchLanguageProvider extends OmmFrenchLanguageProvider {
    protected OmmCanadianFrenchLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "fr_ca", registryLookup);
    }
}

abstract class OmmRussianLanguageProvider extends FabricLanguageProvider {
    protected OmmRussianLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- RUSSIAN -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "Искусственный зум");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "Искусственный зум позволяет увеличивать масштаб выше обычного (+6 уровней) за счёт увеличения минимального размера тайлов.");
        translationBuilder.add("omm.osm-attribution", "© {Участники OpenStreetMap}");
        translationBuilder.add("omm.config.option.configure-hud", "Настроить интерфейс...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Изменить расположение и размер элементов интерфейса");
        translationBuilder.add("omm.config.category.general", "Общие");
        translationBuilder.add("omm.config.gui.save-and-exit", "Сохранить и выйти");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Выйти без сохранения");
        translationBuilder.add("omm.config.option.players", "Игроки");
        translationBuilder.add("omm.config.category.overlays", "Слои");
        translationBuilder.add("omm.config.tooltip.players", "Показывать игроков на всех картах");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Инвертировать колёсико мыши.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Инвертировать прокрутку");
        translationBuilder.add("omm.config.option.zoom-strength", "Сила зума");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "Величина изменения масштаба при каждом зуме");
        translationBuilder.add("omm.config.option.snap-angle", "Угол привязки");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Установить угол, к которому можно привязаться с помощью клавиши. Помогает рисовать прямые линии. (Используйте угол Minecraft)");
        translationBuilder.add("omm.config.option.rcm-uses", "Команда ПКМ");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "Команда, которая будет использоваться для телепортации через контекстное меню в полноэкранном режиме.");
        translationBuilder.add("omm.config.option.directions", "Направления");
        translationBuilder.add("omm.config.tooltip.directions", "Показывать индикаторы направления на всех картах");
        translationBuilder.add("omm.config.option.altitude-shading", "Затенение высоты");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Окрашивает других игроков в белый цвет, когда они выше вас, и в чёрный, когда ниже.");
        translationBuilder.add("omm.config.category.tile-source", "Источник тайлов");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Мышь: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Игрок: ");
        translationBuilder.add("omm.config.gui.previous-source", "Предыдущий источник");
        translationBuilder.add("omm.config.gui.next-source", "Следующий источник");
        translationBuilder.add("omm.config.gui.reset-to-default", "Сбросить по умолчанию");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Навсегда");
        translationBuilder.add("omm.text.name", "Название");
        translationBuilder.add("omm.text.latitude", "Широта");
        translationBuilder.add("omm.text.longitude", "Долгота");
        translationBuilder.add("omm.waypoints.button.create", "Создать метку");
        translationBuilder.add("omm.waypoints.button.save", "Сохранить метку");
        translationBuilder.add("omm.waypoints.button.delete", "Удалить метку");
        translationBuilder.add("omm.waypoints.button.edit", "Редактировать метку");
        translationBuilder.add("omm.waypoints.button.view", "Показать метку");
        translationBuilder.add("omm.waypoints.button.pin", "Закрепить метку");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "Переключить элементы интерфейса");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Приоритет над клавишей переключения");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Сейчас включено");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Сейчас отключено");
        translationBuilder.add("omm.hud.out-of-bounds", "За пределами границ");
        translationBuilder.add("omm.error.tile-url.start", "URL тайлов OpenMineMap");
        translationBuilder.add("omm.error.tile-source-json-formatting", "Файл tileSources.json отформатирован неправильно.");
        translationBuilder.add("omm.error.blank-tile-url", "Обнаружен пустой URL тайла. Возможно, это связано с неправильным форматированием файла.");
        translationBuilder.add("omm.error.blank-field", "Как минимум одно обязательное поле пустое.");
        translationBuilder.add("omm.error.source-link-invalid", "URL источника не является действительной ссылкой.");
        translationBuilder.add("omm.error.attribution-link-invalid", "Как минимум одна ссылка атрибуции недействительна.");
        translationBuilder.add("omm.error.source-bracket-placement", "Неправильное размещение скобок в URL источника.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "Неправильное размещение скобок в атрибуции.");
        translationBuilder.add("omm.error.link-number-mismatch", "Несоответствие количества ссылок между списком ссылок атрибуции и строкой атрибуции.");
        translationBuilder.add("omm.error.field-missing-x", "В URL источника отсутствует поле X.");
        translationBuilder.add("omm.error.field-missing-y", "В URL источника отсутствует поле Y.");
        translationBuilder.add("omm.error.field-missing-zoom", "В URL источника отсутствует поле zoom.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Открыть полноэкранную карту");
        translationBuilder.add("omm.key.zoom-in", "Приблизить (интерфейс)");
        translationBuilder.add("omm.key.zoom-out", "Отдалить (интерфейс)");
        translationBuilder.add("omm.key.toggle-map", "Переключить карту (интерфейс)");
        translationBuilder.add("omm.key.copy-coordinates", "Копировать координаты в буфер обмена");
        translationBuilder.add("omm.key.snap-angle", "Привязать к углу");
        translationBuilder.add("omm.key.execute.error.snap-angle", "Произошла ошибка.");
        translationBuilder.add("omm.key.execute.snap-angle", "Привязано!");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Похоже, вы находитесь за пределами проекции. Пожалуйста, вернитесь в реальность и попробуйте снова.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Координаты скопированы в буфер обмена");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "Произошла ошибка при выполнении действия.");
        translationBuilder.add("omm.fullscreen.report-bugs", "Сообщить об ошибках");
        translationBuilder.add("omm.config.gui.omm-wiki", "Вики OpenMineMap");
        translationBuilder.add("omm.error.incomplete-coordinates", "Произошла ошибка. Вероятно, вы ввели неполные координаты.");
        translationBuilder.add("omm.error.formatted-coordinates", "Произошла ошибка. Вероятно, вы ввели координаты в неправильном формате.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "Произошла ошибка. Возможно, вы ввели недействительные координаты или координаты за пределами границ.");
        translationBuilder.add("omm.error.out-of-bounds", "Произошла ошибка. Вероятно, вы ввели координаты за пределами границ.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Ошибка при обработке координат; игрок, к которому вы пытаетесь телепортироваться, может находиться за пределами проекции.");
        translationBuilder.add("omm.error.cannot-find-player-start", "Не удалось найти игрока \"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\" в отрендеренной области.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: Не удалось изменить свойства метки");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: Не удалось удалить метку");
        translationBuilder.add("omm.config.state.on", "Вкл");
        translationBuilder.add("omm.config.state.off", "Выкл");
        translationBuilder.add("omm.config.state.none", "Нет");
        translationBuilder.add("omm.config.state.self", "Себя");
        translationBuilder.add("omm.config.state.local", "Локально");
        translationBuilder.add("omm.config.option.hover-names", "Показывать имена при наведении");
        translationBuilder.add("omm.config.tooltip.hover-names", "Показывать имена игроков при наведении мыши на игроков на полноэкранной карте");
        translationBuilder.add("omm.waypoints.editing", "(Редактируется...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Установите пользовательский URL для загрузки плиток. Нажмите для получения дополнительной информации.");
        translationBuilder.add("omm.rcm.teleport-here", "Телепортироваться сюда");
        translationBuilder.add("omm.rcm.copy-coordinates", "Скопировать координаты");
        translationBuilder.add("omm.rcm.open-in", "Открыть в...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Редактировать точку");
        translationBuilder.add("omm.rcm.set-snap-angle", "Установить угол привязки");
        translationBuilder.add("omm.rcm.view-on-map", "Показать на карте");
        translationBuilder.add("omm.rcm.unpin", "Открепить");
        translationBuilder.add("omm.rcm.create-waypoint", "Создать точку");
        translationBuilder.add("omm.error.tile-url.parse", "Ошибка разбора источника плиток");
    }
}
class OmmModernRussianLanguageProvider extends OmmRussianLanguageProvider {
    protected OmmModernRussianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "ru_ru", registryLookup);
    }
}
class OmmPreRevolutionaryRussianLanguageProvider extends OmmRussianLanguageProvider {
    protected OmmPreRevolutionaryRussianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "rpr", registryLookup);
    }
}

abstract class OmmGermanLanguageProvider extends FabricLanguageProvider {
    protected OmmGermanLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- GERMAN -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "Künstlicher Zoom");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "Künstlicher Zoom ermöglicht höhere Zoomstufen als normal (+6 Stufen) durch Vergrößerung der kleinsten Kachelgröße.");
        translationBuilder.add("omm.osm-attribution", "© {OpenStreetMap Mitwirkende}");
        translationBuilder.add("omm.config.option.configure-hud", "HUD konfigurieren...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Positionierung und Größe der HUD-Elemente ändern");
        translationBuilder.add("omm.config.category.general", "Allgemein");
        translationBuilder.add("omm.config.gui.save-and-exit", "Speichern und beenden");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Ohne Speichern beenden");
        translationBuilder.add("omm.config.option.players", "Spieler");
        translationBuilder.add("omm.config.category.overlays", "Overlays");
        translationBuilder.add("omm.config.tooltip.players", "Spieler auf allen Karten anzeigen");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Mausrad umkehren.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Scrollen umkehren");
        translationBuilder.add("omm.config.option.zoom-strength", "Zoom-Stärke");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "Zeit bis zum gewünschten Zoom-Level nach Zoom-Eingabe");
        translationBuilder.add("omm.config.option.snap-angle", "Einrastwinkel");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Einen Winkel festlegen, der mit einer Taste einrastet. Hilft beim Zeichnen gerader Linien. (Minecraft-Winkel verwenden)");
        translationBuilder.add("omm.config.option.rcm-uses", "RCM-Verwendung");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "Der Befehl zum Teleportieren über das Vollbild-Rechtsklick-Menü.");
        translationBuilder.add("omm.config.option.directions", "Richtungen");
        translationBuilder.add("omm.config.tooltip.directions", "Richtungsanzeigen auf allen Karten zeigen");
        translationBuilder.add("omm.config.option.altitude-shading", "Höhen-Schattierung");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Andere Spieler weiß färben wenn über dir, schwarz wenn unter dir.");
        translationBuilder.add("omm.config.category.tile-source", "Kachelquelle");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Maus: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Spieler: ");
        translationBuilder.add("omm.config.gui.previous-source", "Vorherige Quelle");
        translationBuilder.add("omm.config.gui.next-source", "Nächste Quelle");
        translationBuilder.add("omm.config.gui.reset-to-default", "Auf Standard zurücksetzen");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Permanent");
        translationBuilder.add("omm.text.name", "Name");
        translationBuilder.add("omm.text.latitude", "Breitengrad");
        translationBuilder.add("omm.text.longitude", "Längengrad");
        translationBuilder.add("omm.waypoints.button.create", "Waypoint erstellen");
        translationBuilder.add("omm.waypoints.button.save", "Waypoint speichern");
        translationBuilder.add("omm.waypoints.button.delete", "Waypoint löschen");
        translationBuilder.add("omm.waypoints.button.edit", "Waypoint bearbeiten");
        translationBuilder.add("omm.waypoints.button.view", "Waypoint ansehen");
        translationBuilder.add("omm.waypoints.button.pin", "Waypoint anheften");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "HUD-Elemente umschalten");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Übergeordnet zur Umschalt-Taste");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Aktuell aktiviert");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Aktuell deaktiviert");
        translationBuilder.add("omm.hud.out-of-bounds", "Außerhalb der Grenzen");
        translationBuilder.add("omm.error.tile-url.start", "OpenMineMap Kachel-URLs");
        translationBuilder.add("omm.error.tile-source-json-formatting", "<tileSources.json> falsch formatiert.");
        translationBuilder.add("omm.error.blank-tile-url", "Leere TileUrl erkannt. Möglicherweise falsches Dateiformat.");
        translationBuilder.add("omm.error.blank-field", "Mindestens ein Pflichtfeld leer.");
        translationBuilder.add("omm.error.source-link-invalid", "Quellen-URL ist kein gültiger Link.");
        translationBuilder.add("omm.error.attribution-link-invalid", "Mindestens ein Attributions-Link ungültig.");
        translationBuilder.add("omm.error.source-bracket-placement", "Klammerplatzierung bei Quellen-URL falsch.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "Klammerplatzierung bei Attribution falsch.");
        translationBuilder.add("omm.error.link-number-mismatch", "Unterschiedliche Link-Anzahl zwischen Attributionsliste und -String.");
        translationBuilder.add("omm.error.field-missing-x", "Quellen-URL fehlt X-Feld.");
        translationBuilder.add("omm.error.field-missing-y", "Quellen-URL fehlt Y-Feld.");
        translationBuilder.add("omm.error.field-missing-zoom", "Quellen-URL fehlt Zoom-Feld.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Vollbildkarte öffnen");
        translationBuilder.add("omm.key.zoom-in", "Hineinzoomen (HUD)");
        translationBuilder.add("omm.key.zoom-out", "Herauszoomen (HUD)");
        translationBuilder.add("omm.key.toggle-map", "Karte umschalten (HUD)");
        translationBuilder.add("omm.key.copy-coordinates", "Koordinaten in Zwischenablage kopieren");
        translationBuilder.add("omm.key.snap-angle", "Zum Winkel einrasten");
        translationBuilder.add("omm.key.execute.error.snap-angle", "Fehler aufgetreten.");
        translationBuilder.add("omm.key.execute.snap-angle", "Eingerastet!");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Du bist außerhalb der Projektionsgrenzen. Bitte kehre in die Realität zurück und versuche es erneut.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Koordinaten in Zwischenablage kopiert");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "Fehler bei der Ausführung.");
        translationBuilder.add("omm.fullscreen.report-bugs", "Bugs melden");
        translationBuilder.add("omm.config.gui.omm-wiki", "OpenMineMap Wiki");
        translationBuilder.add("omm.error.incomplete-coordinates", "Fehler aufgetreten. Wahrscheinlich unvollständige Koordinaten.");
        translationBuilder.add("omm.error.formatted-coordinates", "Fehler aufgetreten. Wahrscheinlich falsch formatierte Koordinaten.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "Fehler aufgetreten. Koordinaten ungültig oder außerhalb der Grenzen.");
        translationBuilder.add("omm.error.out-of-bounds", "Fehler aufgetreten. Koordinaten außerhalb der Grenzen.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Fehler beim Parsen der Koordinaten; Spieler außerhalb der Projektionsgrenzen.");
        translationBuilder.add("omm.error.cannot-find-player-start", "Spieler \"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\" nicht im gerenderten Bereich gefunden.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: Waypoint-Eigenschaftsänderung fehlgeschlagen");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: Waypoint-Löschung fehlgeschlagen");
        translationBuilder.add("omm.config.state.on", "An");
        translationBuilder.add("omm.config.state.off", "Aus");
        translationBuilder.add("omm.config.state.none", "Keine");
        translationBuilder.add("omm.config.state.self", "Selbst");
        translationBuilder.add("omm.config.state.local", "Lokal");
        translationBuilder.add("omm.config.option.hover-names", "Namen beim Hover anzeigen");
        translationBuilder.add("omm.config.tooltip.hover-names", "Spielernamen beim Hover über Spieler in Vollbildkarte anzeigen");
        translationBuilder.add("omm.waypoints.editing", "(Wird bearbeitet...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Benutzerdefinierte URL für Kacheln festlegen. Klick für mehr Infos.");
        translationBuilder.add("omm.rcm.teleport-here", "Hierher teleportieren");
        translationBuilder.add("omm.rcm.copy-coordinates", "Koordinaten kopieren");
        translationBuilder.add("omm.rcm.open-in", "Öffnen in...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Waypoint bearbeiten");
        translationBuilder.add("omm.rcm.set-snap-angle", "Einrastwinkel festlegen");
        translationBuilder.add("omm.rcm.view-on-map", "Auf Karte ansehen");
        translationBuilder.add("omm.rcm.unpin", "Losmachen");
        translationBuilder.add("omm.rcm.create-waypoint", "Waypoint erstellen");
        translationBuilder.add("omm.error.tile-url.parse", "Fehler beim Parsen der Kachelquelle");
    }
}
class OmmEuropeanGermanLanguageProvider extends OmmGermanLanguageProvider {
    protected OmmEuropeanGermanLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "de_de", registryLookup);
    }
}
class OmmSwissGermanLanguageProvider extends OmmGermanLanguageProvider {
    protected OmmSwissGermanLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "de_ch", registryLookup);
    }
}
class OmmAustrianGermanLanguageProvider extends OmmGermanLanguageProvider {
    protected OmmAustrianGermanLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "de_at", registryLookup);
    }
}

abstract class OmmPortugueseLanguageProvider extends FabricLanguageProvider {
    protected OmmPortugueseLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- PORTUGUESE -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "Zoom Artificial");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "O Zoom Artificial permite níveis de zoom mais altos que o normal (+6 níveis) aumentando o tamanho da menor telha.");
        translationBuilder.add("omm.osm-attribution", "© {Contribuidores do OpenStreetMap}");
        translationBuilder.add("omm.config.option.configure-hud", "Configurar HUD...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Alterar posicionamento e tamanho dos elementos do HUD");
        translationBuilder.add("omm.config.category.general", "Geral");
        translationBuilder.add("omm.config.gui.save-and-exit", "Salvar e Sair");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Sair sem Salvar");
        translationBuilder.add("omm.config.option.players", "Jogadores");
        translationBuilder.add("omm.config.category.overlays", "Sobreposições");
        translationBuilder.add("omm.config.tooltip.players", "Mostrar Jogadores em todos os mapas");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Inverter a roda do mouse.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Inverter Rolagem");
        translationBuilder.add("omm.config.option.zoom-strength", "Força do Zoom");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "A quantidade de zoom que muda para cada entrada de zoom");
        translationBuilder.add("omm.config.option.snap-angle", "Ângulo de Fixação");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Definir um ângulo que pode ser fixado usando uma tecla. Útil para fazer linhas retas. (Use um ângulo do Minecraft)");
        translationBuilder.add("omm.config.option.rcm-uses", "Uso do Menu Clique Direito");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "O comando que será usado para teleportar usando o Menu Clique Direito em Tela Cheia.");
        translationBuilder.add("omm.config.option.directions", "Direções");
        translationBuilder.add("omm.config.tooltip.directions", "Mostrar Indicadores de Direção em todos os mapas");
        translationBuilder.add("omm.config.option.altitude-shading", "Sombreamento de Altitude");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Colorir outros jogadores de branco quando acima de você e preto quando abaixo.");
        translationBuilder.add("omm.config.category.tile-source", "Fonte de Telhas");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Mouse: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Jogador: ");
        translationBuilder.add("omm.config.gui.previous-source", "Fonte Anterior");
        translationBuilder.add("omm.config.gui.next-source", "Próxima Fonte");
        translationBuilder.add("omm.config.gui.reset-to-default", "Redefinir para Padrão");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Permanente");
        translationBuilder.add("omm.text.name", "Nome");
        translationBuilder.add("omm.text.latitude", "Latitude");
        translationBuilder.add("omm.text.longitude", "Longitude");
        translationBuilder.add("omm.waypoints.button.create", "Criar Waypoint");
        translationBuilder.add("omm.waypoints.button.save", "Salvar Waypoint");
        translationBuilder.add("omm.waypoints.button.delete", "Excluir Waypoint");
        translationBuilder.add("omm.waypoints.button.edit", "Editar Waypoint");
        translationBuilder.add("omm.waypoints.button.view", "Ver Waypoint");
        translationBuilder.add("omm.waypoints.button.pin", "Fixar Waypoint");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "Alternar Elementos do HUD");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Dominante sobre a tecla de alternância");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Atualmente Ativado");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Atualmente Desativado");
        translationBuilder.add("omm.hud.out-of-bounds", "Fora dos Limites");
        translationBuilder.add("omm.error.tile-url.start", "URLs de Telhas OpenMineMap");
        translationBuilder.add("omm.error.tile-source-json-formatting", "<tileSources.json> está formatado incorretamente.");
        translationBuilder.add("omm.error.blank-tile-url", "URL de Telha vazia detectada. Isso pode ser devido a formatação de arquivo inválida.");
        translationBuilder.add("omm.error.blank-field", "Pelo menos um campo obrigatório está vazio.");
        translationBuilder.add("omm.error.source-link-invalid", "URL da Fonte não é um link válido.");
        translationBuilder.add("omm.error.attribution-link-invalid", "Pelo menos um Link de Atribuição não é um link válido.");
        translationBuilder.add("omm.error.source-bracket-placement", "Colocação de colchetes para URL da Fonte é inválida.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "Colocação de colchetes para Atribuição é inválida.");
        translationBuilder.add("omm.error.link-number-mismatch", "Número de links incompatível entre lista de Links de Atribuição e string de Atribuição.");
        translationBuilder.add("omm.error.field-missing-x", "URL da Fonte está sem campo X.");
        translationBuilder.add("omm.error.field-missing-y", "URL da Fonte está sem campo Y.");
        translationBuilder.add("omm.error.field-missing-zoom", "URL da Fonte está sem campo zoom.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Abrir Mapa em Tela Cheia");
        translationBuilder.add("omm.key.zoom-in", "Aumentar Zoom (HUD)");
        translationBuilder.add("omm.key.zoom-out", "Diminuir Zoom (HUD)");
        translationBuilder.add("omm.key.toggle-map", "Alternar Mapa (HUD)");
        translationBuilder.add("omm.key.copy-coordinates", "Copiar Coordenadas para Área de Transferência");
        translationBuilder.add("omm.key.snap-angle", "Fixar no Ângulo");
        translationBuilder.add("omm.key.execute.error.snap-angle", "Ocorreu um erro.");
        translationBuilder.add("omm.key.execute.snap-angle", "Fixado!");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Parece que você está fora dos limites da projeção. Por favor, volte à realidade e tente novamente.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Coordenadas copiadas para área de transferência");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "Houve um erro ao fazer isso.");
        translationBuilder.add("omm.fullscreen.report-bugs", "Reportar Bugs");
        translationBuilder.add("omm.config.gui.omm-wiki", "Wiki OpenMineMap");
        translationBuilder.add("omm.error.incomplete-coordinates", "Ocorreu um erro. Você provavelmente digitou coordenadas incompletas.");
        translationBuilder.add("omm.error.formatted-coordinates", "Ocorreu um erro. Você provavelmente digitou coordenadas com formatação inválida.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "Ocorreu um erro. Você pode ter digitado coordenadas inválidas ou fora dos limites.");
        translationBuilder.add("omm.error.out-of-bounds", "An error occurred. You likely entered coordinates that are out of bounds.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Error parsing coordinates; The player you are trying to teleport to may be out of bounds of the projection.");
        translationBuilder.add("omm.error.cannot-find-player-start", "Não foi possível encontrar o jogador \"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\" na área renderizada.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: Falha na alteração das propriedades do Waypoint");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: Falha na exclusão do Waypoint");
        translationBuilder.add("omm.config.state.on", "Ligado");
        translationBuilder.add("omm.config.state.off", "Desligado");
        translationBuilder.add("omm.config.state.none", "Nenhum");
        translationBuilder.add("omm.config.state.self", "Si Mesmo");
        translationBuilder.add("omm.config.state.local", "Local");
        translationBuilder.add("omm.config.option.hover-names", "Mostrar Nomes no Hover");
        translationBuilder.add("omm.config.tooltip.hover-names", "Mostrar nomes dos jogadores ao passar o mouse sobre jogadores no mapa em tela cheia");
        translationBuilder.add("omm.waypoints.editing", "(Editando...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Defina uma URL personalizada para carregar telhas. Clique para mais informações.");
        translationBuilder.add("omm.rcm.teleport-here", "Teleportar Aqui");
        translationBuilder.add("omm.rcm.copy-coordinates", "Copiar Coordenadas");
        translationBuilder.add("omm.rcm.open-in", "Abrir Em...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Editar Waypoint");
        translationBuilder.add("omm.rcm.set-snap-angle", "Definir Ângulo de Fixação");
        translationBuilder.add("omm.rcm.view-on-map", "Ver no Mapa");
        translationBuilder.add("omm.rcm.unpin", "Desfixar");
        translationBuilder.add("omm.rcm.create-waypoint", "Criar Waypoint");
        translationBuilder.add("omm.error.tile-url.parse", "Erro ao Analisar Fonte de Telhas");
    }
}
class OmmBrazilianPortugueseLanguageProvider extends OmmPortugueseLanguageProvider {
    protected OmmBrazilianPortugueseLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "pt_br", registryLookup);
    }
}
class OmmEuropeanPortugueseLanguageProvider extends OmmPortugueseLanguageProvider {
    protected OmmEuropeanPortugueseLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "pt_pt", registryLookup);
    }
}

abstract class OmmIndonesianLanguageProvider extends FabricLanguageProvider {
    protected OmmIndonesianLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- INDONESIAN -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "Zoom Buatan");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "Zoom Buatan memungkinkan level zoom lebih tinggi dari normal (+6 level) dengan memperbesar ukuran tile terkecil.");
        translationBuilder.add("omm.osm-attribution", "© {Kontributor OpenStreetMap}");
        translationBuilder.add("omm.config.option.configure-hud", "Konfigurasi HUD...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Ubah posisi dan ukuran elemen HUD");
        translationBuilder.add("omm.config.category.general", "Umum");
        translationBuilder.add("omm.config.gui.save-and-exit", "Simpan dan Keluar");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Keluar tanpa Menyimpan");
        translationBuilder.add("omm.config.option.players", "Pemain");
        translationBuilder.add("omm.config.category.overlays", "Overlay");
        translationBuilder.add("omm.config.tooltip.players", "Tampilkan Pemain di semua peta");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Balikkan roda gulir mouse.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Balikkan Gulir");
        translationBuilder.add("omm.config.option.zoom-strength", "Kekuatan Zoom");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "Jumlah perubahan zoom untuk setiap input zoom");
        translationBuilder.add("omm.config.option.snap-angle", "Sudut Snap");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Atur sudut yang dapat di-snap menggunakan tombol pintas. Berguna untuk garis lurus. (Gunakan sudut Minecraft)");
        translationBuilder.add("omm.config.option.rcm-uses", "Penggunaan RCM");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "Perintah yang digunakan untuk teleport saat menggunakan Menu Klik Kanan Layar Penuh.");
        translationBuilder.add("omm.config.option.directions", "Arah");
        translationBuilder.add("omm.config.tooltip.directions", "Tampilkan Indikator Arah di semua peta");
        translationBuilder.add("omm.config.option.altitude-shading", "Penyamaran Ketinggian");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Warnai pemain lain putih saat di atas Anda dan hitam saat di bawah Anda.");
        translationBuilder.add("omm.config.category.tile-source", "Sumber Tile");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Mouse: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Pemain: ");
        translationBuilder.add("omm.config.gui.previous-source", "Sumber Sebelumnya");
        translationBuilder.add("omm.config.gui.next-source", "Sumber Berikutnya");
        translationBuilder.add("omm.config.gui.reset-to-default", "Atur Ulang ke Default");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Permanen");
        translationBuilder.add("omm.text.name", "Nama");
        translationBuilder.add("omm.text.latitude", "Latitude");
        translationBuilder.add("omm.text.longitude", "Longitude");
        translationBuilder.add("omm.waypoints.button.create", "Buat Waypoint");
        translationBuilder.add("omm.waypoints.button.save", "Simpan Waypoint");
        translationBuilder.add("omm.waypoints.button.delete", "Hapus Waypoint");
        translationBuilder.add("omm.waypoints.button.edit", "Edit Waypoint");
        translationBuilder.add("omm.waypoints.button.view", "Lihat Waypoint");
        translationBuilder.add("omm.waypoints.button.pin", "Pin Waypoint");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "Toggle Elemen HUD");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Dominan terhadap tombol toggle");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Sedang Aktif");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Sedang Nonaktif");
        translationBuilder.add("omm.hud.out-of-bounds", "Di Luar Batas");
        translationBuilder.add("omm.error.tile-url.start", "URL Tile OpenMineMap");
        translationBuilder.add("omm.error.tile-source-json-formatting", "<tileSources.json> diformat salah.");
        translationBuilder.add("omm.error.blank-tile-url", "URL Tile kosong terdeteksi. Mungkin karena format file tidak valid.");
        translationBuilder.add("omm.error.blank-field", "Setidaknya satu field wajib kosong.");
        translationBuilder.add("omm.error.source-link-invalid", "URL Sumber bukan link valid.");
        translationBuilder.add("omm.error.attribution-link-invalid", "Setidaknya satu Link Atribusi bukan link valid.");
        translationBuilder.add("omm.error.source-bracket-placement", "Penempatan kurung untuk URL Sumber tidak valid.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "Penempatan kurung untuk Atribusi tidak valid.");
        translationBuilder.add("omm.error.link-number-mismatch", "Jumlah link tidak cocok antara daftar Link Atribusi dan string Atribusi.");
        translationBuilder.add("omm.error.field-missing-x", "URL Sumber tidak ada field X.");
        translationBuilder.add("omm.error.field-missing-y", "URL Sumber tidak ada field Y.");
        translationBuilder.add("omm.error.field-missing-zoom", "URL Sumber tidak ada field zoom.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Buka Peta Layar Penuh");
        translationBuilder.add("omm.key.zoom-in", "Zoom In (HUD)");
        translationBuilder.add("omm.key.zoom-out", "Zoom Out (HUD)");
        translationBuilder.add("omm.key.toggle-map", "Toggle Peta (HUD)");
        translationBuilder.add("omm.key.copy-coordinates", "Salin Koordinat ke Clipboard");
        translationBuilder.add("omm.key.snap-angle", "Snap ke Sudut");
        translationBuilder.add("omm.key.execute.error.snap-angle", "Terjadi kesalahan.");
        translationBuilder.add("omm.key.execute.snap-angle", "Snap!");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Anda berada di luar batas proyeksi. Silakan kembali ke realitas dan coba lagi.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Koordinat disalin ke clipboard");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "Terjadi kesalahan saat melakukan itu.");
        translationBuilder.add("omm.fullscreen.report-bugs", "Laporkan Bug");
        translationBuilder.add("omm.config.gui.omm-wiki", "Wiki OpenMineMap");
        translationBuilder.add("omm.error.incomplete-coordinates", "Terjadi kesalahan. Anda mungkin memasukkan koordinat tidak lengkap.");
        translationBuilder.add("omm.error.formatted-coordinates", "Terjadi kesalahan. Anda mungkin memasukkan koordinat dengan format tidak valid.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "Terjadi kesalahan. Koordinat mungkin tidak valid atau di luar batas.");
        translationBuilder.add("omm.error.out-of-bounds", "Terjadi kesalahan. Koordinat di luar batas.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Kesalahan parsing koordinat; Pemain tujuan teleport mungkin di luar batas proyeksi.");
        translationBuilder.add("omm.error.cannot-find-player-start", "Tidak dapat menemukan pemain \"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\" di area render.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: Gagal mengubah properti Waypoint");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: Gagal menghapus Waypoint");
        translationBuilder.add("omm.config.state.on", "Hidup");
        translationBuilder.add("omm.config.state.off", "Mati");
        translationBuilder.add("omm.config.state.none", "Tidak Ada");
        translationBuilder.add("omm.config.state.self", "Diri Sendiri");
        translationBuilder.add("omm.config.state.local", "Lokal");
        translationBuilder.add("omm.config.option.hover-names", "Tampilkan Nama Hover");
        translationBuilder.add("omm.config.tooltip.hover-names", "Tampilkan nama pemain saat hover mouse di atas pemain pada peta layar penuh");
        translationBuilder.add("omm.waypoints.editing", "(Editing...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Atur URL kustom untuk memuat tile. Klik untuk info lebih lanjut.");
        translationBuilder.add("omm.rcm.teleport-here", "Teleport ke Sini");
        translationBuilder.add("omm.rcm.copy-coordinates", "Salin Koordinat");
        translationBuilder.add("omm.rcm.open-in", "Buka Di...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Edit Waypoint");
        translationBuilder.add("omm.rcm.set-snap-angle", "Atur Sudut Snap");
        translationBuilder.add("omm.rcm.view-on-map", "Lihat di Peta");
        translationBuilder.add("omm.rcm.unpin", "Lepas Pin");
        translationBuilder.add("omm.rcm.create-waypoint", "Buat Waypoint");
        translationBuilder.add("omm.error.tile-url.parse", "Error Parsing Sumber Tile");
    }
}
class OmmModernIndonesianLanguageProvider extends OmmIndonesianLanguageProvider {
    protected OmmModernIndonesianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "id_id", registryLookup);
    }
}
class OmmPreReformIndonesianLanguageProvider extends OmmIndonesianLanguageProvider {
    protected OmmPreReformIndonesianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "qid", registryLookup);
    }
}

class OmmJapaneseLanguageProvider extends FabricLanguageProvider {
    protected OmmJapaneseLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "ja_jp", registryLookup);
    }

    // ----- JAPANESE -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "人工ズーム");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "人工ズームは通常より高いズームレベル(+6レベル)を、最小タイルサイズを拡大することで可能にします。");
        translationBuilder.add("omm.osm-attribution", "© {OpenStreetMapの貢献者}");
        translationBuilder.add("omm.config.option.configure-hud", "HUD設定...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "HUD要素の位置とサイズを変更");
        translationBuilder.add("omm.config.category.general", "一般");
        translationBuilder.add("omm.config.gui.save-and-exit", "保存して終了");
        translationBuilder.add("omm.config.gui.exit-without-saving", "保存せずに終了");
        translationBuilder.add("omm.config.option.players", "プレイヤー");
        translationBuilder.add("omm.config.category.overlays", "オーバーレイ");
        translationBuilder.add("omm.config.tooltip.players", "全マップにプレイヤーを表示");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "マウスホイールを反転。");
        translationBuilder.add("omm.config.option.reverse-scroll", "スクロール反転");
        translationBuilder.add("omm.config.option.zoom-strength", "ズーム強度");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "ズーム入力後に目的のズームレベルに達する時間");
        translationBuilder.add("omm.config.option.snap-angle", "スナップ角度");
        translationBuilder.add("omm.config.tooltip.snap-angle", "キー割り当てでスナップ可能な角度を設定。直線作成に役立ちます。(Minecraft角度を使用)");
        translationBuilder.add("omm.config.option.rcm-uses", "<右クリックメニュー> RCM使用");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "フルスクリーン右クリックメニュー使用時のテレポートコマンド。");
        translationBuilder.add("omm.config.option.directions", "方向");
        translationBuilder.add("omm.config.tooltip.directions", "全マップに方向表示を表示");
        translationBuilder.add("omm.config.option.altitude-shading", "高度シェーディング");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "あなたの上にいるプレイヤーを白、下にいるプレイヤーを黒でシェード。");
        translationBuilder.add("omm.config.category.tile-source", "タイルソース");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "マウス: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "プレイヤー: ");
        translationBuilder.add("omm.config.gui.previous-source", "前のソース");
        translationBuilder.add("omm.config.gui.next-source", "次のソース");
        translationBuilder.add("omm.config.gui.reset-to-default", "デフォルトにリセット");
        translationBuilder.add("omm.waypoints.delete-tooltip", "永続的");
        translationBuilder.add("omm.text.name", "名前");
        translationBuilder.add("omm.text.latitude", "緯度");
        translationBuilder.add("omm.text.longitude", "経度");
        translationBuilder.add("omm.waypoints.button.create", "ウェイポイント作成");
        translationBuilder.add("omm.waypoints.button.save", "ウェイポイント保存");
        translationBuilder.add("omm.waypoints.button.delete", "ウェイポイント削除");
        translationBuilder.add("omm.waypoints.button.edit", "ウェイポイント編集");
        translationBuilder.add("omm.waypoints.button.view", "ウェイポイント表示");
        translationBuilder.add("omm.waypoints.button.pin", "ウェイポイント固定");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "HUD要素切り替え");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "トグルキーより優先");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "現在有効");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "現在無効");
        translationBuilder.add("omm.hud.out-of-bounds", "範囲外");
        translationBuilder.add("omm.error.tile-url.start", "OpenMineMap タイルURL");
        translationBuilder.add("omm.error.tile-source-json-formatting", "<tileSources.json> のフォーマットが正しくありません。");
        translationBuilder.add("omm.error.blank-tile-url", "空のTileUrlが検出されました。ファイルフォーマットが無効な可能性があります。");
        translationBuilder.add("omm.error.blank-field", "必要なフィールドが1つ以上空です。");
        translationBuilder.add("omm.error.source-link-invalid", "ソースURLが有効なリンクではありません。");
        translationBuilder.add("omm.error.attribution-link-invalid", "1つ以上の帰属リンクが有効なリンクではありません。");
        translationBuilder.add("omm.error.source-bracket-placement", "ソースURLの括弧配置が無効です。");
        translationBuilder.add("omm.error.attribution-bracket-placement", "帰属の括弧配置が無効です。");
        translationBuilder.add("omm.error.link-number-mismatch", "帰属リンクリストと帰属文字列のリンク数が一致しません。");
        translationBuilder.add("omm.error.field-missing-x", "ソースURLにXフィールドがありません。");
        translationBuilder.add("omm.error.field-missing-y", "ソースURLにYフィールドがありません。");
        translationBuilder.add("omm.error.field-missing-zoom", "ソースURLにズームフィールドがありません。");
        translationBuilder.add("omm.key.open-fullscreen-map", "フルスクリーンマップを開く");
        translationBuilder.add("omm.key.zoom-in", "ズームイン(HUD)");
        translationBuilder.add("omm.key.zoom-out", "ズームアウト(HUD)");
        translationBuilder.add("omm.key.toggle-map", "マップ切り替え(HUD)");
        translationBuilder.add("omm.key.copy-coordinates", "座標をクリップボードにコピー");
        translationBuilder.add("omm.key.snap-angle", "角度にスナップ");
        translationBuilder.add("omm.key.execute.error.snap-angle", "エラーが発生しました。");
        translationBuilder.add("omm.key.execute.snap-angle", "スナップ！");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "投影範囲外にいます。現実に戻って再度お試しください。");
        translationBuilder.add("omm.key.execute.copy-coordinates", "座標がクリップボードにコピーされました");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "実行中にエラーが発生しました。");
        translationBuilder.add("omm.fullscreen.report-bugs", "バグを報告");
        translationBuilder.add("omm.config.gui.omm-wiki", "OpenMineMap Wiki");
        translationBuilder.add("omm.error.incomplete-coordinates", "エラーが発生しました。おそらく不完全な座標を入力しました。");
        translationBuilder.add("omm.error.formatted-coordinates", "エラーが発生しました。おそらく座標のフォーマットが無効です。");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "エラーが発生しました。座標が無効または範囲外の可能性があります。");
        translationBuilder.add("omm.error.out-of-bounds", "エラーが発生しました。座標が範囲外の可能性があります。");
        translationBuilder.add("omm.error.player-out-of-bounds", "座標解析エラー; テレポート先プレイヤーが投影範囲外の可能性があります。");
        translationBuilder.add("omm.error.cannot-find-player-start", "レンダリング領域内にプレイヤー\"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\"が見つかりません。");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: ウェイポイント属性変更失敗");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: ウェイポイント削除失敗");
        translationBuilder.add("omm.config.state.on", "オン");
        translationBuilder.add("omm.config.state.off", "オフ");
        translationBuilder.add("omm.config.state.none", "なし");
        translationBuilder.add("omm.config.state.self", "自分");
        translationBuilder.add("omm.config.state.local", "ローカル");
        translationBuilder.add("omm.config.option.hover-names", "ホバー名表示");
        translationBuilder.add("omm.config.tooltip.hover-names", "フルスクリーンマップでプレイヤーにマウスをホバーした時にプレイヤー名を表示");
        translationBuilder.add("omm.waypoints.editing", "(編集中...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "タイル読み込み用のカスタムURLを設定。詳細はクリック。");
        translationBuilder.add("omm.rcm.teleport-here", "ここにテレポート");
        translationBuilder.add("omm.rcm.copy-coordinates", "座標コピー");
        translationBuilder.add("omm.rcm.open-in", "...で開く");
        translationBuilder.add("omm.rcm.edit-waypoint", "ウェイポイント編集");
        translationBuilder.add("omm.rcm.set-snap-angle", "スナップ角度設定");
        translationBuilder.add("omm.rcm.view-on-map", "マップで表示");
        translationBuilder.add("omm.rcm.unpin", "ピンを外す");
        translationBuilder.add("omm.rcm.create-waypoint", "ウェイポイント作成");
        translationBuilder.add("omm.error.tile-url.parse", "タイルソース解析エラー");
    }
}

class OmmItalianLanguageProvider extends FabricLanguageProvider {
    protected OmmItalianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "it_it", registryLookup);
    }

    // ----- ITALIAN -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "Zoom Artificiale");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "Lo Zoom Artificiale permette livelli di zoom superiori al normale (+6 livelli) ingrandendo la dimensione del tile più piccolo.");
        translationBuilder.add("omm.osm-attribution", "© {Contributori di OpenStreetMap}");
        translationBuilder.add("omm.config.option.configure-hud", "Configura HUD...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "Cambia posizione e dimensione degli elementi HUD");
        translationBuilder.add("omm.config.category.general", "Generale");
        translationBuilder.add("omm.config.gui.save-and-exit", "Salva ed Esci");
        translationBuilder.add("omm.config.gui.exit-without-saving", "Esci senza Salvare");
        translationBuilder.add("omm.config.option.players", "Giocatori");
        translationBuilder.add("omm.config.category.overlays", "Overlay");
        translationBuilder.add("omm.config.tooltip.players", "Mostra Giocatori su tutte le mappe");
        translationBuilder.add("omm.config.tooltip.reverse-scroll", "Inverti la rotella del mouse.");
        translationBuilder.add("omm.config.option.reverse-scroll", "Inverti Scorrimento");
        translationBuilder.add("omm.config.option.zoom-strength", "Intensità Zoom");
        translationBuilder.add("omm.config.tooltip.zoom-strength", "Quantità zoom che cambia per ogni input zoom");
        translationBuilder.add("omm.config.option.snap-angle", "Angolo Snap");
        translationBuilder.add("omm.config.tooltip.snap-angle", "Imposta un angolo che può essere agganciato con un tasto. Utile per linee rette. (Usa angolo Minecraft)");
        translationBuilder.add("omm.config.option.rcm-uses", "Utilizzo RCM");
        translationBuilder.add("omm.config.tooltip.rcm-uses", "Il comando usato per teletrasportarsi con Menu Click Destro Schermo Pieno.");
        translationBuilder.add("omm.config.option.directions", "Direzioni");
        translationBuilder.add("omm.config.tooltip.directions", "Mostra Indicatori Direzione su tutte le mappe");
        translationBuilder.add("omm.config.option.altitude-shading", "Ombreggiatura Altitudine");
        translationBuilder.add("omm.config.tooltip.altitude-shading", "Colora altri giocatori bianchi se sopra di te, neri se sotto.");
        translationBuilder.add("omm.config.category.tile-source", "Sorgente Tile");
        translationBuilder.add("omm.fullscreen.mouse-coordinates-label", "Mouse: ");
        translationBuilder.add("omm.fullscreen.player-coordinates-label", "Giocatore: ");
        translationBuilder.add("omm.config.gui.previous-source", "Sorgente Precedente");
        translationBuilder.add("omm.config.gui.next-source", "Sorgente Successiva");
        translationBuilder.add("omm.config.gui.reset-to-default", "Ripristina Predefinito");
        translationBuilder.add("omm.waypoints.delete-tooltip", "Permanente");
        translationBuilder.add("omm.text.name", "Nome");
        translationBuilder.add("omm.text.latitude", "Latitudine");
        translationBuilder.add("omm.text.longitude", "Longitudine");
        translationBuilder.add("omm.waypoints.button.create", "Crea Waypoint");
        translationBuilder.add("omm.waypoints.button.save", "Salva Waypoint");
        translationBuilder.add("omm.waypoints.button.delete", "Elimina Waypoint");
        translationBuilder.add("omm.waypoints.button.edit", "Modifica Waypoint");
        translationBuilder.add("omm.waypoints.button.view", "Visualizza Waypoint");
        translationBuilder.add("omm.waypoints.button.pin", "Fissa Waypoint");
        translationBuilder.add("omm.fullscreen.hud-toggle.name", "Alterna Elementi HUD");
        translationBuilder.add("omm.fullscreen.hud-toggle.description", "Prevalente sul tasto alternanza");
        translationBuilder.add("omm.fullscreen.hud-toggle.enabled", "Attualmente Attivo");
        translationBuilder.add("omm.fullscreen.hud-toggle.disabled", "Attualmente Disattivo");
        translationBuilder.add("omm.hud.out-of-bounds", "Fuori Limiti");
        translationBuilder.add("omm.error.tile-url.start", "URL Tile OpenMineMap");
        translationBuilder.add("omm.error.tile-source-json-formatting", "<tileSources.json> formattato incorrettamente.");
        translationBuilder.add("omm.error.blank-tile-url", "URL Tile vuota rilevata. Forse formato file non valido.");
        translationBuilder.add("omm.error.blank-field", "Almeno un campo obbligatorio vuoto.");
        translationBuilder.add("omm.error.source-link-invalid", "URL Sorgente non è un link valido.");
        translationBuilder.add("omm.error.attribution-link-invalid", "Almeno un Link Attribuzione non valido.");
        translationBuilder.add("omm.error.source-bracket-placement", "Posizionamento parentesi URL Sorgente non valido.");
        translationBuilder.add("omm.error.attribution-bracket-placement", "Posizionamento parentesi Attribuzione non valido.");
        translationBuilder.add("omm.error.link-number-mismatch", "Numero link diverso tra lista Link Attribuzione e stringa Attribuzione.");
        translationBuilder.add("omm.error.field-missing-x", "URL Sorgente manca campo X.");
        translationBuilder.add("omm.error.field-missing-y", "URL Sorgente manca campo Y.");
        translationBuilder.add("omm.error.field-missing-zoom", "URL Sorgente manca campo zoom.");
        translationBuilder.add("omm.key.open-fullscreen-map", "Apri Mappa Schermo Pieno");
        translationBuilder.add("omm.key.zoom-in", "Zoom Avanti (HUD)");
        translationBuilder.add("omm.key.zoom-out", "Zoom Indietro (HUD)");
        translationBuilder.add("omm.key.toggle-map", "Alterna Mappa (HUD)");
        translationBuilder.add("omm.key.copy-coordinates", "Copia Coordinate negli Appunti");
        translationBuilder.add("omm.key.snap-angle", "Snap su Angolo");
        translationBuilder.add("omm.key.execute.error.snap-angle", "Errore.");
        translationBuilder.add("omm.key.execute.snap-angle", "Snap!");
        translationBuilder.add("omm.key.execute.error.out-of-bounds", "Sembri fuori dai limiti della proiezione. Torna alla realtà e riprova.");
        translationBuilder.add("omm.key.execute.copy-coordinates", "Coordinate copiate negli appunti");
        translationBuilder.add("omm.key.execute.error.copy-coordinates", "Errore durante l'operazione.");
        translationBuilder.add("omm.fullscreen.report-bugs", "Segnala Bug");
        translationBuilder.add("omm.config.gui.omm-wiki", "Wiki OpenMineMap");
        translationBuilder.add("omm.error.incomplete-coordinates", "Errore. Probabilmente coordinate incomplete.");
        translationBuilder.add("omm.error.formatted-coordinates", "Errore. Probabilmente coordinate formattazione non valida.");
        translationBuilder.add("omm.error.invalid-or-out-of-bounds", "Errore. Coordinate forse non valide o fuori limiti.");
        translationBuilder.add("omm.error.out-of-bounds", "Errore. Coordinate fuori limiti.");
        translationBuilder.add("omm.error.player-out-of-bounds", "Errore parsing coordinate; Giocatore destinazione forse fuori limiti proiezione.");
        translationBuilder.add("omm.error.cannot-find-player-start", "Giocatore \"");
        translationBuilder.add("omm.error.cannot-find-player-end", "\" non trovato nell'area renderizzata.");
        translationBuilder.add("omm.error.waypoint-property-failiure", "OpenMineMap: Modifica proprietà Waypoint fallita");
        translationBuilder.add("omm.error.waypoint-delete-failed", "OpenMineMap: Eliminazione Waypoint fallita");
        translationBuilder.add("omm.config.state.on", "Attivo");
        translationBuilder.add("omm.config.state.off", "Disattivo");
        translationBuilder.add("omm.config.state.none", "Nessuno");
        translationBuilder.add("omm.config.state.self", "Sé");
        translationBuilder.add("omm.config.state.local", "Locale");
        translationBuilder.add("omm.config.option.hover-names", "Mostra Nomi Hover");
        translationBuilder.add("omm.config.tooltip.hover-names", "Mostra nomi giocatori al passaggio mouse su giocatori nella mappa schermo pieno");
        translationBuilder.add("omm.waypoints.editing", "(In modifica...)");
        translationBuilder.add("omm.config.tooltip.tile-source", "Imposta URL personalizzata per caricare tile. Clicca per maggiori info.");
        translationBuilder.add("omm.rcm.teleport-here", "Teletrasportati Qui");
        translationBuilder.add("omm.rcm.copy-coordinates", "Copia Coordinate");
        translationBuilder.add("omm.rcm.open-in", "Apri In...");
        translationBuilder.add("omm.rcm.edit-waypoint", "Modifica Waypoint");
        translationBuilder.add("omm.rcm.set-snap-angle", "Imposta Angolo Snap");
        translationBuilder.add("omm.rcm.view-on-map", "Visualizza su Mappa");
        translationBuilder.add("omm.rcm.unpin", "Stacca");
        translationBuilder.add("omm.rcm.create-waypoint", "Crea Waypoint");
        translationBuilder.add("omm.error.tile-url.parse", "Errore Parsing Sorgente Tile");
    }
}
