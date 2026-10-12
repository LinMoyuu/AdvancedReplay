package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * Wrapper for the chat packet, which also carries the action bar.
 * <p>
 * The position is a byte before 1.12 and a {@link EnumWrappers.ChatType} from 1.12 on, so both
 * accessors are exposed and each one only writes when the running version has that field.
 */
public class WrapperPlayServerChat extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CHAT;

    /**
     * Position value used before 1.12 to display the message above the hotbar (action bar).
     */
    public static final byte POSITION_ACTION_BAR = 2;

    public WrapperPlayServerChat() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerChat(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Message.
     *
     * @return The current Message
     */
    public WrappedChatComponent getMessage() {
        return handle.getChatComponents().read(0);
    }

    /**
     * Set Message.
     *
     * @param value - new value.
     */
    public void setMessage(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }

    /**
     * Retrieve the position as an enum, which is how 1.12 and above store it.
     *
     * @return The current position, or null when the packet has no such field.
     */
    public EnumWrappers.ChatType getPosition() {
        return handle.getChatTypes().readSafely(0);
    }

    /**
     * Set the position as an enum, which is how 1.12 and above store it.
     *
     * @param value - new value.
     */
    public void setPosition(EnumWrappers.ChatType value) {
        handle.getChatTypes().writeSafely(0, value);
    }

    /**
     * Retrieve the position as a byte, which is how versions before 1.12 store it.
     *
     * @return The current position
     */
    public byte getPositionByte() {
        return handle.getBytes().read(0);
    }

    /**
     * Set the position as a byte, which is how versions before 1.12 store it.
     *
     * @param value - new value.
     */
    public void setPositionByte(byte value) {
        handle.getBytes().writeSafely(0, value);
    }

}
