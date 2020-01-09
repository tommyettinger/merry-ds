/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.github.tommyettinger.merry;

import com.google.gwt.typedarrays.client.Float64ArrayNative;
import com.google.gwt.typedarrays.client.Float32ArrayNative;
import com.google.gwt.typedarrays.client.Int32ArrayNative;
import com.google.gwt.typedarrays.client.Int8ArrayNative;
import com.google.gwt.typedarrays.shared.Float64Array;
import com.google.gwt.typedarrays.shared.Float32Array;
import com.google.gwt.typedarrays.shared.Int32Array;
import com.google.gwt.typedarrays.shared.Int8Array;

public final class NumberUtils {
	private static final Int8Array wba = Int8ArrayNative.create(8);
	private static final Int32Array wia = Int32ArrayNative.create(wba.buffer(), 0, 2);
	private static final Float32Array wfa = Float32ArrayNative.create(wba.buffer(), 0, 2);
	private static final Float64Array wda = Float64ArrayNative.create(wba.buffer(), 0, 1);

	public static long doubleToLongBits(final double value) {
		wda.set(0, value);
		return ((long)wia.get(1) << 32) | (wia.get(0) & 0xffffffffL);
	}

	public static double longBitsToDouble(final long bits) {
		wia.set(1, (int)(bits >>> 32));
		wia.set(0, (int)(bits & 0xffffffffL));
		return wda.get(0);
	}
	public static int floatToIntBits(final float value) {
		wfa.set(0, value);
		return wia.get(0);
	}
	public static int floatToRawIntBits(final float value) {
		wfa.set(0, value);
		return wia.get(0);
	}

	public static float intBitsToFloat(final int bits) {
		wia.set(0, bits);
		return wfa.get(0);
	}
}