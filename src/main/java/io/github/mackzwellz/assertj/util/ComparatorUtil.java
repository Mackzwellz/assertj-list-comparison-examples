package io.github.mackzwellz.assertj.util;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ComparatorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ComparatorUtil.class);

    public static boolean equals(Object a, Object b, String field) {
        LOG.info("{} verification: Expected --> {} vs actual --> {}", field, a, b);
        boolean result;
        if (a instanceof List) {
            return listEquals((List) a, (List) b);
        } else {
            result = Objects.equals(a, b);
        }
        if (!result) {
            LOG.error("Objects are not equal to each other!");
        }
        return result;
    }

    /**
     * Comparing two Lists of DTO's.
     * Assuming the lists are already sorted.
     *
     * @param a first list of DTO to be compared
     * @param b second list of DTO to be compared
     * @return result of verification
     */
    public static <T> boolean listEquals(List<T> a, List<T> b) {
        LOG.info("Comparing two Lists of DTO's");
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            LOG.debug("List sizes are not equal");
            return false;
        }
        LOG.info("First List {}", a);
        LOG.info("Second List {}", b);
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                LOG.info("{} element from first list:\n '{}' \nis not equal to element from the second list:\n '{}'",
                        i, a.get(i), b.get(i));
                return false;
            }
        }
        return true;
    }

    /**
     * TODO rework to return the diff and compare at higher level?
     *
     * @param expected
     * @param actual
     * @return true if maps are equal, throws SoftAssertionError otherwise
     */
    public static <K, V> boolean mapEquals(Map<K, V> expected, Map<K, V> actual) {
        Assertions.assertThat(actual.keySet())
                .describedAs("Mismatch of map keys")
                .isEqualTo(expected.keySet());
        try (AutoCloseableSoftAssertions softAssertion = new AutoCloseableSoftAssertions()) {
            for (K key : actual.keySet()) {
                LOG.debug("Comparing key: " + key);
                V expectedValue = expected.get(key);
                V actualValue = actual.get(key);
                LOG.debug("expected value: {}", expectedValue);
                LOG.debug("actual value: {}", actualValue);
                softAssertion.assertThat(actualValue)
                        .describedAs("Value mismatch for key %s", key)
                        .isEqualTo(expectedValue);
            }
        }
        return true;
    }

    public static boolean mapEquals(Map<String, List<?>> expected, Map<String, List<?>> actual, List<String> ignoreList) {
        if (expected.size() != actual.size()) {
            return false;
        }
        if (!expected.keySet().equals(actual.keySet())) {
            return false;
        }
        for (String key : actual.keySet()) {
            LOG.info("Comparing key : " + key);
            if (!ignoreList.contains(key)) {
                if (expected.get(key).size() != actual.get(key).size()) {
                    LOG.info("Comparing key size {} vs {}: ", expected.get(key).size(), actual.get(key).size());
                    return false;
                }
                LOG.info("Expected values {}", expected.get(key));
                LOG.info("Actual values {}", actual.get(key));
                if (!(new HashSet<>(expected.get(key)).equals(new HashSet<>(actual.get(key))))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Comparing two Lists of DTO's.
     * Assuming the lists are already sorted.
     *
     * @param a first list of DTO to be compared
     * @param b second list of DTO to be compared
     * @return result of verification
     */
    public static <T extends Comparable<? super T>> int listCompareTo(List<T> a, List<T> b) {
        LOG.info("Comparing two Lists of DTO's");
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        if (a.size() < b.size()) {
            LOG.debug("List sizes are not equal, first is smaller");
            return 1;
        }
        if (a.size() > b.size()) {
            LOG.debug("List sizes are not equal, first is larger");
            return -1;
        }
        LOG.info("First List {}", a);
        LOG.info("Second List {}", b);
        int result = 0;
        for (int i = 0; i < a.size(); i++) {
            T aListItem = a.get(i);
            T bListItem = b.get(i);
            result = nullSafeCompareTo(aListItem, bListItem);
            if (result != 0) {
                LOG.info("{} element from first list:\n '{}' \nis not equal to element from the second list:\n '{}'",
                        i, aListItem, bListItem);
                return result;
            }
        }
        return result;
    }

    public static <T extends Comparable<? super T>> int nullSafeCompareTo(T expectedValue, T actualValue) {
        if (expectedValue == actualValue) return 0;
        if (expectedValue == null) return -1;
        if (actualValue == null) return 1;
        return expectedValue.compareTo(actualValue);
    }


}