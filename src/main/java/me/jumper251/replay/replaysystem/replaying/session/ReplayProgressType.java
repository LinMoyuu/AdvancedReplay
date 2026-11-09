package me.jumper251.replay.replaysystem.replaying.session;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.jumper251.replay.replaysystem.replaying.Replayer;
import me.jumper251.replay.utils.VersionUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;


public enum ReplayProgressType implements ReplayProgression {

    XP_BAR {
        @Override
        public void update(Replayer replayer) {
            int currentTicks = replayer.getCurrentTicks();
            int level = currentTicks / 20;
            float percentage = (float) currentTicks / replayer.getReplay().getData().getDuration();

            replayer.getWatchingPlayer().setLevel(level);
            replayer.getWatchingPlayer().setExp(percentage);
        }
    },
    ACTION_BAR {
        @Override
        public void update(Replayer replayer) {
            boolean legacyChatPacket = VersionUtil.isCompatible(VersionUtil.VersionEnum.V1_8);
            boolean legacyComponents = VersionUtil.isCompatible(VersionUtil.VersionEnum.V1_12);
            if (!legacyChatPacket && !legacyComponents && VersionUtil.isBelow(VersionUtil.VersionEnum.V1_20)) return;

            int currentTicks = replayer.getCurrentTicks();
            int duration = replayer.getReplay().getData().getDuration();
            String format = "%s    §e%s §7/ §e%s    §6%s";
            String status = replayer.isPaused() ? "§cPaused" : "§aPlaying";
            String speed = replayer.getSpeed() + "x";

            String message = String.format(format, status, formatTime(currentTicks), formatTime(duration), speed);
            Player watcher = replayer.getWatchingPlayer();
            if (legacyChatPacket) {
                ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
                PacketContainer packet = protocol.createPacket(PacketType.Play.Server.CHAT);
                packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
                // Position 2 displays chat packets in the action bar on 1.8.
                packet.getBytes().write(0, (byte) 2);
                protocol.sendServerPacket(watcher, packet);
            } else if (legacyComponents) {
                watcher.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            } else {
                BaseComponent component = TextComponent.fromLegacy(message);
                watcher.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            }
        }
    },
    NONE {
        @Override
        public void update(Replayer replayer) {
        }
    };


    public static ReplayProgressType getDefault() {
        if (VersionUtil.isAbove(VersionUtil.VersionEnum.V1_21)) {
            return ACTION_BAR;
        } else {
            return XP_BAR;
        }
    }

    private static String formatTime(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        // Format the time in the format mm:ss
        return String.format("%02d:%02d", minutes, seconds);
    }

}
