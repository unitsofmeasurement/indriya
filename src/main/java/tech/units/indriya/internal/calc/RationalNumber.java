package tech.units.indriya.internal.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import tech.units.indriya.function.Calculus;

public class RationalNumber extends Number {
    
    private static final long serialVersionUID = 1L;
    private final Object $lock = new Object[0]; // serializable lock (lombok style)
    
    private final int signum;
    private final BigInteger absDividend;
    private final BigInteger absDivisor;
    private transient BigDecimal divisionResult;

    public final static RationalNumber ZERO = ofWholeNumber(BigInteger.ZERO);
    public final static RationalNumber ONE = ofWholeNumber(BigInteger.ONE);
    
    public static RationalNumber ofWholeNumber(BigInteger number) {
        Objects.requireNonNull(number);
        return new RationalNumber(number.signum(), number.abs(), BigInteger.ONE);
    }
    
    public static RationalNumber of(BigInteger dividend, BigInteger divisor) {
        Objects.requireNonNull(dividend);
        Objects.requireNonNull(divisor);
        
        if(BigInteger.ONE.equals(divisor)) {
            return ofWholeNumber(dividend);
        }
        
        if(BigInteger.ZERO.equals(divisor)) {
            throw new IllegalArgumentException(
                    "cannot initalize a rational number with divisor equal to ZERO");
        }
        
        final int signumDividend = dividend.signum();
        final int signumDivisor = divisor.signum();
        final int signum = signumDividend * signumDivisor;
        
        if(signum==0) {
            return ZERO;
        }
        
        final BigInteger absDividend = dividend.abs();
        final BigInteger absDivisor = divisor.abs();

        // cancel down
        final BigInteger gcd = absDividend.gcd(absDivisor);
        return new RationalNumber(signum, absDividend.divide(gcd), absDivisor.divide(gcd));
    }
    
    private RationalNumber(int signum, BigInteger absDividend, BigInteger absDivisor) {
        this.signum = signum;
        this.absDividend = absDividend;
        this.absDivisor = absDivisor;
    }

    public int signum() {
        return signum; 
    }
    
    public BigDecimal bigDecimalValue() {
        synchronized ($lock) {
            if(divisionResult==null) {
                divisionResult = Calculus.toBigDecimal(absDividend)
                        .divide(Calculus.toBigDecimal(absDivisor));
                
                if(signum<0) {
                    divisionResult = divisionResult.negate();
                }
            }    
        }
        return divisionResult;
    }
    
    public RationalNumber add(RationalNumber that) {
        
        // a/b + c/d = (ad + bc) / bd
        BigInteger a = this.absDividend;
        BigInteger b = this.absDivisor;
        BigInteger c = that.absDividend;
        BigInteger d = that.absDivisor;
        
        if(this.signum<0) {
            a = a.negate();
        }
        if(that.signum<0) {
            c = c.negate();
        }
        
        return of(
                a.multiply(d).add(b.multiply(c)), // (ad + bc)
                b.multiply(d) // bd
                );
    }
    
    public Number multiply(RationalNumber that) {
        
        final int productSignum = this.signum * that.signum;
        if(productSignum==0) {
            return ZERO;
        }
        
        // a/b * c/d = ac / bd
        final BigInteger a = this.absDividend;
        final BigInteger b = this.absDivisor;
        final BigInteger c = that.absDividend;
        final BigInteger d = that.absDivisor;
        
        return new RationalNumber(productSignum, 
                a.multiply(c), // bd 
                b.multiply(d) // bd
                );
    }
    
    public RationalNumber negate() {
        return new RationalNumber(-signum, absDividend, absDivisor);
    }
    
    public RationalNumber reciprocal() {
        return new RationalNumber(signum, absDivisor, absDividend);
    }
    
    // -- NUMBER IMPLEMENTATION
    
    @Override
    public int intValue() {
        return (int) longValue();
    }

    @Override
    public long longValue() {
        return bigDecimalValue().longValue();
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        return bigDecimalValue().doubleValue();
    }


}
