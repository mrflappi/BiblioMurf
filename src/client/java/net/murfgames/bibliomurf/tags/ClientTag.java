package net.murfgames.bibliomurf.tags;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.Identifier;

public class ClientTag {
    public Set<Identifier> values;

    public Set<Identifier> getValues() {
        return Collections.unmodifiableSet(values);
    }

    public boolean contains(Identifier entry) {
        return values.contains(entry);
    }
}
