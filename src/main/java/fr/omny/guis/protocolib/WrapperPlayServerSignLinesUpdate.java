package fr.omny.guis.protocolib;


import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;

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
		try {
			try {

				WrappedChatComponent[] components = new WrappedChatComponent[4];
				for (int i = 0; i < 4; i++) {
					components[i] = WrappedChatComponent.fromText(strLines[i]);
				}
				handle.getChatComponentArrays().write(0, components);

			} catch (FieldAccessException fae) {
				fae.printStackTrace();

				NbtCompound compound = (NbtCompound) this.getNbtData();

				for (int i = 0; i < RAW_TEXT_FIELD_NAMES.length; i++) {
					compound.put(RAW_TEXT_FIELD_NAMES[i], strLines[i]);
				}

				this.setNbtData(compound);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
