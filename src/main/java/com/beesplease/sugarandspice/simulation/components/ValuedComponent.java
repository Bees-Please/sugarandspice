package com.beesplease.sugarandspice.simulation.components;

import javax.annotation.Nonnull;
import java.util.function.Function;

public abstract class ValuedComponent<T> extends Component {
    private Function<Double, T> valueFunction;

    // caching
    private double cachedValueAt = -1;
    private T cachedValue = null;

    // used for constant values
    public ValuedComponent(T value, int... nodes) {
        super(nodes);
        this.valueFunction = t -> value;
    }

    // used for controlled values
    public ValuedComponent(Function<Double, T> value, int... nodes) {
        super(nodes);
        this.valueFunction = value;
    }

    // cached value gathering
    @Nonnull
    public T getValue(double simulationTimeStep) {
        if (simulationTimeStep > cachedValueAt) {
            cachedValueAt = simulationTimeStep;
            cachedValue = valueFunction.apply(simulationTimeStep);
        }
        if (cachedValue == null) {
            throw new NullPointerException("Caching exception: component has null value cached");
        }
        return cachedValue;
    }

    public void setValueFunction(Function<Double, T> valueFunction) {
        this.valueFunction = valueFunction;
    }
}
