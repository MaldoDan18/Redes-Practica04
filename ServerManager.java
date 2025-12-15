// ===================== ServerManager.java =====================
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerManager {
    private final int poolSize;
    private final AtomicBoolean secondaryRunning = new AtomicBoolean(false);

    public ServerManager(int poolSize) {
        this.poolSize = poolSize;
    }

    public boolean shouldStartSecondary(int activeConnections) {
        return activeConnections > poolSize / 2 && !secondaryRunning.get();
    }

    public void markSecondaryRunning() {
        secondaryRunning.set(true);
    }

    public boolean isSecondaryRunning() {
        return secondaryRunning.get();
    }
}