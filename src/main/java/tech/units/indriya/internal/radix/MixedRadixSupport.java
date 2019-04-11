package tech.units.indriya.internal.radix;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Consumer;

import tech.units.indriya.format.MixedRadix;
import tech.units.indriya.function.Calculus;

/**
 * Internal utility class to support {@link MixedRadix}.
 * 
 * @author Andi Huber
 * @since 2.0
 */
public class MixedRadixSupport {

    private final Radix[] radices;
    private final MathContext mc;

    /**
     * 
     * @param radices - most significant first
     */
    public MixedRadixSupport(Radix[] radices) {
        this.radices = radices;
        this.mc = new MathContext(Calculus.MATH_CONTEXT.getPrecision(), RoundingMode.FLOOR);
    }
    
    /**
     * 
     * @param trailingRadixValue
     * @param numberVisitor - gets past over the extracted numbers in least significant first order
     */
    public void visitRadixNumbers(Number trailingRadixValue, Consumer<Number> numberVisitor) {
        
        Number total = trailingRadixValue;
        
        for(int i=0;i<radices.length;++i) {
            
            Radix radix = radices[invertIndex(i)];
            
            boolean fractionalRemainder = i==0;
            
            Number[] divideAndRemainder = radix.divideAndRemainder(total, mc, fractionalRemainder); 
            
            Number remainder = divideAndRemainder[1];
            
            numberVisitor.accept(remainder);

            total = divideAndRemainder[0];
            
        }
        
        numberVisitor.accept(total);
        
    }

    public Number sumMostSignificant(Number[] values) {

        int maxAllowedValueIndex = values.length - 1; 
        
        Number sum = null;
        
        for(int i=0;i<radices.length;++i) {
            
            if(sum==null) {
                sum = values[0];
            }
            
            sum = radices[i].multiply(sum);
            
            if(i >= maxAllowedValueIndex) {
                continue;
            }
            
            sum = Calculus.plus(sum, values[i+1]);    
            
        }
        
        return sum;
        
    }
    
    // -- HELPER
    
    private int invertIndex(int index) {
        return radices.length - index - 1;
    }
    
    
    
}
