package com.carrot123.eternal_career.mixin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Prevents compatibility mixins from loading when their target mod is absent.
 * Add every future compat package prefix to {@link #REQUIRED_MODS}.
 */
public final class EternalCareerMixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, String> REQUIRED_MODS = Map.of(
            ".compat.irons_spellbooks.", "irons_spellbooks",
            ".compat.goety.", "goety",
            ".compat.cataclysm.", "cataclysm",
            ".compat.legendary_monsters.", "legendary_monsters"
    );

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return REQUIRED_MODS.entrySet().stream()
                .filter(entry -> mixinClassName.contains(entry.getKey()))
                .findFirst()
                .map(entry -> FMLLoader.getLoadingModList().getModFileById(entry.getValue()) != null)
                .orElse(true);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                         IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                          IMixinInfo mixinInfo) {
    }
}
