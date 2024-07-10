package org.springframework.cglib.util;

import java.util.Comparator;
import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassesKey;
import org.springframework.cglib.core.ReflectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter.class */
public abstract class ParallelSorter extends SorterTemplate {
    protected Object[] a;
    private Comparer comparer;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$Comparer.class */
    public interface Comparer {
        int compare(int i, int i2);
    }

    public abstract ParallelSorter newInstance(Object[] objArr);

    protected ParallelSorter() {
    }

    public static ParallelSorter create(Object[] arrays) {
        Generator gen = new Generator();
        gen.setArrays(arrays);
        return gen.create();
    }

    private int len() {
        return ((Object[]) this.a[0]).length;
    }

    public void quickSort(int index) {
        quickSort(index, 0, len(), null);
    }

    public void quickSort(int index, int lo, int hi) {
        quickSort(index, lo, hi, null);
    }

    public void quickSort(int index, Comparator cmp) {
        quickSort(index, 0, len(), cmp);
    }

    public void quickSort(int index, int lo, int hi, Comparator cmp) {
        chooseComparer(index, cmp);
        super.quickSort(lo, hi - 1);
    }

    public void mergeSort(int index) {
        mergeSort(index, 0, len(), null);
    }

    public void mergeSort(int index, int lo, int hi) {
        mergeSort(index, lo, hi, null);
    }

    public void mergeSort(int index, Comparator cmp) {
        mergeSort(index, 0, len(), cmp);
    }

    public void mergeSort(int index, int lo, int hi, Comparator cmp) {
        chooseComparer(index, cmp);
        super.mergeSort(lo, hi - 1);
    }

    private void chooseComparer(int index, Comparator cmp) {
        Object array = this.a[index];
        Class type = array.getClass().getComponentType();
        if (type.equals(Integer.TYPE)) {
            this.comparer = new IntComparer((int[]) array);
        } else if (type.equals(Long.TYPE)) {
            this.comparer = new LongComparer((long[]) array);
        } else if (type.equals(Double.TYPE)) {
            this.comparer = new DoubleComparer((double[]) array);
        } else if (type.equals(Float.TYPE)) {
            this.comparer = new FloatComparer((float[]) array);
        } else if (type.equals(Short.TYPE)) {
            this.comparer = new ShortComparer((short[]) array);
        } else if (type.equals(Byte.TYPE)) {
            this.comparer = new ByteComparer((byte[]) array);
        } else if (cmp != null) {
            this.comparer = new ComparatorComparer((Object[]) array, cmp);
        } else {
            this.comparer = new ObjectComparer((Object[]) array);
        }
    }

    @Override // org.springframework.cglib.util.SorterTemplate
    protected int compare(int i, int j) {
        return this.comparer.compare(i, j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$ComparatorComparer.class */
    public static class ComparatorComparer implements Comparer {
        private Object[] a;
        private Comparator cmp;

        public ComparatorComparer(Object[] a, Comparator cmp) {
            this.a = a;
            this.cmp = cmp;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            return this.cmp.compare(this.a[i], this.a[j]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$ObjectComparer.class */
    public static class ObjectComparer implements Comparer {
        private Object[] a;

        public ObjectComparer(Object[] a) {
            this.a = a;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            return ((Comparable) this.a[i]).compareTo(this.a[j]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$IntComparer.class */
    public static class IntComparer implements Comparer {
        private int[] a;

        public IntComparer(int[] a) {
            this.a = a;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            return this.a[i] - this.a[j];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$LongComparer.class */
    public static class LongComparer implements Comparer {
        private long[] a;

        public LongComparer(long[] a) {
            this.a = a;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            long vi = this.a[i];
            long vj = this.a[j];
            if (vi == vj) {
                return 0;
            }
            return vi > vj ? 1 : -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$FloatComparer.class */
    public static class FloatComparer implements Comparer {
        private float[] a;

        public FloatComparer(float[] a) {
            this.a = a;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            float vi = this.a[i];
            float vj = this.a[j];
            if (vi == vj) {
                return 0;
            }
            return vi > vj ? 1 : -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$DoubleComparer.class */
    public static class DoubleComparer implements Comparer {
        private double[] a;

        public DoubleComparer(double[] a) {
            this.a = a;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            double vi = this.a[i];
            double vj = this.a[j];
            if (vi == vj) {
                return 0;
            }
            return vi > vj ? 1 : -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$ShortComparer.class */
    public static class ShortComparer implements Comparer {
        private short[] a;

        public ShortComparer(short[] a) {
            this.a = a;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            return this.a[i] - this.a[j];
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$ByteComparer.class */
    public static class ByteComparer implements Comparer {
        private byte[] a;

        public ByteComparer(byte[] a) {
            this.a = a;
        }

        @Override // org.springframework.cglib.util.ParallelSorter.Comparer
        public int compare(int i, int j) {
            return this.a[i] - this.a[j];
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/util/ParallelSorter$Generator.class */
    public static class Generator extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(ParallelSorter.class.getName());
        private Object[] arrays;

        public Generator() {
            super(SOURCE);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected ClassLoader getDefaultClassLoader() {
            return null;
        }

        public void setArrays(Object[] arrays) {
            this.arrays = arrays;
        }

        public ParallelSorter create() {
            return (ParallelSorter) super.create(ClassesKey.create(this.arrays));
        }

        @Override // org.springframework.cglib.core.ClassGenerator
        public void generateClass(ClassVisitor v) throws Exception {
            if (this.arrays.length == 0) {
                throw new IllegalArgumentException("No arrays specified to sort");
            }
            for (int i = 0; i < this.arrays.length; i++) {
                if (!this.arrays[i].getClass().isArray()) {
                    throw new IllegalArgumentException(this.arrays[i].getClass() + " is not an array");
                }
            }
            new ParallelSorterEmitter(v, getClassName(), this.arrays);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object firstInstance(Class type) {
            return ((ParallelSorter) ReflectUtils.newInstance(type)).newInstance(this.arrays);
        }

        @Override // org.springframework.cglib.core.AbstractClassGenerator
        protected Object nextInstance(Object instance) {
            return ((ParallelSorter) instance).newInstance(this.arrays);
        }
    }
}