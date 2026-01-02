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
        this.inner = new HashSet<>();
        inner.addAll(collection);
    }

    public Set<Integer> getInner(){
        return Set.copyOf(inner);
    }

    public ImmutableSet addAll(Collection<Integer> collection){
        Set<Integer> newInner = new HashSet<>();
        newInner.addAll(this.inner);
        newInner.addAll(collection);
        return new ImmutableSet(newInner);
    }
}
