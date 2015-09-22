package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.InMemoryHierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
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
    private EventStream eventStream;

    @Before
    public void setUp()
    {
        hierarchyId = UUID.randomUUID();
        firstNodeId = UUID.randomUUID();
        List<Event> eventList = Lists.newArrayList(
                new HierarchyCreated(hierarchyId),
                new NodeCreated(hierarchyId, firstNodeId, "node1", ""),
                new NodeCreated(hierarchyId, UUID.randomUUID(), "node2", ""),
                new NodeNameChanged(hierarchyId, firstNodeId, Optional.empty(), "Node 1"));
        eventStream = EventStream.from(eventList);
    }


    @Test
    public void whenHierarchyIsSavedItCanBeFound(){
        hierarchyRepository.store(hierarchyId, eventStream, 0L);

        Optional<Hierarchy> found = hierarchyRepository.findById(hierarchyId);

        assertThat(found.isPresent());
    }

    @Test
    public void whenHierarchyIsFoundNodeIsPresent(){
        hierarchyRepository.store(hierarchyId, eventStream, 0L);

        Hierarchy found = hierarchyRepository.findById(hierarchyId).get();

        assertThat(found.nodeById(firstNodeId)).isNotNull();
    }

    @Test
    public void whenHierarchyNodeIsFoundItHasCorrectName(){
        hierarchyRepository.store(hierarchyId, eventStream, 0L);

        Hierarchy found = hierarchyRepository.findById(hierarchyId).get();

        assertThat(found.nodeById(firstNodeId).getName()).isEqualTo("Node 1");
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenEventStreamVersionIsStaleThenStoreShouldThrowException(){
        UUID nodeId = UUID.randomUUID();
        hierarchyRepository.store(hierarchyId, EventStream.from(Lists.newArrayList(new HierarchyCreated(hierarchyId))), 0L);
        hierarchyRepository.store(hierarchyId, EventStream.from(Lists.newArrayList(new NodeCreated(hierarchyId, nodeId, "nodeName", "nodeColor"))), 1L);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("updated"));
        hierarchyRepository.store(hierarchyId, EventStream.from(Lists.newArrayList(new NodeCreated(hierarchyId, nodeId, "otherNodeName", "nodeColor"))), 1L);
    }

    @Test
    public void whenVersionIdsAreNotContiguousThenStoreThrowException(){
        UUID nodeId = UUID.randomUUID();
        hierarchyRepository.store(hierarchyId, EventStream.from(Lists.newArrayList(new HierarchyCreated(hierarchyId))), 0L);
        hierarchyRepository.store(hierarchyId, EventStream.from(Lists.newArrayList(new NodeCreated(hierarchyId, nodeId, "nodeName", "nodeColor"))), 1L);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("skip"));
        hierarchyRepository.store(hierarchyId, EventStream.from(Lists.newArrayList(new NodeCreated(hierarchyId, nodeId, "otherNodeName", "nodeColor"))), 3L);
    }
}
