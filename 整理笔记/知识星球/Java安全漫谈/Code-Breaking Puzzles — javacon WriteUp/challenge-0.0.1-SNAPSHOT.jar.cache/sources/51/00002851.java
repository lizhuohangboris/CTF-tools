package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Strings.class */
public final class Strings {
    private final Locale locale;

    public Strings(Locale locale) {
        this.locale = locale;
    }

    public String toString(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.toString(target);
    }

    public String[] arrayToString(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = toString(target[i]);
        }
        return result;
    }

    public List<String> listToString(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(toString(element));
        }
        return result;
    }

    public Set<String> setToString(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(toString(element));
        }
        return result;
    }

    public String abbreviate(Object target, int maxSize) {
        if (target == null) {
            return null;
        }
        return StringUtils.abbreviate(target, maxSize);
    }

    public String[] arrayAbbreviate(Object[] target, int maxSize) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = abbreviate(target[i], maxSize);
        }
        return result;
    }

    public List<String> listAbbreviate(List<?> target, int maxSize) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(abbreviate(element, maxSize));
        }
        return result;
    }

    public Set<String> setAbbreviate(Set<?> target, int maxSize) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(abbreviate(element, maxSize));
        }
        return result;
    }

    public Boolean equals(Object first, Object second) {
        return StringUtils.equals(first, second);
    }

    public Boolean equalsIgnoreCase(Object first, Object second) {
        return StringUtils.equalsIgnoreCase(first, second);
    }

    public Boolean contains(Object target, String fragment) {
        return StringUtils.contains(target, fragment);
    }

    public Boolean[] arrayContains(Object[] target, String fragment) {
        if (target == null) {
            return null;
        }
        Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = contains(target[i], fragment);
        }
        return result;
    }

    public List<Boolean> listContains(List<?> target, String fragment) {
        if (target == null) {
            return null;
        }
        List<Boolean> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(contains(element, fragment));
        }
        return result;
    }

    public Set<Boolean> setContains(Set<?> target, String fragment) {
        if (target == null) {
            return null;
        }
        Set<Boolean> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(contains(element, fragment));
        }
        return result;
    }

    public Boolean containsIgnoreCase(Object target, String fragment) {
        return StringUtils.containsIgnoreCase(target, fragment, this.locale);
    }

    public Boolean[] arrayContainsIgnoreCase(Object[] target, String fragment) {
        if (target == null) {
            return null;
        }
        Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = containsIgnoreCase(target[i], fragment);
        }
        return result;
    }

    public List<Boolean> listContainsIgnoreCase(List<?> target, String fragment) {
        if (target == null) {
            return null;
        }
        List<Boolean> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(containsIgnoreCase(element, fragment));
        }
        return result;
    }

    public Set<Boolean> setContainsIgnoreCase(Set<?> target, String fragment) {
        if (target == null) {
            return null;
        }
        Set<Boolean> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(containsIgnoreCase(element, fragment));
        }
        return result;
    }

    public Boolean startsWith(Object target, String prefix) {
        return StringUtils.startsWith(target, prefix);
    }

    public Boolean[] arrayStartsWith(Object[] target, String prefix) {
        if (target == null) {
            return null;
        }
        Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = startsWith(target[i], prefix);
        }
        return result;
    }

    public List<Boolean> listStartsWith(List<?> target, String prefix) {
        if (target == null) {
            return null;
        }
        List<Boolean> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(startsWith(element, prefix));
        }
        return result;
    }

    public Set<Boolean> setStartsWith(Set<?> target, String prefix) {
        if (target == null) {
            return null;
        }
        Set<Boolean> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(startsWith(element, prefix));
        }
        return result;
    }

    public Boolean endsWith(Object target, String suffix) {
        return StringUtils.endsWith(target, suffix);
    }

    public Boolean[] arrayEndsWith(Object[] target, String suffix) {
        if (target == null) {
            return null;
        }
        Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = endsWith(target[i], suffix);
        }
        return result;
    }

    public List<Boolean> listEndsWith(List<?> target, String suffix) {
        if (target == null) {
            return null;
        }
        List<Boolean> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(endsWith(element, suffix));
        }
        return result;
    }

    public Set<Boolean> setEndsWith(Set<?> target, String suffix) {
        if (target == null) {
            return null;
        }
        Set<Boolean> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(endsWith(element, suffix));
        }
        return result;
    }

    public String substring(Object target, int start, int end) {
        if (target == null) {
            return null;
        }
        return StringUtils.substring(target, start, end);
    }

    public String[] arraySubstring(Object[] target, int start, int end) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substring(target[i], start, end);
        }
        return result;
    }

    public List<String> listSubstring(List<?> target, int start, int end) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(substring(element, start, end));
        }
        return result;
    }

    public Set<String> setSubstring(Set<?> target, int start, int end) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(substring(element, start, end));
        }
        return result;
    }

    public String substring(Object target, int start) {
        if (target == null) {
            return null;
        }
        return StringUtils.substring(target, start);
    }

    public String[] arraySubstring(Object[] target, int start) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substring(target[i], start);
        }
        return result;
    }

    public List<String> listSubstring(List<?> target, int start) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(substring(element, start));
        }
        return result;
    }

    public Set<String> setSubstring(Set<?> target, int start) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(substring(element, start));
        }
        return result;
    }

    public String substringAfter(Object target, String substr) {
        if (target == null) {
            return null;
        }
        return StringUtils.substringAfter(target, substr);
    }

    public String[] arraySubstringAfter(Object[] target, String substr) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substringAfter(target[i], substr);
        }
        return result;
    }

    public List<String> listSubstringAfter(List<?> target, String substr) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(substringAfter(element, substr));
        }
        return result;
    }

    public Set<String> setSubstringAfter(Set<?> target, String substr) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(substringAfter(element, substr));
        }
        return result;
    }

    public String substringBefore(Object target, String substr) {
        if (target == null) {
            return null;
        }
        return StringUtils.substringBefore(target, substr);
    }

    public String[] arraySubstringBefore(Object[] target, String substr) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = substringBefore(target[i], substr);
        }
        return result;
    }

    public List<String> listSubstringBefore(List<?> target, String substr) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(substringBefore(element, substr));
        }
        return result;
    }

    public Set<String> setSubstringBefore(Set<?> target, String substr) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(substringBefore(element, substr));
        }
        return result;
    }

    public String prepend(Object target, String prefix) {
        if (target == null) {
            return null;
        }
        return StringUtils.prepend(target, prefix);
    }

    public String[] arrayPrepend(Object[] target, String prefix) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = prepend(target[i], prefix);
        }
        return result;
    }

    public List<String> listPrepend(List<?> target, String prefix) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(prepend(element, prefix));
        }
        return result;
    }

    public Set<String> setPrepend(Set<?> target, String prefix) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(prepend(element, prefix));
        }
        return result;
    }

    public String repeat(Object target, int times) {
        if (target == null) {
            return null;
        }
        return StringUtils.repeat(target, times);
    }

    public String append(Object target, String suffix) {
        if (target == null) {
            return null;
        }
        return StringUtils.append(target, suffix);
    }

    public String concat(Object... values) {
        return StringUtils.concat(values);
    }

    public String concatReplaceNulls(String nullValue, Object... values) {
        return StringUtils.concatReplaceNulls(nullValue, values);
    }

    public String[] arrayAppend(Object[] target, String suffix) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = append(target[i], suffix);
        }
        return result;
    }

    public List<String> listAppend(List<?> target, String suffix) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(append(element, suffix));
        }
        return result;
    }

    public Set<String> setAppend(Set<?> target, String suffix) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(append(element, suffix));
        }
        return result;
    }

    public Integer indexOf(Object target, String fragment) {
        return StringUtils.indexOf(target, fragment);
    }

    public Integer[] arrayIndexOf(Object[] target, String fragment) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = indexOf(target[i], fragment);
        }
        return result;
    }

    public List<Integer> listIndexOf(List<?> target, String fragment) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(indexOf(element, fragment));
        }
        return result;
    }

    public Set<Integer> setIndexOf(Set<?> target, String fragment) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(indexOf(element, fragment));
        }
        return result;
    }

    public Boolean isEmpty(Object target) {
        return Boolean.valueOf(target == null || StringUtils.isEmptyOrWhitespace(target.toString()));
    }

    public Boolean[] arrayIsEmpty(Object[] target) {
        if (target == null) {
            return null;
        }
        Boolean[] result = new Boolean[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = isEmpty(target[i]);
        }
        return result;
    }

    public List<Boolean> listIsEmpty(List<?> target) {
        if (target == null) {
            return null;
        }
        List<Boolean> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(isEmpty(element));
        }
        return result;
    }

    public Set<Boolean> setIsEmpty(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<Boolean> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(isEmpty(element));
        }
        return result;
    }

    public String arrayJoin(Object[] stringArray, String separator) {
        if (stringArray == null) {
            return null;
        }
        return StringUtils.join(stringArray, separator);
    }

    public String listJoin(List<?> stringIter, String separator) {
        if (stringIter == null) {
            return null;
        }
        return StringUtils.join(stringIter, separator);
    }

    public String setJoin(Set<?> stringIter, String separator) {
        if (stringIter == null) {
            return null;
        }
        return StringUtils.join(stringIter, separator);
    }

    public String[] arraySplit(Object target, String separator) {
        if (target == null) {
            return null;
        }
        return StringUtils.split(target, separator);
    }

    public List<String> listSplit(Object target, String separator) {
        if (target == null) {
            return null;
        }
        return new ArrayList(java.util.Arrays.asList(StringUtils.split(target, separator)));
    }

    public Set<String> setSplit(Object target, String separator) {
        if (target == null) {
            return null;
        }
        return new LinkedHashSet(java.util.Arrays.asList(StringUtils.split(target, separator)));
    }

    public Integer length(Object target) {
        return StringUtils.length(target);
    }

    public Integer[] arrayLength(Object[] target) {
        if (target == null) {
            return null;
        }
        Integer[] result = new Integer[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = length(target[i]);
        }
        return result;
    }

    public List<Integer> listLength(List<?> target) {
        if (target == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(length(element));
        }
        return result;
    }

    public Set<Integer> setLength(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<Integer> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(length(element));
        }
        return result;
    }

    public String replace(Object target, String before, String after) {
        if (target == null) {
            return null;
        }
        return StringUtils.replace(target, before, after);
    }

    public String[] arrayReplace(Object[] target, String before, String after) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = replace(target[i], before, after);
        }
        return result;
    }

    public List<String> listReplace(List<?> target, String before, String after) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(replace(element, before, after));
        }
        return result;
    }

    public Set<String> setReplace(Set<?> target, String before, String after) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(replace(element, before, after));
        }
        return result;
    }

    public String multipleReplace(Object target, String[] before, String[] after) {
        Validate.notNull(before, "Array of 'before' values cannot be null");
        Validate.notNull(after, "Array of 'after' values cannot be null");
        Validate.isTrue(before.length == after.length, "Arrays of 'before' and 'after' values must have the same length");
        if (target == null) {
            return null;
        }
        String ret = target.toString();
        for (int i = 0; i < before.length; i++) {
            ret = StringUtils.replace(ret, before[i], after[i]);
        }
        return ret;
    }

    public String[] arrayMultipleReplace(Object[] target, String[] before, String[] after) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = multipleReplace(target[i], before, after);
        }
        return result;
    }

    public List<String> listMultipleReplace(List<?> target, String[] before, String[] after) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(multipleReplace(element, before, after));
        }
        return result;
    }

    public Set<String> setMultipleReplace(Set<?> target, String[] before, String[] after) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(multipleReplace(element, before, after));
        }
        return result;
    }

    public String toUpperCase(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.toUpperCase(target, this.locale);
    }

    public String[] arrayToUpperCase(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = toUpperCase(target[i]);
        }
        return result;
    }

    public List<String> listToUpperCase(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(toUpperCase(element));
        }
        return result;
    }

    public Set<String> setToUpperCase(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(toUpperCase(element));
        }
        return result;
    }

    public String toLowerCase(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.toLowerCase(target, this.locale);
    }

    public String[] arrayToLowerCase(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = toLowerCase(target[i]);
        }
        return result;
    }

    public List<String> listToLowerCase(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(toLowerCase(element));
        }
        return result;
    }

    public Set<String> setToLowerCase(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(toLowerCase(element));
        }
        return result;
    }

    public String trim(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.trim(target);
    }

    public String[] arrayTrim(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = trim(target[i]);
        }
        return result;
    }

    public List<String> listTrim(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(trim(element));
        }
        return result;
    }

    public Set<String> setTrim(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(trim(element));
        }
        return result;
    }

    public String capitalize(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.capitalize(target);
    }

    public String[] arrayCapitalize(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = capitalize(target[i]);
        }
        return result;
    }

    public List<String> listCapitalize(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(capitalize(element));
        }
        return result;
    }

    public Set<String> setCapitalize(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(capitalize(element));
        }
        return result;
    }

    public String unCapitalize(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.unCapitalize(target);
    }

    public String[] arrayUnCapitalize(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = unCapitalize(target[i]);
        }
        return result;
    }

    public List<String> listUnCapitalize(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(unCapitalize(element));
        }
        return result;
    }

    public Set<String> setUnCapitalize(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(unCapitalize(element));
        }
        return result;
    }

    public String capitalizeWords(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.capitalizeWords(target);
    }

    public String[] arrayCapitalizeWords(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = capitalizeWords(target[i]);
        }
        return result;
    }

    public List<String> listCapitalizeWords(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(capitalizeWords(element));
        }
        return result;
    }

    public Set<String> setCapitalizeWords(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(capitalizeWords(element));
        }
        return result;
    }

    public String capitalizeWords(Object target, Object delimiters) {
        if (target == null) {
            return null;
        }
        return StringUtils.capitalizeWords(target, delimiters);
    }

    public String[] arrayCapitalizeWords(Object[] target, Object delimiters) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = capitalizeWords(target[i], delimiters);
        }
        return result;
    }

    public List<String> listCapitalizeWords(List<?> target, Object delimiters) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(capitalizeWords(element, delimiters));
        }
        return result;
    }

    public Set<String> setCapitalizeWords(Set<?> target, Object delimiters) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(capitalizeWords(element, delimiters));
        }
        return result;
    }

    public String escapeXml(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.escapeXml(target);
    }

    public String[] arrayEscapeXml(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = escapeXml(target[i]);
        }
        return result;
    }

    public List<String> listEscapeXml(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(escapeXml(element));
        }
        return result;
    }

    public Set<String> setEscapeXml(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(escapeXml(element));
        }
        return result;
    }

    public String escapeJavaScript(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.escapeJavaScript(target);
    }

    public String[] arrayEscapeJavaScript(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = escapeJavaScript(target[i]);
        }
        return result;
    }

    public List<String> listEscapeJavaScript(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(escapeJavaScript(element));
        }
        return result;
    }

    public Set<String> setEscapeJavaScript(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(escapeJavaScript(element));
        }
        return result;
    }

    public String unescapeJavaScript(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.unescapeJavaScript(target);
    }

    public String[] arrayUnescapeJavaScript(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = unescapeJavaScript(target[i]);
        }
        return result;
    }

    public List<String> listUnescapeJavaScript(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(unescapeJavaScript(element));
        }
        return result;
    }

    public Set<String> setUnescapeJavaScript(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(unescapeJavaScript(element));
        }
        return result;
    }

    public String escapeJava(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.escapeJava(target);
    }

    public String[] arrayEscapeJava(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = escapeJava(target[i]);
        }
        return result;
    }

    public List<String> listEscapeJava(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(escapeJava(element));
        }
        return result;
    }

    public Set<String> setEscapeJava(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(escapeJava(element));
        }
        return result;
    }

    public String unescapeJava(Object target) {
        if (target == null) {
            return null;
        }
        return StringUtils.unescapeJava(target);
    }

    public String[] arrayUnescapeJava(Object[] target) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = unescapeJava(target[i]);
        }
        return result;
    }

    public List<String> listUnescapeJava(List<?> target) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(unescapeJava(element));
        }
        return result;
    }

    public Set<String> setUnescapeJava(Set<?> target) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(unescapeJava(element));
        }
        return result;
    }

    public String randomAlphanumeric(int count) {
        return StringUtils.randomAlphanumeric(count);
    }

    public String defaultString(Object target, Object defaultValue) {
        if (target == null) {
            if (defaultValue == null) {
                return BeanDefinitionParserDelegate.NULL_ELEMENT;
            }
            return defaultValue.toString();
        }
        String targetString = target.toString();
        if (StringUtils.isEmptyOrWhitespace(targetString)) {
            if (defaultValue == null) {
                return BeanDefinitionParserDelegate.NULL_ELEMENT;
            }
            return defaultValue.toString();
        }
        return targetString;
    }

    public String[] arrayDefaultString(Object[] target, Object defaultValue) {
        if (target == null) {
            return null;
        }
        String[] result = new String[target.length];
        for (int i = 0; i < target.length; i++) {
            result[i] = defaultString(target[i], defaultValue);
        }
        return result;
    }

    public List<String> listDefaultString(List<?> target, Object defaultValue) {
        if (target == null) {
            return null;
        }
        List<String> result = new ArrayList<>(target.size() + 2);
        for (Object element : target) {
            result.add(defaultString(element, defaultValue));
        }
        return result;
    }

    public Set<String> setDefaultString(Set<?> target, Object defaultValue) {
        if (target == null) {
            return null;
        }
        Set<String> result = new LinkedHashSet<>(target.size() + 2);
        for (Object element : target) {
            result.add(defaultString(element, defaultValue));
        }
        return result;
    }
}