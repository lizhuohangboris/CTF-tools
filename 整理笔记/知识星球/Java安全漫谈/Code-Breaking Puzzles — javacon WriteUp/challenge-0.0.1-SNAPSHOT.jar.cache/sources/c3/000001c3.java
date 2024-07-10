package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.LiteralConverter;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/TimeBasedArchiveRemover.class */
public class TimeBasedArchiveRemover extends ContextAwareBase implements ArchiveRemover {
    protected static final long UNINITIALIZED = -1;
    protected static final long INACTIVITY_TOLERANCE_IN_MILLIS = 2764800000L;
    static final int MAX_VALUE_FOR_INACTIVITY_PERIODS = 336;
    final FileNamePattern fileNamePattern;
    final RollingCalendar rc;
    final boolean parentClean;
    private int maxHistory = 0;
    private long totalSizeCap = 0;
    long lastHeartBeat = -1;
    int callCount = 0;

    public TimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
        this.fileNamePattern = fileNamePattern;
        this.rc = rc;
        this.parentClean = computeParentCleaningFlag(fileNamePattern);
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public void clean(Date now) {
        long nowInMillis = now.getTime();
        int periodsElapsed = computeElapsedPeriodsSinceLastClean(nowInMillis);
        this.lastHeartBeat = nowInMillis;
        if (periodsElapsed > 1) {
            addInfo("Multiple periods, i.e. " + periodsElapsed + " periods, seem to have elapsed. This is expected at application start.");
        }
        for (int i = 0; i < periodsElapsed; i++) {
            int offset = getPeriodOffsetForDeletionTarget() - i;
            Date dateOfPeriodToClean = this.rc.getEndOfNextNthPeriod(now, offset);
            cleanPeriod(dateOfPeriodToClean);
        }
    }

    protected File[] getFilesInPeriod(Date dateOfPeriodToClean) {
        String filenameToDelete = this.fileNamePattern.convert(dateOfPeriodToClean);
        File file2Delete = new File(filenameToDelete);
        return fileExistsAndIsFile(file2Delete) ? new File[]{file2Delete} : new File[0];
    }

    private boolean fileExistsAndIsFile(File file2Delete) {
        return file2Delete.exists() && file2Delete.isFile();
    }

    public void cleanPeriod(Date dateOfPeriodToClean) {
        File[] matchingFileArray = getFilesInPeriod(dateOfPeriodToClean);
        for (File f : matchingFileArray) {
            addInfo("deleting " + f);
            f.delete();
        }
        if (this.parentClean && matchingFileArray.length > 0) {
            File parentDir = getParentDir(matchingFileArray[0]);
            removeFolderIfEmpty(parentDir);
        }
    }

    void capTotalSize(Date now) {
        long totalSize = 0;
        long totalRemoved = 0;
        for (int offset = 0; offset < this.maxHistory; offset++) {
            Date date = this.rc.getEndOfNextNthPeriod(now, -offset);
            File[] matchingFileArray = getFilesInPeriod(date);
            descendingSortByLastModified(matchingFileArray);
            for (File f : matchingFileArray) {
                long size = f.length();
                if (totalSize + size > this.totalSizeCap) {
                    addInfo("Deleting [" + f + "] of size " + new FileSize(size));
                    totalRemoved += size;
                    f.delete();
                }
                totalSize += size;
            }
        }
        addInfo("Removed  " + new FileSize(totalRemoved) + " of files");
    }

    private void descendingSortByLastModified(File[] matchingFileArray) {
        Arrays.sort(matchingFileArray, new Comparator<File>() { // from class: ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover.1
            @Override // java.util.Comparator
            public int compare(File f1, File f2) {
                long l1 = f1.lastModified();
                long l2 = f2.lastModified();
                if (l1 == l2) {
                    return 0;
                }
                if (l2 < l1) {
                    return -1;
                }
                return 1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public File getParentDir(File file) {
        File absolute = file.getAbsoluteFile();
        File parentDir = absolute.getParentFile();
        return parentDir;
    }

    int computeElapsedPeriodsSinceLastClean(long nowInMillis) {
        long periodsElapsed;
        if (this.lastHeartBeat == -1) {
            addInfo("first clean up after appender initialization");
            long periodsElapsed2 = this.rc.periodBarriersCrossed(nowInMillis, nowInMillis + INACTIVITY_TOLERANCE_IN_MILLIS);
            periodsElapsed = Math.min(periodsElapsed2, 336L);
        } else {
            periodsElapsed = this.rc.periodBarriersCrossed(this.lastHeartBeat, nowInMillis);
        }
        return (int) periodsElapsed;
    }

    boolean computeParentCleaningFlag(FileNamePattern fileNamePattern) {
        Converter<Object> p;
        DateTokenConverter<Object> dtc = fileNamePattern.getPrimaryDateTokenConverter();
        if (dtc.getDatePattern().indexOf(47) != -1) {
            return true;
        }
        Converter<Object> converter = fileNamePattern.headTokenConverter;
        while (true) {
            p = converter;
            if (p == null || (p instanceof DateTokenConverter)) {
                break;
            }
            converter = p.getNext();
        }
        while (p != null) {
            if (p instanceof LiteralConverter) {
                String s = p.convert(null);
                if (s.indexOf(47) != -1) {
                    return true;
                }
            }
            p = p.getNext();
        }
        return false;
    }

    void removeFolderIfEmpty(File dir) {
        removeFolderIfEmpty(dir, 0);
    }

    private void removeFolderIfEmpty(File dir, int depth) {
        if (depth < 3 && dir.isDirectory() && FileFilterUtil.isEmptyDirectory(dir)) {
            addInfo("deleting folder [" + dir + "]");
            dir.delete();
            removeFolderIfEmpty(dir.getParentFile(), depth + 1);
        }
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public void setMaxHistory(int maxHistory) {
        this.maxHistory = maxHistory;
    }

    protected int getPeriodOffsetForDeletionTarget() {
        return (-this.maxHistory) - 1;
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public void setTotalSizeCap(long totalSizeCap) {
        this.totalSizeCap = totalSizeCap;
    }

    public String toString() {
        return "c.q.l.core.rolling.helper.TimeBasedArchiveRemover";
    }

    @Override // ch.qos.logback.core.rolling.helper.ArchiveRemover
    public Future<?> cleanAsynchronously(Date now) {
        Runnable runnable = new ArhiveRemoverRunnable(now);
        ExecutorService executorService = this.context.getScheduledExecutorService();
        Future<?> future = executorService.submit(runnable);
        return future;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/TimeBasedArchiveRemover$ArhiveRemoverRunnable.class */
    public class ArhiveRemoverRunnable implements Runnable {
        Date now;

        ArhiveRemoverRunnable(Date now) {
            this.now = now;
        }

        @Override // java.lang.Runnable
        public void run() {
            TimeBasedArchiveRemover.this.clean(this.now);
            if (TimeBasedArchiveRemover.this.totalSizeCap != 0 && TimeBasedArchiveRemover.this.totalSizeCap > 0) {
                TimeBasedArchiveRemover.this.capTotalSize(this.now);
            }
        }
    }
}