package org.bitbucket.cliffyschool.hierarchy.domain;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

public class HierarchySerializer implements PofSerializer {

    @Override
    public void serialize(PofWriter pofWriter, Object o) throws IOException {
        /*
        HierarchyImpl hierarchy = (HierarchyImpl ) o;

        pofWriter.writeString(0, hierarchy.getId().toString());
        List<String> nodes = hierarchy.nodesById.entrySet().stream()
                .map(e -> e.getKey().toString())
                .collect(Collectors.toList());
        pofWriter.writeCollection(1, nodes);

        Set<Map.Entry<String,String>> nodesByName =hierarchy.nodesByName.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().toString()))
                .collect(Collectors.toSet());
        pofWriter.writeCollection(2, nodesByName);

        List<Map.Entry<String,String>> childrenByParentId = hierarchy.childrenByParentId.entries().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey().toString(), e.getValue().toString()))
                .collect(Collectors.toList());
        pofWriter.writeCollection(3, childrenByParentId);

        pofWriter.writeRemainder(null);
        */
    }

    @Override
    public Object deserialize(PofReader pofReader) throws IOException {
        /*
        UUID id = UUID.fromString(pofReader.readString(0));
        HierarchyImpl hierarchy = new HierarchyImpl(id);

        List<String> nodes = (List<String>)pofReader.readCollection(1, new ArrayList<String>());
        nodes.stream().forEach(str -> {
            UUID nodeId = UUID.fromString(str);
            hierarchy.nodesById.put(nodeId, nodeId);
        });

        List<Map.Entry<String,String>> nodeNames = (List<Map.Entry<String,String>>)pofReader.readCollection(2,
                new ArrayList<Map.Entry<String,String>>());
        nodeNames.stream().forEach(entry -> {
            hierarchy.nodesByName.put(entry.getKey(), UUID.fromString(entry.getValue()));
        });

        List<Map.Entry<String,String>> childrenByParentId = (List<Map.Entry<String,String>>)pofReader.readCollection(3,
                new ArrayList<Map.Entry<String,String>>());
        childrenByParentId.forEach(entry -> {
            hierarchy.childrenByParentId.put(UUID.fromString(entry.getKey()), UUID.fromString(entry.getValue()));
        });

        pofReader.readRemainder();

        return hierarchy;
        */
        return null;
    }
}
