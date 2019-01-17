package org.mswsplex.enchants.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.NBTEditor;
import org.mswsplex.enchants.utils.Utils;

public class EnchanterCommand implements CommandExecutor, TabCompleter {
	private FreakyEnchants plugin;

	public EnchanterCommand(FreakyEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = plugin.getCommand("enchanter");
		cmd.setExecutor(this);
		cmd.setPermission("freakyenchants.enchanter");
		cmd.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (args.length == 0) {
			PlayerManager.setInfo(player, "openInventory", "MainMenu");
			player.openInventory(Utils.getGui(player, "MainMenu", 0));
			Utils.playSound(plugin.config, "Sounds.OpenEnchantmentInventory", player);
			return true;
		}
		Entity ent;
		switch (args[0].toLowerCase()) {
		case "create":
			if (!sender.hasPermission("freakyenchants.enchanter.create")) {
				MSG.noPerm(sender);
				return true;
			}
			ent = player.getLocation().getWorld().spawnEntity(player.getLocation(),
					EntityType.valueOf(plugin.config.getString("NPC.Type")));
			NBTEditor.setEntityTag(ent, 1, "NoAI");
			NBTEditor.setEntityTag(ent, 1, "Silent");
			ArmorStand stand = (ArmorStand) player.getLocation().getWorld().spawnEntity(
					player.getLocation().clone().add(0, Utils.getEntityHeight(ent.getType()) - 2, 0),
					EntityType.ARMOR_STAND);
			stand.setVisible(false);
			stand.setCustomName(MSG.color(plugin.config.getString("NPC.Name")));
			stand.setCustomNameVisible(true);
			stand.setGravity(false);
			int pos = 0;
			while (plugin.data.contains("NPC." + pos))
				pos++;
			plugin.data.set("NPC." + pos, player.getLocation());
			ent.setMetadata("isNPC", new FixedMetadataValue(plugin, pos));
			ent.setMetadata("holoID", new FixedMetadataValue(plugin, stand.getUniqueId() + ""));
			MSG.tell(player, MSG.getString("NPC.Spawned", "NPC Spawned"));
			break;
		case "remove":
		case "delete":
			if (!sender.hasPermission("freakyenchants.enchanter.delete")) {
				MSG.noPerm(sender);
				return true;
			}
			Entity closest = null;
			double dist = 0;
			for (Entity e : player.getWorld().getEntities()) {
				if (!e.hasMetadata("isNPC"))
					continue;
				double d = e.getLocation().distanceSquared(player.getLocation());
				if (closest == null || dist == 0 || d <= dist) {
					closest = e;
					dist = d;
				}
			}
			if (closest == null) {
				MSG.tell(player, MSG.getString("NPC.Error", "unknown NPC"));
				return true;
			}
			MSG.tell(player, MSG.getString("NPC.Deleted", "NPC deleted"));
			plugin.data.set("NPC." + closest.getMetadata("isNPC").get(0).asString(), null);
			Utils.getEntity(closest.getMetadata("holoID").get(0).asString(), player.getWorld()).remove();
			;
			closest.remove();
			break;
		case "settype":
			if (!sender.hasPermission("freakyenchants.enchanter.settype")) {
				MSG.noPerm(sender);
				return true;
			}
			EntityType type = EntityType.valueOf(args[1].toUpperCase());
			plugin.config.set("NPC.Type", type + "");
			plugin.saveConfig();
			plugin.refreshNPCs();
			break;
		}
		plugin.saveData();
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1)
			for (String res : new String[] { "create", "delete", "settype" }) {
				if (sender.hasPermission("freakyenchants.enchanter." + res))
					if (res.toLowerCase().startsWith(args[0].toLowerCase()))
						result.add(res);
			}
		if (args.length == 2 && args[0].equalsIgnoreCase("settype")) {
			for (EntityType type : EntityType.values()) {
				if (!type.isAlive() || !type.isSpawnable())
					continue;
				if (type.toString().toLowerCase().startsWith(args[1].toLowerCase()))
					result.add(MSG.camelCase(type.toString()).replace(" ", "_"));
			}
		}
		return result;
	}
}
