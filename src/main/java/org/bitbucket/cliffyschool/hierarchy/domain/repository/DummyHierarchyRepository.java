package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.DummyRepository;

import java.util.UUID;

public class DummyHierarchyRepository extends DummyRepository<UUID,Hierarchy> implements HierarchyRepository {

}
