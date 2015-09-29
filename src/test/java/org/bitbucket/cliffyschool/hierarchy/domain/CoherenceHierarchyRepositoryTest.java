package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.repository.CoherenceHierarchyRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CoherenceHierarchyRepositoryTest {

    private HierarchyRepository hierarchyRepository;

    @Before
    public void setUp(){
        hierarchyRepository = new CoherenceHierarchyRepository();
    }

    @Test
    public void hierarchyCanBeSavedAndRetrieved(){

        Hierarchy hierarchy = Hierarchy.createHierarchy(UUID.randomUUID());
        Node node = Node.createNode(new CreateNodeCommand(hierarchy.getId(), hierarchy.getVersionId(), UUID.randomUUID(),
                "a Node", "blue", Optional.empty()));
        Node childNode = Node.createNode(new CreateNodeCommand(hierarchy.getId(), hierarchy.getVersionId(), UUID.randomUUID(),
                "a child Node", "blue", Optional.of(node.getId())));
        hierarchy.insertNode(Optional.of(node), childNode);

        hierarchyRepository.store(hierarchy.getId(), hierarchy, 0);

        Optional<Hierarchy> retrieved = hierarchyRepository.findById(hierarchy.getId());

        assertThat(retrieved.isPresent()).isTrue();
        assertThat(retrieved.get()).isEqualTo(hierarchy);

    }
}
