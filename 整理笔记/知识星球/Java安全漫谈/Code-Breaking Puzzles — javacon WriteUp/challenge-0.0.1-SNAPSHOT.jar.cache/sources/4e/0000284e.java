package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.NumberPointType;
import org.thymeleaf.util.NumberUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Numbers.class */
public final class Numbers {
    private final Locale locale;

    public Numbers(Locale locale) {
        this.locale = locale;
    }

    public String formatInteger(Number target, Integer minIntegerDigits) {
        if (target == null) {
            return null;
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting integer with minimum integer digits = " + minIntegerDigits, e);
        }
    }

    public String[] arrayFormatInteger(Object[] target, Integer minIntegerDigits) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatInteger((Number) target[i], minIntegerDigits);
        }
        return result;
    }

    public List<String> listFormatInteger(List<? extends Number> target, Integer minIntegerDigits) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatInteger(element, minIntegerDigits));
        }
        return result;
    }

    public Set<String> setFormatInteger(Set<? extends Number> target, Integer minIntegerDigits) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatInteger(element, minIntegerDigits));
        }
        return result;
    }

    public String formatInteger(Number target, Integer minIntegerDigits, String thousandsPointType) {
        if (target == null) {
            return null;
        }
        NumberPointType thousandsNumberPointType = NumberPointType.match(thousandsPointType);
        if (thousandsNumberPointType == null) {
            throw new TemplateProcessingException("Unrecognized point format \"" + thousandsPointType + "\"");
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, thousandsNumberPointType, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting integer with minimum integer digits = " + minIntegerDigits + " and thousands point type = " + thousandsPointType, e);
        }
    }

    public String[] arrayFormatInteger(Object[] target, Integer minIntegerDigits, String thousandsPointType) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatInteger((Number) target[i], minIntegerDigits, thousandsPointType);
        }
        return result;
    }

    public List<String> listFormatInteger(List<? extends Number> target, Integer minIntegerDigits, String thousandsPointType) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatInteger(element, minIntegerDigits, thousandsPointType));
        }
        return result;
    }

    public Set<String> setFormatInteger(Set<? extends Number> target, Integer minIntegerDigits, String thousandsPointType) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatInteger(element, minIntegerDigits, thousandsPointType));
        }
        return result;
    }

    public String formatDecimal(Number target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, decimalDigits, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting decimal with minimum integer digits = " + minIntegerDigits + " and decimal digits " + decimalDigits, e);
        }
    }

    public String[] arrayFormatDecimal(Object[] target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatDecimal((Number) target[i], minIntegerDigits, decimalDigits);
        }
        return result;
    }

    public List<String> listFormatDecimal(List<? extends Number> target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits));
        }
        return result;
    }

    public Set<String> setFormatDecimal(Set<? extends Number> target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits));
        }
        return result;
    }

    public String formatDecimal(Number target, Integer minIntegerDigits, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        NumberPointType decimalNumberPointType = NumberPointType.match(decimalPointType);
        if (decimalNumberPointType == null) {
            throw new TemplateProcessingException("Unrecognized point format \"" + decimalPointType + "\"");
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, decimalDigits, decimalNumberPointType, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting decimal with minimum integer digits = " + minIntegerDigits + ", decimal digits = " + decimalDigits + " and decimal point type = " + decimalPointType, e);
        }
    }

    public String[] arrayFormatDecimal(Object[] target, Integer minIntegerDigits, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatDecimal((Number) target[i], minIntegerDigits, decimalDigits, decimalPointType);
        }
        return result;
    }

    public List<String> listFormatDecimal(List<? extends Number> target, Integer minIntegerDigits, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits, decimalPointType));
        }
        return result;
    }

    public Set<String> setFormatDecimal(Set<? extends Number> target, Integer minIntegerDigits, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, decimalDigits, decimalPointType));
        }
        return result;
    }

    public String formatDecimal(Number target, Integer minIntegerDigits, String thousandsPointType, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        NumberPointType decimalNumberPointType = NumberPointType.match(decimalPointType);
        if (decimalNumberPointType == null) {
            throw new TemplateProcessingException("Unrecognized point format \"" + decimalPointType + "\"");
        }
        NumberPointType thousandsNumberPointType = NumberPointType.match(thousandsPointType);
        if (thousandsNumberPointType == null) {
            throw new TemplateProcessingException("Unrecognized point format \"" + thousandsPointType + "\"");
        }
        try {
            return NumberUtils.format(target, minIntegerDigits, thousandsNumberPointType, decimalDigits, decimalNumberPointType, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting decimal with minimum integer digits = " + minIntegerDigits + ", thousands point type = " + thousandsPointType + ", decimal digits = " + decimalDigits + " and decimal point type = " + decimalPointType, e);
        }
    }

    public String[] arrayFormatDecimal(Object[] target, Integer minIntegerDigits, String thousandsPointType, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatDecimal((Number) target[i], minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType);
        }
        return result;
    }

    public List<String> listFormatDecimal(List<? extends Number> target, Integer minIntegerDigits, String thousandsPointType, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType));
        }
        return result;
    }

    public Set<String> setFormatDecimal(Set<? extends Number> target, Integer minIntegerDigits, String thousandsPointType, Integer decimalDigits, String decimalPointType) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatDecimal(element, minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType));
        }
        return result;
    }

    public String formatCurrency(Number target) {
        if (target == null) {
            return null;
        }
        try {
            return NumberUtils.formatCurrency(target, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting currency", e);
        }
    }

    public String[] arrayFormatCurrency(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatCurrency((Number) target[i]);
        }
        return result;
    }

    public List<String> listFormatCurrency(List<? extends Number> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatCurrency(element));
        }
        return result;
    }

    public Set<String> setFormatCurrency(Set<? extends Number> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatCurrency(element));
        }
        return result;
    }

    public String formatPercent(Number target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        try {
            return NumberUtils.formatPercent(target, minIntegerDigits, decimalDigits, this.locale);
        } catch (Exception e) {
            throw new TemplateProcessingException("Error formatting percent", e);
        }
    }

    public String[] arrayFormatPercent(Object[] target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = formatPercent((Number) target[i], minIntegerDigits, decimalDigits);
        }
        return result;
    }

    public List<String> listFormatPercent(List<? extends Number> target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatPercent(element, minIntegerDigits, decimalDigits));
        }
        return result;
    }

    public Set<String> setFormatPercent(Set<? extends Number> target, Integer minIntegerDigits, Integer decimalDigits) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Number element : target) {
            result.add(formatPercent(element, minIntegerDigits, decimalDigits));
        }
        return result;
    }

    public Integer[] sequence(Integer from, Integer to) {
        return NumberUtils.sequence(from, to);
    }

    public Integer[] sequence(Integer from, Integer to, Integer step) {
        return NumberUtils.sequence(from, to, step);
    }
}