package tech.units.indriya.internal.calc;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DefaultNumberSystem implements NumberSystem {

    @Override
    public Number add(Number x, Number y) {
        
        if(isInteger(x) && isInteger(y)) {
            
            // +1 carry, not including sign
            int total_bits_required = Math.max(bitLengthOfInteger(x), bitLengthOfInteger(y)) + 1; 
            
            if(total_bits_required<63) {
                return longValueOfIntegerExact(x) + longValueOfIntegerExact(y);
            }
            
            return integerToBigInteger(x).add(integerToBigInteger(y));
            
        }
        
        if((x instanceof RationalNumber) && (y instanceof RationalNumber)) {
            return ((RationalNumber) x).add((RationalNumber) y);
        }
        
        // TODO[220] certainly this can be optimized
        
        return toBigDecimal(x).add(toBigDecimal(y));
    }

    @Override
    public Number subtract(Number x, Number y) {
        return add(x, negate(y));
    }

    @Override
    public Number multiply(Number x, Number y) {
        
        if(isInteger(x) && isInteger(y)) {
            
            int total_bits_required = bitLengthOfInteger(x) + bitLengthOfInteger(y); // not including sign
            
            if(total_bits_required<63) {
                return longValueOfIntegerExact(x) * longValueOfIntegerExact(y);
            }
            
            return integerToBigInteger(x).multiply(integerToBigInteger(y));
            
        }
        
        if((x instanceof RationalNumber) && (y instanceof RationalNumber)) {
            return ((RationalNumber) x).multiply((RationalNumber) y);
        }
        
        // TODO[220] certainly this can be optimized
        
        return toBigDecimal(x).multiply(toBigDecimal(y));
    }

    @Override
    public Number divide(Number x, Number y) {
        return multiply(x, reciprocal(y));
    }

    @Override
    public Number reciprocal(Number number) {
        if(isInteger(number)) {
            return RationalNumber.of(BigInteger.ONE, integerToBigInteger(number));
        }
        if(number instanceof BigDecimal) {
            return BigDecimal.ONE.divide(((BigDecimal) number));
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).reciprocal();
        }
        if(number instanceof Double) {
            return BigDecimal.ONE.divide(BigDecimal.valueOf((double)number));
        }
        if(number instanceof Float) {
            return BigDecimal.ONE.divide(BigDecimal.valueOf(number.doubleValue()));
        }
        throw unsupportedNumberType(number);
    }

    @Override
    public Number negate(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).negate();
        }
        if(number instanceof BigDecimal) {
            return ((BigDecimal) number).negate();
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).negate();
        }
        if(number instanceof Double) {
            return -((double)number);
        }
        if(number instanceof Float) {
            return -((float)number);
        }
        if(number instanceof Long) {
            return -((long)number);
        }
        if(number instanceof Integer) {
            return -((int)number);
        }
        if(number instanceof Short) {
            return -((short)number);
        }
        if(number instanceof Byte) {
            return -((byte)number);
        }
        throw unsupportedNumberType(number);
    }
    
    @Override
    public Number narrow(Number number) {
        // TODO[220]
        return number;
    }
    
    // -- HELPER
    
    private IllegalArgumentException unsupportedNumberType(Number number) {
        final String msg = String.format("Unsupported number type '%s' in number system '%s'",
                number.getClass().getName(),
                this.getClass().getName());
        
        return new IllegalArgumentException(msg);
    }
    
    private boolean isInteger(Number number) {
        if(number instanceof Long || 
                number instanceof Integer || 
                number instanceof BigInteger || 
                number instanceof Short || 
                number instanceof Byte) {
            return true;
        }
        return false;
    }
    
    private int bitLengthOfInteger(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).bitLength();
        }
        long long_value = number.longValue(); 
        
        if(long_value == Long.MIN_VALUE) {
            return 63;
        } else {
            return Long.bitCount(Math.abs(long_value));
        }
    }
    
    /**
     * Converts this integer number to a long, checking for lost information. If the value 
     * of this number is out of the range of the long type, then an ArithmeticException is thrown.
     * @param number
     */
    private long longValueOfIntegerExact(Number number) {
        if(number instanceof BigInteger) {
            return ((BigInteger) number).longValueExact();
        }
        return number.longValue();
    }
    
    private BigInteger integerToBigInteger(Number number) {
        if(number instanceof BigInteger) {
            return (BigInteger) number;
        }
        return BigInteger.valueOf(number.longValue());
    }
    
    private BigDecimal toBigDecimal(Number number) {
        if(number instanceof BigDecimal) {
            return (BigDecimal) number;
        }
        if(number instanceof BigInteger) {
            return new BigDecimal((BigInteger) number);
        }
        if(number instanceof Long || 
                number instanceof Integer || 
                number instanceof Short || 
                number instanceof Byte) {
            return BigDecimal.valueOf(number.longValue());
        }
        if(number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if(number instanceof RationalNumber) {
            return ((RationalNumber) number).bigDecimalValue();
        }
        throw unsupportedNumberType(number);
    }
    

}
