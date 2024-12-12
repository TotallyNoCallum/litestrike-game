package gg.litestrike.game;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import gg.litestrike.game.LSItem.ItemCategory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class ShopListener implements Listener {

	@EventHandler
	public void openShop(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Shop s = Shop.getShop(p);
		if (event.getAction() == RIGHT_CLICK_AIR || event.getAction() == RIGHT_CLICK_BLOCK) {
			if (p.getInventory().getItemInMainHand().getType() == Material.EMERALD) {
				p.openInventory(s.currentView);
				s.setItems(s.shopItems);
				s.setDefuser();
			}
		}
	}

	@EventHandler
	public void buyItem(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		Shop s = Shop.getShop(p);
		if (event.getCurrentItem() == null) {
			return;
		}
		if (event.getInventory() != s.currentView) {
			return;
		}
		event.setCancelled(true);
		GameController gc = Litestrike.getInstance().game_controller;

		if (event.isRightClick()) {
			undoBuy((Player) event.getWhoClicked(), event.getSlot());
			return;
		}

		PlayerData pd = Litestrike.getInstance().game_controller.getPlayerData(p);

		for (LSItem lsitem : s.shopItems) {
			if (lsitem.slot == null || lsitem.slot != event.getSlot()) {
				continue;
			}
			if (lsitem.slot == Shop.DEFUSER_SLOT && gc.teams.get_team(p) != Team.Breaker) {
				continue;
			}

			// if the item is not ammuntion and also not a consumable and we already have
			// it, then we cant but it
			if (lsitem.categ != LSItem.ItemCategory.Ammunition && lsitem.categ != LSItem.ItemCategory.Consumable
					&& s.alreadyHasThis(lsitem.item)) {
				p.sendMessage(Component.text("You already have this item").color(RED));
				p.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1, 1));
				return;
			}
			// check that we have enough money
			if (!gc.getPlayerData(p).removeMoney(lsitem.price)) {
				p.sendMessage(Component.text("Cant afford this").color(RED));
				p.playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1, 1));
				return;
			}

			// remove items of same categ from inv
			if (lsitem.categ != ItemCategory.Ammunition && lsitem.categ != ItemCategory.Consumable
					&& lsitem.categ != ItemCategory.Armor) {
				for (int i = 0; i <= 40; i++) {
					ItemStack it = p.getInventory().getItem(i);
					if (it == null) {
						continue;
					}
					if (LSItem.getItemCategory(it) == lsitem.categ) {
						p.getInventory().clear(i);
					}
				}
			}

			if (lsitem.categ == ItemCategory.Armor) {
				p.getInventory().setChestplate(lsitem.item);
			} else {
				p.getInventory().addItem(lsitem.item);
			}
			p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 5));
			s.updateTitle(lsitem);
			s.buyHistory.add(lsitem);
			return;
		}
	}

	public void undoBuy(Player p, int slot) {

		Shop s = Shop.getShop(p);
		GameController gc = Litestrike.getInstance().game_controller;
		ItemStack ite = null;
		LSItem lsitem = null;
		Integer invSlot = null;

		for (LSItem lsi : s.shopItems) {
			// find corresponding LSItem to the item clicked by slot
			p.sendMessage(lsi.item.displayName());

			if(lsi.slot == null){
				continue;
			}
			p.sendMessage("lsi.slot: "+lsi.slot);
			p.sendMessage("slot: " +slot);
			if (lsi.slot.equals(slot)) {
				lsitem = lsi;
				break;
			}

		}
		if (lsitem == null) {
			return;
		}
		p.sendMessage("lsitem isn't null");
		// go through the players inv and find the item we want to sell
		for (int i = 0; i <= 40; i++) {
			ite = p.getInventory().getItem(i);

			if(ite == null){
				continue;
			}

			Integer lsitemData;
			Integer iteData;

			if(lsitem.item.hasItemMeta()){
				if(lsitem.item.getItemMeta().hasCustomModelData()) {
					lsitemData = lsitem.item.getItemMeta().getCustomModelData();
				}else {
					lsitemData = null;
				}
			}else{
				lsitemData = null;
			}

			if(ite.hasItemMeta()){
				if(ite.getItemMeta().hasCustomModelData()) {
					iteData = ite.getItemMeta().getCustomModelData();
				}else {
					iteData = null;
				}
			}else{
				iteData = null;
			}

			if (lsitem.item.getType() == ite.getType() && Objects.equals(iteData, lsitemData)) {
				invSlot = i;
				break;
			}

			if (i == 40) {
				return;
			}
		}

		if (invSlot == null) {
			return;
		}
		p.sendMessage("for loop complete invSlot isn't null");
		// go through the buyHistory and find and LSItem that has the same category but isn't the same item
		LSItem hisitem = null;
		for (int j = s.buyHistory.size(); j > 1; j--) {
			hisitem = s.buyHistory.get(j);
			if (hisitem.categ == lsitem.categ && hisitem.item != lsitem.item) {
				break;
			}
		}

		int amt = lsitem.item.getAmount();
		int amount = ite.getAmount();
		ItemStack stack = null;
		
		if (hisitem == null) {
			stack =  Shop.getBasicKid(lsitem.categ, p);
			// if we don't find any buys in the history we give the player the basic kid
		} else if (lsitem.categ == ItemCategory.Consumable || lsitem.categ == ItemCategory.Ammunition){
			if (lsitem.slot == 50 && ite.getAmount() == 6) {
				return;
			}
			stack = new ItemStack(lsitem.item.getType(), amount - amt);
		} else{
			stack = hisitem.item;
		}
		p.sendMessage("completed initializing stack");
		if(stack == null){
			return;
		}

		p.getInventory().setItem(invSlot, stack);
		gc.getPlayerData(p).addMoney(lsitem.price, "for selling an Item!");
		s.updateTitle(lsitem);
		p.playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.AMBIENT, 1, 3));
	}
}
