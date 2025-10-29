package net.murfgames.bibliomurf.tags;

import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientTag {
    public Set<Identifier> values;

    public Set<Identifier> getValues() {
        return Collections.unmodifiableSet(values);
    }

    public boolean contains(Identifier entry) {
        return values.contains(entry);
    }
}
