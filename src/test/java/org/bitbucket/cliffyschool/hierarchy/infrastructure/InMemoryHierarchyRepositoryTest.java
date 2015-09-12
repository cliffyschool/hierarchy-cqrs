package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryHierarchyRepositoryTest {
    private HierarchyRepository hierarchyRepository = new InMemoryHierarchyRepository();
    private Hierarchy hierarchy;

    @Before
    public void setUp()
    {
        hierarchy = new Hierarchy(UUID.randomUUID());
        hierarchy.apply(new CreateNodeCommand("myNode", "blue", "abc"));
    }


    @Test
    public void whenHierarchyIsSavedItCanBeFound(){
        hierarchyRepository.save(hierarchy);

        Optional<Hierarchy> found = hierarchyRepository.findById(hierarchy.getId());

        assertThat(found.isPresent());
    }
}
