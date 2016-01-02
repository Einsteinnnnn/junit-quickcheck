/*
 The MIT License

 Copyright (c) 2010-2015 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.pholser.junit.quickcheck.generator.java.time;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.pholser.junit.quickcheck.internal.Reflection.defaultValueOf;

/**
 * Produces values of type {@link Clock}.
 */
public class ClockGenerator extends Generator<Clock> {
    private final ZoneId zoneId = ZoneId.of("UTC");
    private Instant min = Instant.MIN;
    private Instant max = Instant.MAX;

    public ClockGenerator() {
        super(Clock.class);
    }

    /**
     * <p>Tells this generator to produce values within a specified
     * {@linkplain InRange#min() minimum} and/or {@linkplain InRange#max()
     * maximum}, inclusive, with uniform distribution, down to the nanosecond.</p>
     *
     * <p>{@link ClockGenerator} instances are configured using Instant strings.</p>
     *
     * <p>If an endpoint of the range is not specified, the generator will use
     * instants with values of either {@link Instant#MIN} or {@link Instant#MAX}
     * as appropriate.</p>
     *
     * <p>{@link InRange#format()} is ignored.  Instants are always parsed using
     * {@linkplain DateTimeFormatter#ISO_INSTANT}.</p>
     *
     * @param range annotation that gives the range's constraints
     * @throws IllegalArgumentException if the range's values cannot be
     * converted to {@code Instant}
     */
    public void configure(InRange range) {
        try {
            if (!defaultValueOf(InRange.class, "min").equals(range.min()))
                min = Instant.parse(range.min());
            if (!defaultValueOf(InRange.class, "max").equals(range.max()))
                max = Instant.parse(range.max());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(e);
        }

        if (min.compareTo(max) > 0)
            throw new IllegalArgumentException(String.format("bad range, %s > %s", range.min(), range.max()));
    }

    @Override
    public Clock generate(SourceOfRandomness random, GenerationStatus status) {
        long epochSecond = random.nextLong(min.getEpochSecond(), max.getEpochSecond());
        long nanoAdjustment = random.nextLong(min.getNano(), max.getNano());

        return Clock.fixed(Instant.ofEpochSecond(epochSecond, nanoAdjustment), zoneId);
    }
}
