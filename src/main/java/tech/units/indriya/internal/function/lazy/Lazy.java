package tech.units.indriya.internal.function.lazy;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Holder of an instance of type T, supporting the <em>compute-if-absent</em> idiom in a thread-safe manner.
 * <p>
 * Not serializable!     
 * 
 * @author Andi Huber
 * @since 2.0.3
 */
public class Lazy<T> {

    private final Supplier<? extends T> supplier;
    private T value;
    private boolean memoized;

    public Lazy(Supplier<? extends T> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier is required");
    }

    public boolean isMemoized() {
        synchronized (this) {
            return memoized;    
        }
    }

    public void clear() {
        synchronized (this) {
            this.memoized = false;
            this.value = null;
        }
    }

    public T get() {
        synchronized (this) {
            if(memoized) {
                return value;
            }
            memoized = true;
            return value = supplier.get();    
        }
    }
    
    public void set(T value) {
        synchronized (this) {
            if(memoized) {
                throw new IllegalStateException(
                        String.format("cannot set value '%s' on Lazy that has already memoized a value", ""+value));
            }
            memoized = true;
            this.value = value;
        }
    }

}