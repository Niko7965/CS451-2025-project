package cs451.LatticeAgreement;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ImmutableSet implements Serializable {

    private final Set<Integer> inner;

    public ImmutableSet(){
        this.inner = new HashSet<>();
    }

    public ImmutableSet(Collection<Integer> collection){
        this.inner = new HashSet<>(collection);
    }

    public Set<Integer> getInner(){
        return Set.copyOf(inner);
    }

    public ImmutableSet addAll(Collection<Integer> collection){
        Set<Integer> inner = new HashSet<>();
        inner.addAll(this.inner);
        inner.addAll(collection);
        return new ImmutableSet(inner);
    }
}
