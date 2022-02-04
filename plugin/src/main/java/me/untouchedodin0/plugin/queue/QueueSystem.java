/*
MIT License

Copyright (c) 2021 - 2022 Kyle Hicks

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package me.untouchedodin0.plugin.queue;

import me.untouchedodin0.plugin.mines.Mine;
import redempt.redlib.misc.Task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class QueueSystem {

    Queue<Mine> worldEditUpgradeMineQueue = new LinkedList<>();
    Queue<Mine> worldEditExpandMineQueue = new LinkedList<>();

    Task upgradeQueueTask;
    Task expandQueueTask;

    public void startUpgradeQueueSystem() {
        this.upgradeQueueTask = Task.syncDelayed(task1 -> {
            Mine worldEditMine = worldEditUpgradeMineQueue.poll();
            //Bukkit.broadcastMessage("Found mine: " + worldEditMine + " in the queue to be upgraded!");
        }, TimeUnit.SECONDS.toMillis(5));
    }

    public void addToUpgradeQueue(Mine worldEditMine) {
        if (worldEditUpgradeMineQueue.contains(worldEditMine)) {
            //Bukkit.broadcastMessage("The queue already contained this mine!");
        } else {
            worldEditUpgradeMineQueue.offer(worldEditMine);
        }
    }

    public void startExpandQueueSystem() {
        this.expandQueueTask = Task.syncDelayed(task1 -> {
            Mine worldEditMine = worldEditExpandMineQueue.poll();
            //Bukkit.broadcastMessage("Found mine: " + worldEditMine + " in the queue to be upgraded!");
        }, TimeUnit.SECONDS.toMillis(5));
    }

    public void addToExpandQueue(Mine worldEditMine) {
        if (worldEditExpandMineQueue.contains(worldEditMine)) {
            //Bukkit.broadcastMessage("The queue already contained this mine!");
        } else {
            worldEditExpandMineQueue.offer(worldEditMine);
        }
    }
}
