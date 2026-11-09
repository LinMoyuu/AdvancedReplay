package me.jumper251.replay.filesystem.saving;


import me.jumper251.replay.ReplaySystem;
import me.jumper251.replay.filesystem.ConfigManager;
import me.jumper251.replay.replaysystem.Replay;
import me.jumper251.replay.replaysystem.data.ReplayData;
import me.jumper251.replay.utils.LogUtils;
import me.jumper251.replay.utils.fetcher.Acceptor;
import me.jumper251.replay.utils.fetcher.Consumer;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DefaultReplaySaver implements IReplaySaver {

    /** @deprecated The default location. Use getReplayFile to resolve a file in the active directory. */
    @Deprecated
    public final static File DIR = new File(ReplaySystem.getInstance().getDataFolder(), "replays");

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");

    private final File directory;
    private boolean reformatting;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    public DefaultReplaySaver() {
        this.directory = resolveDirectory(ConfigManager.getReplayDirectory());
    }

    private static File resolveDirectory(String configured) {
        if (configured != null && !configured.trim().isEmpty()) {
            try {
                Path path = Paths.get(configured);
                if (!path.isAbsolute()) {
                    throw new IOException("Expected an absolute path");
                }
                return prepareDirectory(path);
            } catch (IOException | InvalidPathException | SecurityException e) {
                ReplaySystem.getInstance().getLogger().warning(
                        "Cannot use general.replay_directory '" + configured + "': " + e.getMessage()
                                + ". Falling back to " + DIR.getAbsolutePath());
            }
        }

        try {
            return prepareDirectory(DIR.toPath().toAbsolutePath());
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Cannot use default replay directory " + DIR.getAbsolutePath(), e);
        }
    }

    private static File prepareDirectory(Path path) throws IOException {
        Files.createDirectories(path);
        try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
            Files.delete(Files.createTempFile(path, ".advancedreplay-", ".tmp"));
        }
        return path.toFile();
    }

    public File getReplayFile(String replayName) {
        return new File(directory, replayName + ".replay");
    }

    public static boolean isValidName(String replayName) {
        return NAME_PATTERN.matcher(replayName).matches();
    }

    @Override
    public void saveReplay(Replay replay) {

        File file = getReplayFile(replay.getId());

        try {
            Files.createDirectories(directory.toPath());

            try (FileOutputStream fileOut = new FileOutputStream(file);
                 GZIPOutputStream gOut = new GZIPOutputStream(fileOut);
                 ObjectOutputStream objectOut = new ObjectOutputStream(gOut)) {

                objectOut.writeObject(replay.getData());
                objectOut.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void loadReplay(String replayName, Consumer<Replay> consumer) {

        this.pool.execute(new Acceptor<Replay>(consumer) {

            @Override
            public Replay getValue() {

                File file = getReplayFile(replayName);

                try (FileInputStream fileIn = new FileInputStream(file);
                     GZIPInputStream gIn = new GZIPInputStream(fileIn);
                     ObjectInputStream objectIn = new ObjectInputStream(gIn)) {

                    ReplayData data = (ReplayData) objectIn.readObject();

                    return new Replay(replayName, data);

                } catch (ClassNotFoundException | IOException e) {
                    if (!reformatting) e.printStackTrace();
                }

                return null;
            }
        });
    }

    @Override
    public boolean replayExists(String replayName) {
        if (!isValidName(replayName)) return false;

        File file = getReplayFile(replayName);

        return file.exists();
    }

    @Override
    public void deleteReplay(String replayName) {
        File file = getReplayFile(replayName);

        if (file.exists()) file.delete();
    }

    public void reformatAll() {
        this.reformatting = true;
        File[] entries = directory.listFiles();
        if (entries != null) {
            Arrays.stream(entries)
                    .filter(file -> (file.isFile() && file.getName().endsWith(".replay")))
                    .map(File::getName)
                    .collect(Collectors.toList())
                    .forEach(file -> reformat(file.replaceAll("\\.replay", "")));
        }

        this.reformatting = false;
    }

    private void reformat(String replayName) {
        loadReplay(replayName, old -> {

            if (old == null) {
                LogUtils.log("Reformatting: " + replayName);

                try {
                    File file = getReplayFile(replayName);

                    FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream objectIn = new ObjectInputStream(fileIn);

                    ReplayData data = (ReplayData) objectIn.readObject();

                    objectIn.close();
                    fileIn.close();

                    deleteReplay(replayName);
                    saveReplay(new Replay(replayName, data));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public List<String> getReplays() {
        List<String> files = new ArrayList<>();

        File[] entries = directory.listFiles();
        if (entries != null) {
            for (File file : entries) {
                if (file.isFile() && file.getName().endsWith(".replay")) {
                    files.add(file.getName().replaceAll("\\.replay", ""));
                }
            }
        }
        return files;
    }

}
