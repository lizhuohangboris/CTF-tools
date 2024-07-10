package org.springframework.util;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/StopWatch.class */
public class StopWatch {
    private final String id;
    private boolean keepTaskList;
    private final List<TaskInfo> taskList;
    private long startTimeMillis;
    @Nullable
    private String currentTaskName;
    @Nullable
    private TaskInfo lastTaskInfo;
    private int taskCount;
    private long totalTimeMillis;

    public StopWatch() {
        this("");
    }

    public StopWatch(String id) {
        this.keepTaskList = true;
        this.taskList = new LinkedList();
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setKeepTaskList(boolean keepTaskList) {
        this.keepTaskList = keepTaskList;
    }

    public void start() throws IllegalStateException {
        start("");
    }

    public void start(String taskName) throws IllegalStateException {
        if (this.currentTaskName != null) {
            throw new IllegalStateException("Can't start StopWatch: it's already running");
        }
        this.currentTaskName = taskName;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public void stop() throws IllegalStateException {
        if (this.currentTaskName == null) {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }
        long lastTime = System.currentTimeMillis() - this.startTimeMillis;
        this.totalTimeMillis += lastTime;
        this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
        if (this.keepTaskList) {
            this.taskList.add(this.lastTaskInfo);
        }
        this.taskCount++;
        this.currentTaskName = null;
    }

    public boolean isRunning() {
        return this.currentTaskName != null;
    }

    @Nullable
    public String currentTaskName() {
        return this.currentTaskName;
    }

    public long getLastTaskTimeMillis() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task interval");
        }
        return this.lastTaskInfo.getTimeMillis();
    }

    public String getLastTaskName() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task name");
        }
        return this.lastTaskInfo.getTaskName();
    }

    public TaskInfo getLastTaskInfo() throws IllegalStateException {
        if (this.lastTaskInfo == null) {
            throw new IllegalStateException("No tasks run: can't get last task info");
        }
        return this.lastTaskInfo;
    }

    public long getTotalTimeMillis() {
        return this.totalTimeMillis;
    }

    public double getTotalTimeSeconds() {
        return this.totalTimeMillis / 1000.0d;
    }

    public int getTaskCount() {
        return this.taskCount;
    }

    public TaskInfo[] getTaskInfo() {
        if (!this.keepTaskList) {
            throw new UnsupportedOperationException("Task info is not being kept!");
        }
        return (TaskInfo[]) this.taskList.toArray(new TaskInfo[0]);
    }

    public String shortSummary() {
        return "StopWatch '" + getId() + "': running time (millis) = " + getTotalTimeMillis();
    }

    public String prettyPrint() {
        TaskInfo[] taskInfo;
        StringBuilder sb = new StringBuilder(shortSummary());
        sb.append('\n');
        if (!this.keepTaskList) {
            sb.append("No task info kept");
        } else {
            sb.append("-----------------------------------------\n");
            sb.append("ms     %     Task name\n");
            sb.append("-----------------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumIntegerDigits(5);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumIntegerDigits(3);
            pf.setGroupingUsed(false);
            for (TaskInfo task : getTaskInfo()) {
                sb.append(nf.format(task.getTimeMillis())).append("  ");
                sb.append(pf.format(task.getTimeSeconds() / getTotalTimeSeconds())).append("  ");
                sb.append(task.getTaskName()).append("\n");
            }
        }
        return sb.toString();
    }

    public String toString() {
        TaskInfo[] taskInfo;
        StringBuilder sb = new StringBuilder(shortSummary());
        if (this.keepTaskList) {
            for (TaskInfo task : getTaskInfo()) {
                sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeMillis());
                long percent = Math.round((100.0d * task.getTimeSeconds()) / getTotalTimeSeconds());
                sb.append(" = ").append(percent).append(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
            }
        } else {
            sb.append("; no task info kept");
        }
        return sb.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/StopWatch$TaskInfo.class */
    public static final class TaskInfo {
        private final String taskName;
        private final long timeMillis;

        TaskInfo(String taskName, long timeMillis) {
            this.taskName = taskName;
            this.timeMillis = timeMillis;
        }

        public String getTaskName() {
            return this.taskName;
        }

        public long getTimeMillis() {
            return this.timeMillis;
        }

        public double getTimeSeconds() {
            return this.timeMillis / 1000.0d;
        }
    }
}