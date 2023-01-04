package fr.omny.guis.backend.sign;


import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;

import fr.omny.guis.OGui;
import fr.omny.guis.protocolib.WrapperPlayServerBlockChange;
import fr.omny.guis.protocolib.WrapperPlayServerOpenSignEditor;
import fr.omny.guis.protocolib.WrapperPlayServerSignLinesUpdate;

public class SignGUI {

	private Optional<Consumer<String>> onClose;
	private String title;
	private Predicate<String> validate;
	private Material signType;

	public SignGUI(SignGUIBuilder signGUIBuilder) {
		this.onClose = signGUIBuilder.getOnClose();
		this.title = signGUIBuilder.getTitle();
		this.signType = signGUIBuilder.getSignType();
		this.validate = signGUIBuilder.getValidate();
	}

	public void open(Player player) {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		sendSignData(player, new String[] {
				"", this.title, "", "" });

		manager.addPacketListener(new PacketAdapter(OGui.getPlugin(), ListenerPriority.NORMAL,
				PacketType.Play.Client.UPDATE_SIGN) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Player ePlayer = event.getPlayer();
				if (ePlayer != player)
					return;
				var l = player.getLocation();
				var signBlockPosition = new BlockPosition(l.getBlockX(), 0, l.getBlockZ());

				PacketContainer packet = event.getPacket();
				var blockPos = packet.getBlockPositionModifier().getValues().get(0);
				var data = packet.getStringArrays().getValues().get(0);

				if (signBlockPosition.getX() != blockPos.getX()
						|| signBlockPosition.getY() != blockPos.getY()
						|| signBlockPosition.getZ() != blockPos.getZ()) {
					return;
				}

				String string = data[0];
				if (SignGUI.this.validate.test(string)) {
					SignGUI.this.onClose.ifPresent(c -> {
						var blockTypeAt = blockPos.toVector().toLocation(player.getWorld()).getBlock()
								.getType();
						// create block
						var createBlockPacket = new WrapperPlayServerBlockChange();
						createBlockPacket.setLocation(signBlockPosition);
						createBlockPacket.setBlockData(WrappedBlockData.createData(blockTypeAt));
						createBlockPacket.sendPacket(player);
						Bukkit.getScheduler().runTask(OGui.getPlugin(), () -> c.accept(string));
					});
					manager.removePacketListener(this);
				} else {
					SignGUI.this.sendSignData(player, new String[] {
							"", SignGUI.this.title, "§cError", "" });
				}
				event.setCancelled(true);
			}
		});
	}

	private void sendSignData(Player player, String[] lines) {
		if (lines.length != 4) {
			throw new IllegalArgumentException("String line must be of length 4");
		}
		var l = player.getLocation();
		// create block
		var createBlockPacket = new WrapperPlayServerBlockChange();
		var signBlockPosition = new BlockPosition(l.getBlockX(), 0, l.getBlockZ());
		createBlockPacket.setLocation(signBlockPosition);
		createBlockPacket.setBlockData(WrappedBlockData.createData(this.signType));

		// change lines of sign
		var updateSignLines = new WrapperPlayServerSignLinesUpdate();
		updateSignLines.setLocation(signBlockPosition);
		updateSignLines.setLines(lines);

		// open sign editor
		var showSignUI = new WrapperPlayServerOpenSignEditor();
		showSignUI.setLocation(signBlockPosition);

		createBlockPacket.sendPacket(player);
		updateSignLines.sendPacket(player);
		showSignUI.sendPacket(player);
	}

}
