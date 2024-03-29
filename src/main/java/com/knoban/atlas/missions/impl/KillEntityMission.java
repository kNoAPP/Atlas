package com.knoban.atlas.missions.impl;

import com.google.firebase.database.DatabaseReference;
import com.knoban.atlas.missions.Mission;
import com.knoban.atlas.missions.MissionInfo;
import com.knoban.atlas.missions.bossbar.BossBarAnimationHandler;
import com.knoban.atlas.missions.bossbar.BossBarConstant;
import com.knoban.atlas.utils.Tools;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@MissionInfo(name = "kill-entity")
public class KillEntityMission extends Mission {

    private final EntityType entityType;

    public KillEntityMission(@NotNull Plugin plugin, @NotNull BossBarAnimationHandler animationHandler,
                             @NotNull DatabaseReference reference, @NotNull String uuid,
                             @NotNull Map<String, Object> missionData) {
        super(plugin, animationHandler, reference, uuid, missionData);
        String entityName = (String) extraData.getOrDefault("entity", "ZOMBIE");
        entityName = entityName.toUpperCase();
        this.entityType = EntityType.valueOf(entityName); // It's okay and expected this may throw. Error is caught from caller.
        this.material = Material.getMaterial(entityType.name() + "_SPAWN_EGG");
        if(material == null)
            throw new IllegalArgumentException("Invalid entity egg: " + entityType.name() + "_SPAWN_EGG");
        String humanName = Tools.enumNameToHumanReadable(entityName);
        String grammarName = maxProgress == 1 ? humanName : humanName + "s";
        this.display = "§4Kill " + maxProgress + " §c" + grammarName;
        this.description = new String[]{"§4Let the slaughter begin!"};

        bossBarInformation.setTitle(display + " §7(§c" + BossBarConstant.PROGRESS_LEFT + " left§7)");
        bossBarInformation.setStyle(BarStyle.SEGMENTED_6);
        bossBarInformation.setColor(BarColor.RED);
        bossBarInformation.setFlags(); // No flags
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        if(e.getEntityType() != entityType)
            return;

        Player killer = e.getEntity().getKiller();
        if(killer != null)
            incrementProgress(killer, 1L);
    }
}
