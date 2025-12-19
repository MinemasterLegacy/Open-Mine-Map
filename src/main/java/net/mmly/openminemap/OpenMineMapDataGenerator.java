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