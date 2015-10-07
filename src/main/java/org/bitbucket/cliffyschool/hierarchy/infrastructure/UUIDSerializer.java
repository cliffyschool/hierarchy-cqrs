package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;
import java.util.UUID;

public class UUIDSerializer implements PofSerializer {
    @Override
    public void serialize(PofWriter pofWriter, Object o) throws IOException {
        pofWriter.writeString(0, ((UUID)o).toString());
        pofWriter.writeRemainder(null);
    }

    @Override
    public Object deserialize(PofReader pofReader) throws IOException {
        String uuidString = pofReader.readString(0);
        return UUID.fromString(uuidString);
    }
}
