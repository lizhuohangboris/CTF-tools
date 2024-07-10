package org.springframework.scheduling.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/config/ScheduledTaskRegistrar.class */
public class ScheduledTaskRegistrar implements ScheduledTaskHolder, InitializingBean, DisposableBean {
    @Nullable
    private TaskScheduler taskScheduler;
    @Nullable
    private ScheduledExecutorService localExecutor;
    @Nullable
    private List<TriggerTask> triggerTasks;
    @Nullable
    private List<CronTask> cronTasks;
    @Nullable
    private List<IntervalTask> fixedRateTasks;
    @Nullable
    private List<IntervalTask> fixedDelayTasks;
    private final Map<Task, ScheduledTask> unresolvedTasks = new HashMap(16);
    private final Set<ScheduledTask> scheduledTasks = new LinkedHashSet(16);

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        Assert.notNull(taskScheduler, "TaskScheduler must not be null");
        this.taskScheduler = taskScheduler;
    }

    public void setScheduler(@Nullable Object scheduler) {
        if (scheduler == null) {
            this.taskScheduler = null;
        } else if (scheduler instanceof TaskScheduler) {
            this.taskScheduler = (TaskScheduler) scheduler;
        } else if (scheduler instanceof ScheduledExecutorService) {
            this.taskScheduler = new ConcurrentTaskScheduler((ScheduledExecutorService) scheduler);
        } else {
            throw new IllegalArgumentException("Unsupported scheduler type: " + scheduler.getClass());
        }
    }

    @Nullable
    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    public void setTriggerTasks(Map<Runnable, Trigger> triggerTasks) {
        this.triggerTasks = new ArrayList();
        triggerTasks.forEach(task, trigger -> {
            addTriggerTask(new TriggerTask(task, trigger));
        });
    }

    public void setTriggerTasksList(List<TriggerTask> triggerTasks) {
        this.triggerTasks = triggerTasks;
    }

    public List<TriggerTask> getTriggerTaskList() {
        return this.triggerTasks != null ? Collections.unmodifiableList(this.triggerTasks) : Collections.emptyList();
    }

    public void setCronTasks(Map<Runnable, String> cronTasks) {
        this.cronTasks = new ArrayList();
        cronTasks.forEach(this::addCronTask);
    }

    public void setCronTasksList(List<CronTask> cronTasks) {
        this.cronTasks = cronTasks;
    }

    public List<CronTask> getCronTaskList() {
        return this.cronTasks != null ? Collections.unmodifiableList(this.cronTasks) : Collections.emptyList();
    }

    public void setFixedRateTasks(Map<Runnable, Long> fixedRateTasks) {
        this.fixedRateTasks = new ArrayList();
        fixedRateTasks.forEach((v1, v2) -> {
            addFixedRateTask(v1, v2);
        });
    }

    public void setFixedRateTasksList(List<IntervalTask> fixedRateTasks) {
        this.fixedRateTasks = fixedRateTasks;
    }

    public List<IntervalTask> getFixedRateTaskList() {
        return this.fixedRateTasks != null ? Collections.unmodifiableList(this.fixedRateTasks) : Collections.emptyList();
    }

    public void setFixedDelayTasks(Map<Runnable, Long> fixedDelayTasks) {
        this.fixedDelayTasks = new ArrayList();
        fixedDelayTasks.forEach((v1, v2) -> {
            addFixedDelayTask(v1, v2);
        });
    }

    public void setFixedDelayTasksList(List<IntervalTask> fixedDelayTasks) {
        this.fixedDelayTasks = fixedDelayTasks;
    }

    public List<IntervalTask> getFixedDelayTaskList() {
        return this.fixedDelayTasks != null ? Collections.unmodifiableList(this.fixedDelayTasks) : Collections.emptyList();
    }

    public void addTriggerTask(Runnable task, Trigger trigger) {
        addTriggerTask(new TriggerTask(task, trigger));
    }

    public void addTriggerTask(TriggerTask task) {
        if (this.triggerTasks == null) {
            this.triggerTasks = new ArrayList();
        }
        this.triggerTasks.add(task);
    }

    public void addCronTask(Runnable task, String expression) {
        addCronTask(new CronTask(task, expression));
    }

    public void addCronTask(CronTask task) {
        if (this.cronTasks == null) {
            this.cronTasks = new ArrayList();
        }
        this.cronTasks.add(task);
    }

    public void addFixedRateTask(Runnable task, long interval) {
        addFixedRateTask(new IntervalTask(task, interval, 0L));
    }

    public void addFixedRateTask(IntervalTask task) {
        if (this.fixedRateTasks == null) {
            this.fixedRateTasks = new ArrayList();
        }
        this.fixedRateTasks.add(task);
    }

    public void addFixedDelayTask(Runnable task, long delay) {
        addFixedDelayTask(new IntervalTask(task, delay, 0L));
    }

    public void addFixedDelayTask(IntervalTask task) {
        if (this.fixedDelayTasks == null) {
            this.fixedDelayTasks = new ArrayList();
        }
        this.fixedDelayTasks.add(task);
    }

    public boolean hasTasks() {
        return (CollectionUtils.isEmpty(this.triggerTasks) && CollectionUtils.isEmpty(this.cronTasks) && CollectionUtils.isEmpty(this.fixedRateTasks) && CollectionUtils.isEmpty(this.fixedDelayTasks)) ? false : true;
    }

    public void afterPropertiesSet() {
        scheduleTasks();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void scheduleTasks() {
        if (this.taskScheduler == null) {
            this.localExecutor = Executors.newSingleThreadScheduledExecutor();
            this.taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
        }
        if (this.triggerTasks != null) {
            for (TriggerTask task : this.triggerTasks) {
                addScheduledTask(scheduleTriggerTask(task));
            }
        }
        if (this.cronTasks != null) {
            for (CronTask task2 : this.cronTasks) {
                addScheduledTask(scheduleCronTask(task2));
            }
        }
        if (this.fixedRateTasks != null) {
            for (IntervalTask task3 : this.fixedRateTasks) {
                addScheduledTask(scheduleFixedRateTask(task3));
            }
        }
        if (this.fixedDelayTasks != null) {
            for (IntervalTask task4 : this.fixedDelayTasks) {
                addScheduledTask(scheduleFixedDelayTask(task4));
            }
        }
    }

    private void addScheduledTask(@Nullable ScheduledTask task) {
        if (task != null) {
            this.scheduledTasks.add(task);
        }
    }

    @Nullable
    public ScheduledTask scheduleTriggerTask(TriggerTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
        } else {
            addTriggerTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Nullable
    public ScheduledTask scheduleCronTask(CronTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            scheduledTask.future = this.taskScheduler.schedule(task.getRunnable(), task.getTrigger());
        } else {
            addCronTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Nullable
    @Deprecated
    public ScheduledTask scheduleFixedRateTask(IntervalTask task) {
        FixedRateTask taskToUse = task instanceof FixedRateTask ? (FixedRateTask) task : new FixedRateTask(task.getRunnable(), task.getInterval(), task.getInitialDelay());
        return scheduleFixedRateTask(taskToUse);
    }

    @Nullable
    public ScheduledTask scheduleFixedRateTask(FixedRateTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            if (task.getInitialDelay() > 0) {
                Date startTime = new Date(System.currentTimeMillis() + task.getInitialDelay());
                scheduledTask.future = this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), startTime, task.getInterval());
            } else {
                scheduledTask.future = this.taskScheduler.scheduleAtFixedRate(task.getRunnable(), task.getInterval());
            }
        } else {
            addFixedRateTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Nullable
    @Deprecated
    public ScheduledTask scheduleFixedDelayTask(IntervalTask task) {
        FixedDelayTask taskToUse = task instanceof FixedDelayTask ? (FixedDelayTask) task : new FixedDelayTask(task.getRunnable(), task.getInterval(), task.getInitialDelay());
        return scheduleFixedDelayTask(taskToUse);
    }

    @Nullable
    public ScheduledTask scheduleFixedDelayTask(FixedDelayTask task) {
        ScheduledTask scheduledTask = this.unresolvedTasks.remove(task);
        boolean newTask = false;
        if (scheduledTask == null) {
            scheduledTask = new ScheduledTask(task);
            newTask = true;
        }
        if (this.taskScheduler != null) {
            if (task.getInitialDelay() > 0) {
                Date startTime = new Date(System.currentTimeMillis() + task.getInitialDelay());
                scheduledTask.future = this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), startTime, task.getInterval());
            } else {
                scheduledTask.future = this.taskScheduler.scheduleWithFixedDelay(task.getRunnable(), task.getInterval());
            }
        } else {
            addFixedDelayTask(task);
            this.unresolvedTasks.put(task, scheduledTask);
        }
        if (newTask) {
            return scheduledTask;
        }
        return null;
    }

    @Override // org.springframework.scheduling.config.ScheduledTaskHolder
    public Set<ScheduledTask> getScheduledTasks() {
        return Collections.unmodifiableSet(this.scheduledTasks);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks) {
            task.cancel();
        }
        if (this.localExecutor != null) {
            this.localExecutor.shutdownNow();
        }
    }
}