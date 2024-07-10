package org.thymeleaf.util;

import java.util.Comparator;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.util.ProcessorConfigurationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorComparators.class */
public final class ProcessorComparators {
    public static final Comparator<IProcessor> PROCESSOR_COMPARATOR = new ProcessorPrecedenceComparator();
    public static final Comparator<IPreProcessor> PRE_PROCESSOR_COMPARATOR = new PreProcessorPrecedenceComparator();
    public static final Comparator<IPostProcessor> POST_PROCESSOR_COMPARATOR = new PostProcessorPrecedenceComparator();

    private ProcessorComparators() {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorComparators$ProcessorPrecedenceComparator.class */
    private static final class ProcessorPrecedenceComparator implements Comparator<IProcessor> {
        ProcessorPrecedenceComparator() {
        }

        @Override // java.util.Comparator
        public int compare(IProcessor o1, IProcessor o2) {
            if (o1 == o2) {
                return 0;
            }
            if ((o1 instanceof ProcessorConfigurationUtils.AbstractProcessorWrapper) && (o2 instanceof ProcessorConfigurationUtils.AbstractProcessorWrapper)) {
                return compareWrapped((ProcessorConfigurationUtils.AbstractProcessorWrapper) o1, (ProcessorConfigurationUtils.AbstractProcessorWrapper) o2);
            }
            int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2));
        }

        private int compareWrapped(ProcessorConfigurationUtils.AbstractProcessorWrapper o1w, ProcessorConfigurationUtils.AbstractProcessorWrapper o2w) {
            int dialectPrecedenceComp = compareInts(o1w.getDialectPrecedence(), o2w.getDialectPrecedence());
            if (dialectPrecedenceComp != 0) {
                return dialectPrecedenceComp;
            }
            IProcessor o1 = o1w.unwrap();
            IProcessor o2 = o2w.unwrap();
            int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2));
        }

        private static int compareInts(int x, int y) {
            if (x < y) {
                return -1;
            }
            return x == y ? 0 : 1;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorComparators$PreProcessorPrecedenceComparator.class */
    private static final class PreProcessorPrecedenceComparator implements Comparator<IPreProcessor> {
        PreProcessorPrecedenceComparator() {
        }

        @Override // java.util.Comparator
        public int compare(IPreProcessor o1, IPreProcessor o2) {
            if (o1 == o2) {
                return 0;
            }
            if ((o1 instanceof ProcessorConfigurationUtils.PreProcessorWrapper) && (o2 instanceof ProcessorConfigurationUtils.PreProcessorWrapper)) {
                return compareWrapped((ProcessorConfigurationUtils.PreProcessorWrapper) o1, (ProcessorConfigurationUtils.PreProcessorWrapper) o2);
            }
            int preProcessorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (preProcessorPrecedenceComp != 0) {
                return preProcessorPrecedenceComp;
            }
            int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2));
        }

        private int compareWrapped(ProcessorConfigurationUtils.PreProcessorWrapper o1w, ProcessorConfigurationUtils.PreProcessorWrapper o2w) {
            int dialectPrecedenceComp = compareInts(o1w.getDialect().getDialectProcessorPrecedence(), o2w.getDialect().getDialectProcessorPrecedence());
            if (dialectPrecedenceComp != 0) {
                return dialectPrecedenceComp;
            }
            IPreProcessor o1 = o1w.unwrap();
            IPreProcessor o2 = o2w.unwrap();
            int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2));
        }

        private static int compareInts(int x, int y) {
            if (x < y) {
                return -1;
            }
            return x == y ? 0 : 1;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ProcessorComparators$PostProcessorPrecedenceComparator.class */
    private static final class PostProcessorPrecedenceComparator implements Comparator<IPostProcessor> {
        PostProcessorPrecedenceComparator() {
        }

        @Override // java.util.Comparator
        public int compare(IPostProcessor o1, IPostProcessor o2) {
            if (o1 == o2) {
                return 0;
            }
            if ((o1 instanceof ProcessorConfigurationUtils.PostProcessorWrapper) && (o2 instanceof ProcessorConfigurationUtils.PostProcessorWrapper)) {
                return compareWrapped((ProcessorConfigurationUtils.PostProcessorWrapper) o1, (ProcessorConfigurationUtils.PostProcessorWrapper) o2);
            }
            int postProcessorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (postProcessorPrecedenceComp != 0) {
                return postProcessorPrecedenceComp;
            }
            int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2));
        }

        private int compareWrapped(ProcessorConfigurationUtils.PostProcessorWrapper o1w, ProcessorConfigurationUtils.PostProcessorWrapper o2w) {
            int dialectPrecedenceComp = compareInts(o1w.getDialect().getDialectProcessorPrecedence(), o2w.getDialect().getDialectProcessorPrecedence());
            if (dialectPrecedenceComp != 0) {
                return dialectPrecedenceComp;
            }
            IPostProcessor o1 = o1w.unwrap();
            IPostProcessor o2 = o2w.unwrap();
            int processorPrecedenceComp = compareInts(o1.getPrecedence(), o2.getPrecedence());
            if (processorPrecedenceComp != 0) {
                return processorPrecedenceComp;
            }
            int classNameComp = o1.getClass().getName().compareTo(o2.getClass().getName());
            if (classNameComp != 0) {
                return classNameComp;
            }
            return compareInts(System.identityHashCode(o1), System.identityHashCode(o2));
        }

        private static int compareInts(int x, int y) {
            if (x < y) {
                return -1;
            }
            return x == y ? 0 : 1;
        }
    }
}