package me.untouchedodin0.plugin.queue;

import me.untouchedodin0.plugin.mines.WorldEditMine;
import org.bukkit.Bukkit;
import redempt.redlib.misc.Task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class QueueSystem {

    Queue<WorldEditMine> worldEditUpgradeMineQueue = new LinkedList<>();
    Queue<WorldEditMine> worldEditExpandMineQueue = new LinkedList<>();

    Task upgradeQueueTask;
    Task expandQueueTask;

    public void startUpgradeQueueSystem() {
        this.upgradeQueueTask = Task.syncDelayed(task1 -> {
            WorldEditMine worldEditMine = worldEditUpgradeMineQueue.poll();
            Bukkit.broadcastMessage("Found mine: " + worldEditMine + " in the queue to be upgraded!");
        }, TimeUnit.SECONDS.toMillis(5));
    }

    public void addToUpgradeQueue(WorldEditMine worldEditMine) {
        if (worldEditUpgradeMineQueue.contains(worldEditMine)) {
            Bukkit.broadcastMessage("The queue already contained this mine!");
        } else {
            worldEditUpgradeMineQueue.offer(worldEditMine);
        }
    }

    public void startExpandQueueSystem() {
        this.expandQueueTask = Task.syncDelayed(task1 -> {
            WorldEditMine worldEditMine = worldEditExpandMineQueue.poll();
            Bukkit.broadcastMessage("Found mine: " + worldEditMine + " in the queue to be upgraded!");
        }, TimeUnit.SECONDS.toMillis(5));
    }

    public void addToExpandQueue(WorldEditMine worldEditMine) {
        if (worldEditExpandMineQueue.contains(worldEditMine)) {
            Bukkit.broadcastMessage("The queue already contained this mine!");
        } else {
            worldEditExpandMineQueue.offer(worldEditMine);
        }
    }
}
