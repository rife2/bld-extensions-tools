package rife.bld.extension.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CollectionUtils")
class CollectionUtilsTest {
    @Nested
    @DisplayName("isEmpty()")
    class IsEmptyTest {
        @Test
        @DisplayName("should return true for null collection")
        @SuppressWarnings("ConstantValue")
        void shouldReturnTrueForNull() {
            assertTrue(CollectionUtils.isEmpty(null));
        }

        @ParameterizedTest
        @DisplayName("should return true for empty collections")
        @MethodSource("emptyCollections")
        void shouldReturnTrueForEmptyCollections(Collection<?> collection) {
            assertTrue(CollectionUtils.isEmpty(collection));
        }

        @ParameterizedTest
        @DisplayName("should return false for non-empty collections")
        @MethodSource("nonEmptyCollections")
        void shouldReturnFalseForNonEmptyCollections(Collection<?> collection) {
            assertFalse(CollectionUtils.isEmpty(collection));
        }

        static Stream<Arguments> emptyCollections() {
            return Stream.of(
                    Arguments.of(new ArrayList<>()),
                    Arguments.of(new HashSet<>()),
                    Arguments.of(new LinkedList<>()),
                    Arguments.of(Collections.emptyList()),
                    Arguments.of(Collections.emptySet())
            );
        }

        static Stream<Arguments> nonEmptyCollections() {
            return Stream.of(
                    Arguments.of(List.of("element")),
                    Arguments.of(Set.of(1, 2, 3)),
                    Arguments.of(Arrays.asList("a", "b")),
                    Arguments.of(new ArrayList<>(List.of("item"))),
                    Arguments.of(new HashSet<>(Set.of(42)))
            );
        }
    }

    @Nested
    @DisplayName("isNotEmpty()")
    class IsNotEmptyTest {
        @Test
        @DisplayName("should return false for null collection")
        @SuppressWarnings("ConstantValue")
        void shouldReturnFalseForNull() {
            assertFalse(CollectionUtils.isNotEmpty(null));
        }

        @ParameterizedTest
        @DisplayName("should return false for empty collections")
        @MethodSource("emptyCollections")
        void shouldReturnFalseForEmptyCollections(Collection<?> collection) {
            assertFalse(CollectionUtils.isNotEmpty(collection));
        }

        @ParameterizedTest
        @DisplayName("should return true for non-empty collections")
        @MethodSource("nonEmptyCollections")
        void shouldReturnTrueForNonEmptyCollections(Collection<?> collection) {
            assertTrue(CollectionUtils.isNotEmpty(collection));
        }

        static Stream<Arguments> emptyCollections() {
            return Stream.of(
                    Arguments.of(new ArrayList<>()),
                    Arguments.of(new HashSet<>()),
                    Arguments.of(new LinkedList<>()),
                    Arguments.of(Collections.emptyList()),
                    Arguments.of(Collections.emptySet())
            );
        }

        static Stream<Arguments> nonEmptyCollections() {
            return Stream.of(
                    Arguments.of(List.of("element")),
                    Arguments.of(Set.of(1, 2, 3)),
                    Arguments.of(Arrays.asList("a", "b")),
                    Arguments.of(new ArrayList<>(List.of("item"))),
                    Arguments.of(new HashSet<>(Set.of(42)))
            );
        }
    }

    @Nested
    @DisplayName("isEmpty() and isNotEmpty() logical consistency")
    class LogicalConsistencyTest {
        @ParameterizedTest
        @DisplayName("should be logical inverses for all collection states")
        @MethodSource("allCollectionStates")
        void shouldBeLogicalInverses(Collection<?> collection) {
            var empty = CollectionUtils.isEmpty(collection);
            var notEmpty = CollectionUtils.isNotEmpty(collection);
            assertEquals(!empty, notEmpty, "isEmpty and isNotEmpty should be logical inverses");
        }

        static Stream<Arguments> allCollectionStates() {
            return Stream.of(
                    Arguments.of((Collection<?>) null),
                    Arguments.of(new ArrayList<>()),
                    Arguments.of(List.of("element")),
                    Arguments.of(new HashSet<>()),
                    Arguments.of(Set.of(1, 2, 3)),
                    Arguments.of(Collections.emptyList()),
                    Arguments.of(Arrays.asList("a", "b", "c"))
            );
        }
    }
}