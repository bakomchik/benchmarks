
/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package ru.mfms.becnhmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class AtomicBooleanBenchmark {
    @State(Scope.Benchmark)
    public static class MyState {
        public byte[] first;
        public byte[] second;
        {
            Random r = new Random();
            first = new byte[100 * 1024 * 1024];
            r.nextBytes(first);
            second = new byte[100 * 1024 * 1024];
            System.arraycopy(first,0,second,0,first.length);

        }

    }


    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(5)
    public void plainArray(MyState state, Blackhole blackhole) {
        blackhole.consume(Arrays.equals(state.first,state.second));
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(5)

    public void fjpWithCopy(MyState state, Blackhole blackhole) throws InterruptedException {
        blackhole.consume(new ArrayEqualityCopyArray().arrayEquals(state.first,state.second));
    }


    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(5)
    public void fjpWithShared(MyState state, Blackhole blackhole) throws InterruptedException {
        blackhole.consume(new ArrayEquality().arrayEquals(state.first,state.second));
    }





}
