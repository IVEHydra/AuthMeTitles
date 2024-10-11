package me.ivehydra.authmetitles.handler;

import me.ivehydra.authmetitles.AuthMeTitles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class AbstractHandler implements Handler {

    protected final AuthMeTitles instance = AuthMeTitles.getInstance();
    protected final Player p;
    protected final String string;
    private int taskId = -1;

    public AbstractHandler(Player p, String string) {
        this.p = p;
        this.string = string;
    }

    protected void setTaskId(int taskId) { this.taskId = taskId; }

    @Override
    public void stop() {
        if(taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
