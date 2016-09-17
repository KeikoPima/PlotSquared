package com.plotsquared.nukkit.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.generator.GeneratorWrapper;
import com.intellectualcrafters.plot.object.PlotArea;
import com.plotsquared.nukkit.generator.NukkitPlotGenerator;
import java.lang.reflect.Field;
import java.util.HashMap;

public class WorldEvents implements Listener {

    private final Field instance;

    public WorldEvents() {
        try {
            this.instance = Level.class.getDeclaredField("generatorInstance");
            this.instance.setAccessible(true);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldLoad(LevelLoadEvent event) {
        handle(event.getLevel());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldInit(LevelInitEvent event) {
        handle(event.getLevel());
    }

    private void handle(Level level) {
        String name = level.getName();
        try {
            Generator gen = (Generator) instance.get(level);
            if (gen instanceof GeneratorWrapper) {
                PS.get().loadWorld(name, (GeneratorWrapper<?>) gen);
            } else {
                HashMap<String, Object> settings = new HashMap<>();
                settings.put("world", level.getName());
                settings.put("generator", gen);
                PS.get().loadWorld(name, new NukkitPlotGenerator(settings));
                for (PlotArea area : PS.get().getPlotAreas(name)) {
                    area.MAX_BUILD_HEIGHT = Math.min(127, area.MAX_BUILD_HEIGHT);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
