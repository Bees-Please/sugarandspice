package com.beesplease.sugarandspice.simulation.components;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class ValuedComponent<T> extends Component {
    private Supplier<T> valueSupplier;

    // caching
    private double cachedValueAt = -1;
    private T cachedValue = null;

    // used for constant values
    public ValuedComponent(T value, int... nodes) {
        super(nodes);
        this.valueSupplier = () -> value;
    }

    // used for controlled values
    public ValuedComponent(Supplier<T> value, int... nodes) {
        super(nodes);
        this.valueSupplier = value;
    }

    // Warning: Not lazy! Use cautiously! The Supplier might be expensive to run!
    public T getValue() {
        return valueSupplier.get();
    }

    // cached value gathering
    @Nonnull
    public T getValue(double simulationTimeStep) {
        if (simulationTimeStep > cachedValueAt) {
            cachedValueAt = simulationTimeStep;
            cachedValue = getValue();
        }
        if (cachedValue == null) {
            throw new NullPointerException("Caching exception: component has null value cached");
        }
        return cachedValue;
    }

    public void setValueSupplier(Supplier<T> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }
}
