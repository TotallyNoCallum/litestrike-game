package gg.litestrike.game;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;

public class ProtocolLibLib {

	public static PacketAdapter make_allys_glow() {
		return new PacketAdapter(Litestrike.getInstance(), PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				GameController gc = Litestrike.getInstance().game_controller;
				if (gc == null) {
					return;
				}
				PacketContainer packet = event.getPacket();
				Player updated_player = null;
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getEntityId() == packet.getIntegers().read(0)) {
						updated_player = player;
						break;
					}
				}
				if (updated_player == null) {
					return;
				}
				if (gc.teams.get_team(updated_player) != gc.teams.get_team(event.getPlayer())) {
					return;
				}

				event.setPacket(packet = packet.deepClone());
				List<WrappedDataValue> wrappedData = packet.getDataValueCollectionModifier().read(0);
				for (WrappedDataValue wdv : wrappedData) {
					if (wdv.getIndex() == 0) {
						byte b = (byte) wdv.getValue();
						b |= 0b01000000;
						wdv.setValue(b);
					}
				}
			}
		};
	}

	public static PacketAdapter change_bomb_carrier_armor_color() {
		return new PacketAdapter(Litestrike.getInstance(), PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				GameController gc = Litestrike.getInstance().game_controller;
				if (gc == null) {
					return;
				}
				PacketContainer packet = event.getPacket();
				Player updated_player = null;
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getEntityId() == packet.getIntegers().read(0)) {
						updated_player = player;
						break;
					}
				}
				if (updated_player == null) {
					return;
				}
				if (gc.teams.get_team(updated_player) != gc.teams.get_team(event.getPlayer())) {
					return;
				}
				if (!(gc.bomb instanceof InvItemBomb)) {
					return;
				}
				if (!(updated_player.getInventory().equals(((InvItemBomb) gc.bomb).p_inv))) {
					return;
				}
				event.setPacket(packet = packet.deepClone());
				for (var slot : packet.getSlotStackPairLists().read(0)) {
					ItemStack stack = slot.getSecond();
					if (stack != null && stack.getType().name().contains("LEATHER")) {
						LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
						meta.setColor(Color.fromRGB(0xFF5733));
						stack.setItemMeta(meta);
					}
				}
			}
		};
	};
}
