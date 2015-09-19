package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.cqrs.EventStream;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.InMemoryHierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNamedChanged;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class InMemoryHierarchyRepositoryTest {
    private InMemoryHierarchyRepository hierarchyRepository = new InMemoryHierarchyRepository();
    private UUID firstNodeId;
    private UUID hierarchyId;
    private List<Event> eventList;

    @Before
    public void setUp()
    {
        hierarchyId = UUID.randomUUID();
        firstNodeId = UUID.randomUUID();
        eventList = Lists.newArrayList(
                new HierarchyCreated(hierarchyId, 1L),
                new NodeCreated(hierarchyId, 1L, firstNodeId, "node1", "", ""),
                new NodeCreated(hierarchyId, 1L, UUID.randomUUID(), "node2", "", ""),
                new NodeNamedChanged(hierarchyId, 1L, firstNodeId, "Node 1"));
    }


    @Test
    public void whenHierarchyIsSavedItCanBeFound(){
        hierarchyRepository.store(hierarchyId, new EventStream(eventList));

        Optional<Hierarchy> found = hierarchyRepository.findById(hierarchyId);

        assertThat(found.isPresent());
    }

    @Test
    public void whenHierarchyIsFoundNodeIsPresent(){
        hierarchyRepository.store(hierarchyId, new EventStream(eventList));

        Hierarchy found = hierarchyRepository.findById(hierarchyId).get();

        assertThat(found.nodeById(firstNodeId)).isNotNull();
    }

    @Test
    public void whenHierarchyNodeIsFoundItHasCorrectName(){
        hierarchyRepository.store(hierarchyId, new EventStream(eventList));

        Hierarchy found = hierarchyRepository.findById(hierarchyId).get();

        assertThat(found.nodeById(firstNodeId).getName()).isEqualTo("Node 1");
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenEventStreamVersionIsStaleThenShouldThrowException(){
        UUID nodeId = UUID.randomUUID();
        hierarchyRepository.store(hierarchyId, new EventStream(Lists.newArrayList(new HierarchyCreated(hierarchyId, 0L))));
        hierarchyRepository.store(hierarchyId, new EventStream(Lists.newArrayList(new NodeCreated(hierarchyId, 1L, nodeId, "nodeName", "nodeColor", "nodeShape"))));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("updated"));
        hierarchyRepository.store(hierarchyId, new EventStream(Lists.newArrayList(new NodeCreated(hierarchyId, 1L, nodeId, "otherNodeName", "nodeColor", "nodeShape"))));
    }

    @Test
    public void whenGivenVersionIdIsNotContiguousThenThrowException(){
        UUID nodeId = UUID.randomUUID();
        hierarchyRepository.store(hierarchyId, new EventStream(Lists.newArrayList(new HierarchyCreated(hierarchyId, 0L))));
        hierarchyRepository.store(hierarchyId, new EventStream(Lists.newArrayList(new NodeCreated(hierarchyId, 1L, nodeId, "nodeName", "nodeColor", "nodeShape"))));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("skip"));
        hierarchyRepository.store(hierarchyId, new EventStream(Lists.newArrayList(new NodeCreated(hierarchyId, 3L, nodeId, "otherNodeName", "nodeColor", "nodeShape"))));

    }
}
