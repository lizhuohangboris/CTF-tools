package ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.util.Date;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/SizeAndTimeBasedArchiveRemover.class */
public class SizeAndTimeBasedArchiveRemover extends TimeBasedArchiveRemover {
    public SizeAndTimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
        super(fileNamePattern, rc);
    }

    @Override // ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover
    protected File[] getFilesInPeriod(Date dateOfPeriodToClean) {
        File archive0 = new File(this.fileNamePattern.convertMultipleArguments(dateOfPeriodToClean, 0));
        File parentDir = getParentDir(archive0);
        String stemRegex = createStemRegex(dateOfPeriodToClean);
        File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
        return matchingFileArray;
    }

    private String createStemRegex(Date dateOfPeriodToClean) {
        String regex = this.fileNamePattern.toRegexForFixedDate(dateOfPeriodToClean);
        return FileFilterUtil.afterLastSlash(regex);
    }
}