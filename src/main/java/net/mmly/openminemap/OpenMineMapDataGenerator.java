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

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        //TODO
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

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        //TODO
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