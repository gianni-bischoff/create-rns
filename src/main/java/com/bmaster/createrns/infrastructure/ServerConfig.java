package com.bmaster.createrns.infrastructure;

import com.bmaster.createrns.CreateRNS;
import net.minecraft.SharedConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = CreateRNS.MOD_ID)
public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ------------------------------------------------ Config values ----------------------------------------------- //
    private static final ModConfigSpec.DoubleValue MINER_MK1_SPEED_CV = BUILDER
            .comment(" How many mining operations a miner mk1 can complete in one hour\n" +
                    " at 256 RPM, with one deposit block claimed, and no deposit multipliers.\n" +
                    " Valid range: 0.1 to 10000.0 (default: 45.0)")
            .defineInRange("minerMk1Speed", 45.0, 0.1, 10000.0);

    private static final ModConfigSpec.DoubleValue MINER_MK2_SPEED_CV = BUILDER
            .comment(" How many mining operations a miner mk2 can complete in one hour\n" +
                    " at 256 RPM, with one deposit block claimed, and no deposit multipliers.\n" +
                    " Valid range: 0.1 to 10000.0 (default: 90.0)")
            .defineInRange("minerMk2Speed", 90.0, 0.1, 10000.0);

    private static final ModConfigSpec.DoubleValue MINER_MK1_YIELD_MULTIPLIER_CV = BUILDER
            .comment(" Yield multiplier for the miner mk1.\n" +
                    " This multiplies with the deposit's yield multiplier.\n" +
                    " Example: 1.0 = normal yield, 2.0 = double yield, 0.5 = half yield\n" +
                    " Valid range: 0.01 to 100.0 (default: 1.0)")
            .defineInRange("minerMk1YieldMultiplier", 1.0, 0.01, 100.0);

    private static final ModConfigSpec.DoubleValue MINER_MK2_YIELD_MULTIPLIER_CV = BUILDER
            .comment(" Yield multiplier for the miner mk2.\n" +
                    " This multiplies with the deposit's yield multiplier.\n" +
                    " Example: 1.0 = normal yield, 2.0 = double yield, 0.5 = half yield\n" +
                    " Valid range: 0.01 to 100.0 (default: 1.0)")
            .defineInRange("minerMk2YieldMultiplier", 1.0, 0.01, 100.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    // ------------------------------------------------ Baked values ------------------------------------------------ //
    public static int minerMk1BaseProgress;
    public static int minerMk2BaseProgress;
    public static float minerMk1YieldMultiplier;
    public static float minerMk2YieldMultiplier;

    // -------------------------------------------------------------------------------------------------------------- //
    @SubscribeEvent
    static void onLoadReload(ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Unloading) return;
        if (event.getConfig().getSpec() != ServerConfig.SPEC) return;

        CreateRNS.LOGGER.info("Loading Create RNS server config...");

        // Log config event type
        if (event instanceof ModConfigEvent.Loading) {
            CreateRNS.LOGGER.info("Config event type: Loading (initial load)");
        } else if (event instanceof ModConfigEvent.Reloading) {
            CreateRNS.LOGGER.info("Config event type: Reloading (file changed or /reload command)");
        }

        // Get raw config values
        double mk1Speed = MINER_MK1_SPEED_CV.get();
        double mk2Speed = MINER_MK2_SPEED_CV.get();
        float mk1Yield = MINER_MK1_YIELD_MULTIPLIER_CV.get().floatValue();
        float mk2Yield = MINER_MK2_YIELD_MULTIPLIER_CV.get().floatValue();

        // Log loaded values
        CreateRNS.LOGGER.info("Loaded config values:");
        CreateRNS.LOGGER.info("  minerMk1Speed: {} operations/hour", mk1Speed);
        CreateRNS.LOGGER.info("  minerMk2Speed: {} operations/hour", mk2Speed);
        CreateRNS.LOGGER.info("  minerMk1YieldMultiplier: {}x", mk1Yield);
        CreateRNS.LOGGER.info("  minerMk2YieldMultiplier: {}x", mk2Yield);

        // Calculate baked values
        var ticksPerHour = 60 * SharedConstants.TICKS_PER_MINUTE;
        minerMk1BaseProgress = 256 * ticksPerHour / (int) mk1Speed;
        minerMk2BaseProgress = 256 * ticksPerHour / (int) mk2Speed;
        minerMk1YieldMultiplier = mk1Yield;
        minerMk2YieldMultiplier = mk2Yield;

        // Log calculated values
        CreateRNS.LOGGER.info("Calculated baked values:");
        CreateRNS.LOGGER.info("  minerMk1BaseProgress: {} ticks", minerMk1BaseProgress);
        CreateRNS.LOGGER.info("  minerMk2BaseProgress: {} ticks", minerMk2BaseProgress);

        CreateRNS.LOGGER.info("Create RNS server config loaded successfully");
    }
}
