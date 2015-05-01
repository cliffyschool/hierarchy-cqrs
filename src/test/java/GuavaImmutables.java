import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class GuavaImmutables {

    private class Node {
        private int id;
        private String name;

        public Node(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
    }

    @Test
    public void givenAnImmutableSet_whenAppended_thenOriginalReferencesAreMaintained() {
        ImmutableSet<Node> originalSet = ImmutableSet.<Node>builder()
                .add(new Node(1, "first"))
                .add(new Node(2, "second"))
                .add(new Node(3, "third"))
                .add(new Node(4, "fourth"))
                .build();

        ImmutableSet<Node> modifiedSet = ImmutableSet.<Node>builder()
                .addAll(originalSet)
                .add(new Node(5, "fifth"))
                .build();
        Node node1InOriginalSet = Iterables.find(originalSet, p -> p.getId() == 1);
        Node node1InModifiedSet = Iterables.find(modifiedSet, p -> p.getId() == 1);

        // compares objects by reference (not equality)
        assertThat(node1InOriginalSet).isSameAs(node1InModifiedSet);
    }

    @Test
    public void givenAnImmutableMap_whenEntriesAreNotChanged_thenOriginalReferencesAreMaintained() {
        ImmutableMap<Integer,Node> originalMap = ImmutableMap.<Integer,Node>builder()
                .put(1, new Node(1, "first"))
                .put(2, new Node(2, "second"))
                .put(3, new Node(3, "third"))
                .build();

        ImmutableMap<Integer,Node> modifiedMap = ImmutableMap.<Integer,Node>builder().putAll(originalMap)
                .put(4, new Node(4, "fourth"))
                .build();

        assertThat(modifiedMap.get(2)).isSameAs(originalMap.get(2));
    }

    @Test
    public void givenAnImmutableMap_whenEntriesAreChanged_thenOriginalReferencesAreNotMaintained() {
        ImmutableMap<Integer,Node> originalMap = ImmutableMap.<Integer,Node>builder()
                .put(1, new Node(1, "first"))
                .put(2, new Node(2, "second"))
                .put(3, new Node(3, "third"))
                .build();

        ImmutableMap<Integer,Node> modifiedMap = ImmutableMap.<Integer,Node>builder()
                .put(1, originalMap.get(1))
                .put(2, new Node(2, "2nd"))
                .put(3, originalMap.get(3))
                .build();

        assertThat(modifiedMap.get(2)).isNotSameAs(originalMap.get(2));
    }
}
