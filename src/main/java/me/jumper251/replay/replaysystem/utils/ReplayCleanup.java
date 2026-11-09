package me.jumper251.replay.replaysystem.utils;

import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.filesystem.saving.DatabaseReplaySaver;
import me.jumper251.replay.filesystem.saving.DefaultReplaySaver;
import me.jumper251.replay.filesystem.saving.IReplaySaver;
import me.jumper251.replay.filesystem.saving.ReplaySaver;
import me.jumper251.replay.utils.LogUtils;
import org.bukkit.Bukkit;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class ReplayCleanup {

    public static void cleanupReplays() {
        IReplaySaver saver = ReplaySaver.getReplaySaver();
        if (saver == null) return;

        List<String> replays = saver.getReplays();
        Bukkit.getScheduler().runTaskAsynchronously(ReplaySystem.getInstance(),
                () -> replays.forEach(replay -> checkAndDelete(saver, replay)));
    }

    private static void checkAndDelete(IReplaySaver saver, String replay) {
        LocalDate creationDate = getCreationDate(saver, replay);
        LocalDate threshold = LocalDate.now().minusDays(ConfigManager.CLEANUP_REPLAYS);

        if (creationDate.isBefore(threshold)) {
            LogUtils.log("Replay " + replay + " has expired. Removing it...");
            saver.deleteReplay(replay);
        }
    }

    private static LocalDate getCreationDate(IReplaySaver saver, String replay) {
        if (saver instanceof DefaultReplaySaver) {
            return fromMillis(((DefaultReplaySaver) saver).getReplayFile(replay).lastModified());
        }

        if (saver instanceof DatabaseReplaySaver) {
            return fromMillis(DatabaseReplaySaver.replayCache.get(replay).getTime());
        }

        return LocalDate.now();
    }

    private static LocalDate fromMillis(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();

    }
}
