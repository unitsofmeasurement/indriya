package tech.units.indriya.format;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Objects;

import javax.measure.MeasurementException;
import javax.measure.Quantity;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.QuantityFormat;
import javax.measure.format.UnitFormat;

public class MixedRadixFormat<Q extends Quantity<Q>> implements QuantityFormat {

    // -- FACTORIES

    public static <Q extends Quantity<Q>> MixedRadixFormat<Q> of(MixedRadix<Q> mixedRadix, MixedRadixFormat.MixedRadixFormatOptions mixedRadixFormatOptions) {
        return new MixedRadixFormat<>(mixedRadix, mixedRadixFormatOptions);
    }

    public static <Q extends Quantity<Q>> MixedRadixFormat<Q> of(MixedRadix<Q> mixedRadix) {
        return new MixedRadixFormat<>(mixedRadix, new MixedRadixFormat.MixedRadixFormatOptions());
    }

    // -- IMPLEMENTATION

    @Override
    public Appendable format(Quantity<?> quantity, Appendable destination) throws IOException {
        Quantity<Q> quantity_typed = castOrFail(quantity);
        return mixedRadix.format(quantity_typed, destination, mixedRadixFormatOptions);
    }

    @Override
    public String format(Quantity<?> quantity) {
        Quantity<Q> quantity_typed = castOrFail(quantity);
        return mixedRadix.format(quantity_typed, mixedRadixFormatOptions);
    }

    @Override
    public Quantity<Q> parse(CharSequence csq, ParsePosition pos)
            throws IllegalArgumentException, MeasurementParseException {
        return mixedRadix.parse(csq, pos, mixedRadixFormatOptions);
    }

    @Override
    public Quantity<Q> parse(CharSequence csq) throws MeasurementParseException {
        return mixedRadix.parse(csq, mixedRadixFormatOptions);
    }

    // -- FORMAT OPTIONS

    public static class MixedRadixFormatOptions {

        private DecimalFormat integerFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        {
            integerFormat.setMaximumFractionDigits(0);    
            integerFormat.setDecimalSeparatorAlwaysShown(false);
        }

        private DecimalFormat realFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        private UnitFormat unitFormat = SimpleUnitFormat.getInstance();
        private String numberToUnitDelimiter = AbstractQuantityFormat.DEFAULT_DELIMITER;
        private String radixPartsDelimiter = " ";

        /**
         * @return the {@link DecimalFormat} to be used for formatting the whole-number parts
         */
        public DecimalFormat getIntegerFormat() {
            return integerFormat;
        }

        /**
         * Sets the integerFormat parameter to the given {@code DecimalFormat}.
         * @param integerFormat the {@link DecimalFormat}
         * @throws NullPointerException if {@code integerFormat} is {@code null}
         * @return this {@code MixedRadixFormatOptions}
         */
        public MixedRadixFormatOptions integerFormat(DecimalFormat integerFormat) {
            Objects.requireNonNull(integerFormat);
            this.integerFormat = integerFormat;
            return this;
        }

        /**
         * @return the {@link DecimalFormat} to be used for formatting the real-number part 
         * (which is the last part)
         */
        public DecimalFormat getRealFormat() {
            return realFormat;
        }

        /**
         * Sets the realFormat parameter to the given {@code DecimalFormat}.
         * @param realFormat the {@link DecimalFormat}
         * @throws NullPointerException if {@code realFormat} is {@code null}
         * @return this {@code MixedRadixFormatOptions}    
         */
        public MixedRadixFormatOptions realFormat(DecimalFormat realFormat) {
            Objects.requireNonNull(realFormat);
            this.realFormat = realFormat;
            return this;
        }

        public UnitFormat getUnitFormat() {
            return unitFormat;
        }

        public MixedRadixFormatOptions unitFormat(UnitFormat unitFormat) {
            Objects.requireNonNull(unitFormat);
            this.unitFormat = unitFormat;
            return this;
        }

        public String getNumberToUnitDelimiter() {
            return numberToUnitDelimiter;
        }

        public MixedRadixFormatOptions numberToUnitDelimiter(String numberToUnitDelimiter) {
            Objects.requireNonNull(numberToUnitDelimiter);
            this.numberToUnitDelimiter = numberToUnitDelimiter;
            return this;
        }

        public String getRadixPartsDelimiter() {
            return radixPartsDelimiter;
        }

        public MixedRadixFormatOptions radixPartsDelimiter(String radixPartsDelimiter) {
            Objects.requireNonNull(radixPartsDelimiter);
            this.radixPartsDelimiter = radixPartsDelimiter;
            return this;
        }

    }

    // -- IMPLEMENTATION DETAILS

    private final MixedRadix<Q> mixedRadix;
    private final MixedRadixFormat.MixedRadixFormatOptions mixedRadixFormatOptions;

    private MixedRadixFormat(MixedRadix<Q> mixedRadix, MixedRadixFormat.MixedRadixFormatOptions mixedRadixFormatOptions) {
        this.mixedRadix = mixedRadix;
        this.mixedRadixFormatOptions = mixedRadixFormatOptions;
    }

    @SuppressWarnings("unchecked")
    private Quantity<Q> castOrFail(Quantity<?> quantity) {
        try {
            return (Quantity<Q>) quantity;    
        } catch (ClassCastException e) {
            throw new MeasurementException("Generic type of given 'quantity' "
                    + "does not match the required MixedRadix's generic type!", e);
        }
    }


}
