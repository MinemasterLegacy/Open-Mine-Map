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
	}
}

class OmmEnglishLanguageProvider extends FabricLanguageProvider {
    protected OmmEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    // ----- ENGLISH -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
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
        translationBuilder.add("oom.config.tooltip.rcm-uses", "The command that will be used to teleport when using the Fullscreen Right Click Menu.");
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
    }
}

abstract class OmmTraditionalChineseLanguageProvider extends FabricLanguageProvider {
    protected OmmTraditionalChineseLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    // ----- TRADITIONAL CHINESE -----
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.config.option.artificial-zoom", "人工變焦");
        translationBuilder.add("omm.config.tooltip.artificial-zoom", "人工變焦可提供比正常更高的縮放等級（+6 等級），透過放大最小圖塊尺寸。");
        translationBuilder.add("omm.osm-attribution", "© {開放街圖貢獻者}");
        translationBuilder.add("omm.config.option.configure-hud", "設定 HUD...");
        translationBuilder.add("omm.config.tooltip.configure-hud", "變更 HUD 元素的位置與大小");
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
        translationBuilder.add("oom.config.tooltip.rcm-uses", "全螢幕地圖的右鍵選單中用來傳送的指令。");
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

class OmmSimplifiedChineseLanguageProvider extends FabricLanguageProvider {
    protected OmmSimplifiedChineseLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }
    // ----- SIMPLIFIED CHINESE -----

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("omm.osm-attribution", "© {OpenStreetMap 贡献者}");
    }
}