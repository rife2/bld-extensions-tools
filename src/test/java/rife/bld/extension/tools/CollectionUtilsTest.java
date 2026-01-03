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
@SuppressWarnings( "PMD.AvoidDuplicateLiterals")
class CollectionUtilsTest {
    @Nested
    @DisplayName("isEmpty()")
    class IsEmptyTest {
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

        @ParameterizedTest
        @DisplayName("should return false for non-empty collections")
        @MethodSource("nonEmptyCollections")
        void shouldReturnFalseForNonEmptyCollections(Collection<?> collection) {
            assertFalse(CollectionUtils.isEmpty(collection));
        }

        @ParameterizedTest
        @DisplayName("should return true for empty collections")
        @MethodSource("emptyCollections")
        void shouldReturnTrueForEmptyCollections(Collection<?> collection) {
            assertTrue(CollectionUtils.isEmpty(collection));
        }
    }

    @Nested
    @DisplayName("isEmpty(Collection<?>...)")
    class IsEmptyVarargsTest {
        @Test
        @DisplayName("should return true when all collections are empty")
        void shouldReturnTrueWhenAllCollectionsAreEmpty() {
            assertTrue(CollectionUtils.isEmpty(
                    new ArrayList<>(),
                    new HashSet<>(),
                    Collections.emptyList()
            ));
        }

        @Test
        @DisplayName("should return true when all collections are null")
        void shouldReturnTrueWhenAllCollectionsAreNull() {
            assertTrue(CollectionUtils.isEmpty(null, null, null));
        }

        @Test
        @DisplayName("should return true when mix of null and empty collections")
        void shouldReturnTrueWhenMixOfNullAndEmpty() {
            assertTrue(CollectionUtils.isEmpty(
                    null,
                    new ArrayList<>(),
                    null,
                    Collections.emptySet()
            ));
        }

        @Test
        @DisplayName("should return false when any collection is not empty")
        void shouldReturnFalseWhenAnyCollectionIsNotEmpty() {
            assertFalse(CollectionUtils.isEmpty(
                    new ArrayList<>(),
                    List.of("element"),
                    new HashSet<>()
            ));
        }

        @Test
        @DisplayName("should return false when first collection is not empty")
        void shouldReturnFalseWhenFirstCollectionIsNotEmpty() {
            assertFalse(CollectionUtils.isEmpty(
                    List.of("element"),
                    new ArrayList<>(),
                    null
            ));
        }

        @Test
        @DisplayName("should return false when last collection is not empty")
        void shouldReturnFalseWhenLastCollectionIsNotEmpty() {
            assertFalse(CollectionUtils.isEmpty(
                    null,
                    new ArrayList<>(),
                    Set.of(1, 2, 3)
            ));
        }

        @Test
        @DisplayName("should return true when no collections provided")
        void shouldReturnTrueWhenNoCollectionsProvided() {
            assertTrue(CollectionUtils.isEmpty());
        }

        @Test
        @DisplayName("should handle single collection")
        void shouldHandleSingleCollection() {
            assertTrue(CollectionUtils.isEmpty(new ArrayList<>()));
            assertFalse(CollectionUtils.isEmpty(List.of("item")));
        }
    }

    @Nested
    @DisplayName("isNotEmpty()")
    class IsNotEmptyTest {
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
    }

    @Nested
    @DisplayName("isNotEmpty(Collection<?>...)")
    class IsNotEmptyVarargsTest {
        @Test
        @DisplayName("should return false when all collections are empty")
        void shouldReturnFalseWhenAllCollectionsAreEmpty() {
            assertFalse(CollectionUtils.isNotEmpty(
                    new ArrayList<>(),
                    new HashSet<>(),
                    Collections.emptyList()
            ));
        }

        @Test
        @DisplayName("should return false when all collections are null")
        void shouldReturnFalseWhenAllCollectionsAreNull() {
            assertFalse(CollectionUtils.isNotEmpty(null, null, null));
        }

        @Test
        @DisplayName("should return false when mix of null and empty collections")
        void shouldReturnFalseWhenMixOfNullAndEmpty() {
            assertFalse(CollectionUtils.isNotEmpty(
                    null,
                    new ArrayList<>(),
                    null,
                    Collections.emptySet()
            ));
        }

        @Test
        @DisplayName("should return true when any collection is not empty")
        void shouldReturnTrueWhenAnyCollectionIsNotEmpty() {
            assertTrue(CollectionUtils.isNotEmpty(
                    new ArrayList<>(),
                    List.of("element"),
                    new HashSet<>()
            ));
        }

        @Test
        @DisplayName("should return true when first collection is not empty")
        void shouldReturnTrueWhenFirstCollectionIsNotEmpty() {
            assertTrue(CollectionUtils.isNotEmpty(
                    List.of("element"),
                    new ArrayList<>(),
                    null
            ));
        }

        @Test
        @DisplayName("should return true when last collection is not empty")
        void shouldReturnTrueWhenLastCollectionIsNotEmpty() {
            assertTrue(CollectionUtils.isNotEmpty(
                    null,
                    new ArrayList<>(),
                    Set.of(1, 2, 3)
            ));
        }

        @Test
        @DisplayName("should return false when no collections provided")
        void shouldReturnFalseWhenNoCollectionsProvided() {
            assertFalse(CollectionUtils.isNotEmpty());
        }

        @Test
        @DisplayName("should handle single collection")
        void shouldHandleSingleCollection() {
            assertFalse(CollectionUtils.isNotEmpty(new ArrayList<>()));
            assertTrue(CollectionUtils.isNotEmpty(List.of("item")));
        }

        @Test
        @DisplayName("should return true when multiple collections are not empty")
        void shouldReturnTrueWhenMultipleCollectionsAreNotEmpty() {
            assertTrue(CollectionUtils.isNotEmpty(
                    List.of("a"),
                    Set.of(1, 2),
                    Arrays.asList("x", "y", "z")
            ));
        }
    }

    @Nested
    @DisplayName("isEmpty() and isNotEmpty() logical consistency")
    class LogicalConsistencyTest {
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

        @ParameterizedTest
        @DisplayName("should be logical inverses for all collection states")
        @MethodSource("allCollectionStates")
        void shouldBeLogicalInverses(Collection<?> collection) {
            var empty = CollectionUtils.isEmpty(collection);
            var notEmpty = CollectionUtils.isNotEmpty(collection);
            assertEquals(!empty, notEmpty, "isEmpty and isNotEmpty should be logical inverses");
        }

        @Test
        @DisplayName("varargs isEmpty and isNotEmpty should be logical inverses")
        void varargsMethodsShouldBeLogicalInverses() {
            // Test case 1: all empty
            var empty1 = CollectionUtils.isEmpty(new ArrayList<>(), new HashSet<>());
            var notEmpty1 = CollectionUtils.isNotEmpty(new ArrayList<>(), new HashSet<>());
            assertEquals(!empty1, notEmpty1, "Should be logical inverses for all empty");

            // Test case 2: one non-empty
            var empty2 = CollectionUtils.isEmpty(List.of("a"), new ArrayList<>());
            var notEmpty2 = CollectionUtils.isNotEmpty(List.of("a"), new ArrayList<>());
            assertEquals(!empty2, notEmpty2, "Should be logical inverses for one non-empty");

            // Test case 3: all null
            var empty3 = CollectionUtils.isEmpty(null, null);
            var notEmpty3 = CollectionUtils.isNotEmpty(null, null);
            assertEquals(!empty3, notEmpty3, "Should be logical inverses for all null");

            // Test case 4: mix of null and non-empty
            var empty4 = CollectionUtils.isEmpty(null, List.of(1));
            var notEmpty4 = CollectionUtils.isNotEmpty(null, List.of(1));
            assertEquals(!empty4, notEmpty4, "Should be logical inverses for mixed");

            // Test case 5: no arguments
            var empty5 = CollectionUtils.isEmpty();
            var notEmpty5 = CollectionUtils.isNotEmpty();
            assertEquals(!empty5, notEmpty5, "Should be logical inverses for no args");
        }
    }
}