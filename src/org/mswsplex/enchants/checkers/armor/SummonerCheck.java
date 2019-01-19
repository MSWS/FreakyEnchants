package org.mswsplex.enchants.checkers.armor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class SummonerCheck implements Listener {

	private FreakyEnchants plugin;

	@SuppressWarnings("deprecation")
	public SummonerCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, run(), 0, 5);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof LivingEntity) || !(event.getDamager() instanceof LivingEntity))
			return;
		if (!Utils.allowEnchant(ent.getWorld(), "summoner"))
			return;
		for (ItemStack hand : ((LivingEntity) ent).getEquipment().getArmorContents()) {
			if (hand == null || hand.getType() == Material.AIR)
				continue;
			if (!hand.containsEnchantment(plugin.getEnchant("summoner")))
				continue;
			if (!plugin.getEnchManager().checkProbability("summoner",
					hand.getEnchantmentLevel(plugin.getEnchant("summoner"))))
				continue;
			for (int i = 0; i < plugin.getEnchManager().getBonusAmount("summoner",
					hand.getEnchantmentLevel(plugin.getEnchant("summoner"))); i++) {
				Entity t = ent.getWorld().spawnEntity(ent.getLocation(),
						EntityType.valueOf(plugin.config.getString("Summoner.EntityType")));
				if (t instanceof Creature) {
					t.setMetadata("summonedTarget", new FixedMetadataValue(plugin, event.getDamager().getUniqueId()));
					((Creature) t).setTarget((LivingEntity) event.getDamager());
				}
				t.setMetadata("summoned", new FixedMetadataValue(plugin, ent.getUniqueId()));
			}
			if (ent instanceof Player)
				MSG.sendStatusMessage((Player) ent, plugin.config.getString("Summoner.SuccessMessage"));
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		for (LivingEntity ent : event.getEntity().getWorld().getLivingEntities()) {
			if (!ent.hasMetadata("summoned"))
				continue;
			String uuid = ent.getMetadata("summoned").get(0).asString();
			if (uuid.equals(event.getEntity().getUniqueId().toString()))
				ent.remove();
		}
		if (!event.getEntity().hasMetadata("summoned"))
			return;
		event.getDrops().clear();
		event.getEntity().remove();
	}

	public BukkitRunnable run() {
		return new BukkitRunnable() {
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					for (LivingEntity ent : w.getLivingEntities()) {
						if (!ent.hasMetadata("summoned"))
							continue;
						String uuid = ent.getMetadata("summoned").get(0).asString();
						LivingEntity target = getEntity(uuid, w);
						if (target == null || target.isDead()) {
							ent.damage(ent.getHealth());
						}
						if (!ent.hasMetadata("summonedTarget"))
							continue;
						uuid = ent.getMetadata("summonedTarget").get(0).asString();
						target = getEntity(uuid, w);
						if (target == null || target.isDead()) {
							ent.remove();
							continue;
						}
						((Creature) ent).setTarget(target);
					}
				}
			}
		};
	}

	private LivingEntity getEntity(String uuid, World world) {
		for (LivingEntity e : world.getLivingEntities()) {
			if (e.getUniqueId().toString().equals(uuid))
				return e;
		}
		return null;
	}
}
