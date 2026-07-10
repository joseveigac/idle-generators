package com.ghozix.idlegenerators.neoforge;

import net.neoforged.fml.common.Mod;

import com.ghozix.idlegenerators.IdleGenerators;

@Mod(IdleGenerators.MOD_ID)
public final class IdleGeneratorsNeoForge {
    public IdleGeneratorsNeoForge() {
        // Run our common setup.
        IdleGenerators.init();
    }
}
