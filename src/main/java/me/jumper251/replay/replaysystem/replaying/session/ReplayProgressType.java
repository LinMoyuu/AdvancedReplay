package me.jumper251.replay.replaysystem.replaying.session;

import me.jumper251.replay.replaysystem.replaying.Replayer;
import me.jumper251.replay.utils.VersionUtil;
import me.jumper251.replay.replaysystem.replaying.ReplayHelper;


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
            int currentTicks = replayer.getCurrentTicks();
            int duration = replayer.getReplay().getData().getDuration();
            String format = "%s    §e%s §7/ §e%s    §6%s";
            String status = replayer.isPaused() ? "§cPaused" : "§aPlaying";
            String speed = replayer.getSpeed() + "x";
            String text = String.format(format, status, formatTime(currentTicks), formatTime(duration), speed);

            ReplayHelper.sendActionBar(replayer.getWatchingPlayer(), text);
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
