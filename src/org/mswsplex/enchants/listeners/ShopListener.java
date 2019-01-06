package org.mswsplex.enchants.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Sounds;
import org.mswsplex.enchants.utils.Utils;

public class ShopListener implements Listener {
	private CustomEnchants plugin;

	public ShopListener(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			return;
		String inv = PlayerManager.getString(player, "openInventory");
		if (inv == null)
			return;
		event.setCancelled(true);
		int slot = event.getSlot();
		String id = "";
		ConfigurationSection section = plugin.gui.getConfigurationSection(inv);
		for (String entry : section.getKeys(false)) {
			if (section.contains(entry + ".Slot") && section.getInt(entry + ".Slot") == slot) {
				id = entry;
				break;
			}
		}

		shop: if (PlayerManager.getInfo(player, "enchantToApply") != null) {
			Enchantment apply = plugin.getEnchantmentManager().enchants
					.get(PlayerManager.getString(player, "enchantToApply"));
			if (!event.getClickedInventory().getName().equals("container.inventory"))
				break shop;
			if (!apply.canEnchantItem(item)) {
				MSG.tell(player, MSG.getString("Enchant.Invalid", "unable to add %enchant% to item")
						.replace("%enchant%", apply.getName()));
				break shop;
			}
			plugin.getEnchantmentManager().addEnchant(item, PlayerManager.getDouble(player, "amplifier").intValue(),
					apply);
			player.playSound(player.getLocation(), Sounds.LEVEL_UP.bukkitSound(), 2, 1);
			MSG.tell(player,
					MSG.getString("Enchant.Added", "Added %enchat% %level%").replace("%enchant%", apply.getName())
							.replace("%level%", PlayerManager.getDouble(player, "amplifier").intValue() + ""));
			PlayerManager.setInfo(player, "tokens",
					PlayerManager.getDouble(player, "tokens") - PlayerManager.getDouble(player, "cost"));
			PlayerManager.removeInfo(player, "enchantToApply");
			PlayerManager.removeInfo(player, "amplifier");
			PlayerManager.removeInfo(player, "cost");
		}
		if (id.isEmpty())
			return;
		if (section.contains(id + ".NextInventory")) {
			player.openInventory(Utils.getGui(player, section.getString(id + ".NextInventory"), 0));
			player.playSound(player.getLocation(), Sounds.CLICK.bukkitSound(), 2, 1);
			PlayerManager.setInfo(player, "openInventory", section.getString(id + ".NextInventory"));
		}
		if (plugin.enchantCosts.contains(id)) {
			Enchantment ench = plugin.getEnchantmentManager().enchants.get(id);
			double cost = plugin.enchantCosts.getInt(id + "." + item.getAmount());
			if (event.getClick() == ClickType.RIGHT && ench.getMaxLevel() != 1) {
				PlayerManager.setInfo(player, id,
						PlayerManager.getInfo(player, id) == null ? 2
								: Math.max(((PlayerManager.getDouble(player, id) + 1) % (ench.getMaxLevel() + 1))
										+ ench.getStartLevel(), 1 + ench.getStartLevel()));
				PlayerManager.setInfo(player, "ignore", true);
				player.playSound(player.getLocation(), Sounds.NOTE_PLING.bukkitSound(), 2,
						(((float) (PlayerManager.getInfo(player, id) == null ? 2
								: Math.max(((PlayerManager.getDouble(player, id)) % (ench.getMaxLevel() + 1))
										+ ench.getStartLevel(), 1 + ench.getStartLevel()))
								/ ench.getMaxLevel()) * 2f));
				player.openInventory(Utils.getGui(player, inv, 0));
				PlayerManager.setInfo(player, "openInventory", inv);
				return;
			}
			if (event.getClick() == ClickType.LEFT) {
				if (PlayerManager.getDouble(player, "tokens") < cost) {
					MSG.tell(player, MSG
							.getString("Token.Insufficient",
									"&cYou have insufficient funds. (&4%total% &cof &a%cost%%c).")
							.replace("%total%", (int) Math.round(PlayerManager.getDouble(player, "tokens")) + "")
							.replace("%cost%", (int) cost + ""));
					player.playSound(player.getLocation(), Sounds.VILLAGER_NO.bukkitSound(), 2, 2);
					return;
				}

				player.playSound(player.getLocation(), Sounds.LEVEL_UP.bukkitSound(), 2, 2);
				MSG.tell(player, MSG
						.getString("Enchant.Click",
								"click item in inventory you want to enchant with %enchant% %level%")
						.replace("%enchant%", ench.getName()).replace("%level%", MSG.toRoman(item.getAmount())));
				PlayerManager.setInfo(player, "enchantToApply", id);
				PlayerManager.setInfo(player, "amplifier", item.getAmount());
				PlayerManager.setInfo(player, "cost", cost);

			}
		}

	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		String inv = PlayerManager.getString(player, "openInventory");
		if (inv == null)
			return;
		PlayerManager.removeInfo(player, "openInventory");
		if (inv.equals("MainMenu"))
			return;
		PlayerManager.removeInfo(player, "enchantToApply");
		PlayerManager.removeInfo(player, "amplifier");
		PlayerManager.removeInfo(player, "cost");
		if (PlayerManager.getInfo(player, "ignore") != null) {
			PlayerManager.removeInfo(player, "ignore");
			return;
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			player.playSound(player.getLocation(), Sounds.BAT_LOOP.bukkitSound(), 2, 2);
			player.openInventory(Utils.getGui(player, "MainMenu", 0));
			PlayerManager.setInfo(player, "openInventory", "MainMenu");
		}, 1);
	}
}
