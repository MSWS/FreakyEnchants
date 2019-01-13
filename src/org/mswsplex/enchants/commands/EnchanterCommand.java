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
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.NBTEditor;
import org.mswsplex.enchants.utils.Sounds;
import org.mswsplex.enchants.utils.Utils;

public class EnchanterCommand implements CommandExecutor, TabCompleter {
	private CustomEnchants plugin;

	public EnchanterCommand(CustomEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = plugin.getCommand("enchanter");
		cmd.setExecutor(this);
		cmd.setPermission("customenchants.enchanter");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;

		if (args.length == 0) {
			PlayerManager.setInfo(player, "openInventory", "MainMenu");
			player.openInventory(Utils.getGui(player, "MainMenu", 0));
			player.playSound(player.getLocation(),
					Sounds.valueOf(plugin.config.getString("Sounds.OpenEnchantmentInventory.Name")).bukkitSound(),
					(float) plugin.config.getDouble("Sounds.OpenEnchantmentInventory.Volume"),
					(float) plugin.config.getDouble("Sounds.OpenEnchantmentInventory.Pitch"));
			return true;
		}
		Entity ent;
		switch (args[0].toLowerCase()) {
		case "create":
			if (!sender.hasPermission("customenchants.enchanter.create")) {
				MSG.noPerm(sender);
				return true;
			}
			ent = player.getWorld().spawnEntity(player.getLocation(),
					EntityType.valueOf(plugin.config.getString("NPC.Type")));
			ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			stand.setVisible(false);
			stand.setCustomName(MSG.color(plugin.config.getString("NPC.Name")));
			stand.setCustomNameVisible(true);
			stand.setGravity(false);
			NBTEditor.setEntityTag(ent, 1, "NoAI");
			NBTEditor.setEntityTag(ent, 1, "Silent");
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
			if (!sender.hasPermission("customenchants.enchanter.delete")) {
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
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1)
			for (String res : new String[] { "create", "delete" }) {
				if (sender.hasPermission("customenchants.enchanter." + res))
					if (res.toLowerCase().startsWith(args[0].toLowerCase()))
						result.add(res);
			}
		return result;
	}
}
