package za.redbridge.experiment.NEATM.sensor.parameter.spec;

/**
 * Created by jamie on 2014/11/28.
 */
public final class Limiters {

    private Limiters() {
    }

    public static Limiter wrap() {
        return new Limiter() {
            @Override
            public float limitValue(float oldValue, float newValue, Range range) {
                if ((range.inclusiveMin && newValue < range.min)
                        || (!range.inclusiveMin && newValue <= range.min)) {
                    return range.inclusiveMax ?
                            range.max : Math.nextAfter(range.max, Double.NEGATIVE_INFINITY);
                }

                if ((range.inclusiveMax && newValue > range.max)
                        || (!range.inclusiveMax && newValue >= range.max)) {
                    return range.inclusiveMin ?
                            range.min : Math.nextAfter(range.min, Double.POSITIVE_INFINITY);
                }

                return newValue;
            }
        };
    }

    public static Limiter clamp() {
        return new Limiter() {
            @Override
            public float limitValue(float oldValue, float newValue, Range range) {
                if (range.inclusiveMin) {
                    if (newValue < range.min) {
                        return range.min;
                    }
                } else {
                    if (newValue <= range.min) {
                        return Math.nextAfter(range.min, Double.POSITIVE_INFINITY);
                    }
                }

                if (range.inclusiveMax) {
                    if (newValue > range.max) {
                        return range.max;
                    }
                } else {
                    if (newValue >= range.max) {
                        return Math.nextAfter(range.max, Double.NEGATIVE_INFINITY);
                    }
                }

                return newValue;
            }
        };
    }

}
