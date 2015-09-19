package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.DummyProjectionStore;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HierarchyAsGridProjection extends DummyProjectionStore<HierarchyAsGrid>{
}
