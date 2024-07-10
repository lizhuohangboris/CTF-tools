package org.springframework.boot.ansi;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.asm.Opcodes;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/ansi/AnsiColors.class */
public final class AnsiColors {
    private static final Map<AnsiColor, LabColor> ANSI_COLOR_MAP;

    static {
        Map<AnsiColor, LabColor> colorMap = new EnumMap<>(AnsiColor.class);
        colorMap.put(AnsiColor.BLACK, new LabColor((Integer) 0));
        colorMap.put(AnsiColor.RED, new LabColor((Integer) 11141120));
        colorMap.put(AnsiColor.GREEN, new LabColor((Integer) 43520));
        colorMap.put(AnsiColor.YELLOW, new LabColor((Integer) 11162880));
        colorMap.put(AnsiColor.BLUE, new LabColor(Integer.valueOf((int) Opcodes.TABLESWITCH)));
        colorMap.put(AnsiColor.MAGENTA, new LabColor((Integer) 11141290));
        colorMap.put(AnsiColor.CYAN, new LabColor((Integer) 43690));
        colorMap.put(AnsiColor.WHITE, new LabColor((Integer) 11184810));
        colorMap.put(AnsiColor.BRIGHT_BLACK, new LabColor((Integer) 5592405));
        colorMap.put(AnsiColor.BRIGHT_RED, new LabColor((Integer) 16733525));
        colorMap.put(AnsiColor.BRIGHT_GREEN, new LabColor((Integer) 5635840));
        colorMap.put(AnsiColor.BRIGHT_YELLOW, new LabColor((Integer) 16777045));
        colorMap.put(AnsiColor.BRIGHT_BLUE, new LabColor((Integer) 5592575));
        colorMap.put(AnsiColor.BRIGHT_MAGENTA, new LabColor((Integer) 16733695));
        colorMap.put(AnsiColor.BRIGHT_CYAN, new LabColor((Integer) 5636095));
        colorMap.put(AnsiColor.BRIGHT_WHITE, new LabColor((Integer) 16777215));
        ANSI_COLOR_MAP = Collections.unmodifiableMap(colorMap);
    }

    private AnsiColors() {
    }

    public static AnsiColor getClosest(Color color) {
        return getClosest(new LabColor(color));
    }

    private static AnsiColor getClosest(LabColor color) {
        AnsiColor result = null;
        double resultDistance = 3.4028234663852886E38d;
        for (Map.Entry<AnsiColor, LabColor> entry : ANSI_COLOR_MAP.entrySet()) {
            double distance = color.getDistance(entry.getValue());
            if (result == null || distance < resultDistance) {
                resultDistance = distance;
                result = entry.getKey();
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/ansi/AnsiColors$LabColor.class */
    public static final class LabColor {
        private static final ColorSpace XYZ_COLOR_SPACE = ColorSpace.getInstance(1001);
        private final double l;
        private final double a;
        private final double b;

        LabColor(Integer rgb) {
            this(rgb != null ? new Color(rgb.intValue()) : null);
        }

        LabColor(Color color) {
            Assert.notNull(color, "Color must not be null");
            float[] lab = fromXyz(color.getColorComponents(XYZ_COLOR_SPACE, (float[]) null));
            this.l = lab[0];
            this.a = lab[1];
            this.b = lab[2];
        }

        private float[] fromXyz(float[] xyz) {
            return fromXyz(xyz[0], xyz[1], xyz[2]);
        }

        private float[] fromXyz(float x, float y, float z) {
            double l = (f(y) - 16.0d) * 116.0d;
            double a = (f(x) - f(y)) * 500.0d;
            double b = (f(y) - f(z)) * 200.0d;
            return new float[]{(float) l, (float) a, (float) b};
        }

        private double f(double t) {
            return t > 0.008856451679035631d ? Math.cbrt(t) : (0.3333333333333333d * Math.pow(4.833333333333333d, 2.0d) * t) + 0.13793103448275862d;
        }

        public double getDistance(LabColor other) {
            double c1 = Math.sqrt((this.a * this.a) + (this.b * this.b));
            double deltaC = c1 - Math.sqrt((other.a * other.a) + (other.b * other.b));
            double deltaA = this.a - other.a;
            double deltaB = this.b - other.b;
            double deltaH = Math.sqrt(Math.max(0.0d, ((deltaA * deltaA) + (deltaB * deltaB)) - (deltaC * deltaC)));
            return Math.sqrt(Math.max(0.0d, Math.pow((this.l - other.l) / 1.0d, 2.0d) + Math.pow(deltaC / (1.0d + (0.045d * c1)), 2.0d) + Math.pow(deltaH / (1.0d + (0.015d * c1)), 2.0d)));
        }
    }
}