package fr.omny.guis.protocolib;


import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

public class WrapperPlayServerSignLinesUpdate extends WrapperPlayServerTileEntityData {

	private static final String[] RAW_TEXT_FIELD_NAMES = new String[] {
			"Text1", "Text2", "Text3", "Text4" };

	public WrapperPlayServerSignLinesUpdate() {
		super();
	}

	public WrapperPlayServerSignLinesUpdate(PacketContainer packet) {
		super(packet);
	}

	public void setLines(String[] strLines) {
		List<NbtBase<?>> tags = new ArrayList<>();
		for (int i = 0; i < RAW_TEXT_FIELD_NAMES.length; i++) {
			var tag = WrappedChatComponent.fromText(strLines[i]).getJson();
			tags.add(NbtFactory.of(RAW_TEXT_FIELD_NAMES[i], tag));
		}

		tags.add(NbtFactory.of("Color", "black"));
		tags.add(NbtFactory.of("GlowingText", (byte) 1));
		setNbtData(NbtFactory.ofCompound("tag", tags));
	}

}
