package kr.ac.kookmin.cs.motion;

import java.util.ArrayList;

/*
 * libmfcc.c - Code implementation for libMFCC
 * Copyright (c) 2010 Jeremy Sawruk
 *
 * This code is released under the MIT License. 
 * For conditions of distribution and use, see the license in LICENSE
 */

public class MfccAnalyzer {
	/*
	 * Computes the specified (mth) MFCC
	 * 
	 * spectralData - array of doubles containing the results of FFT
	 * computation. This data is already assumed to be purely real samplingRate
	 * - the rate that the original time-series data was sampled at (i.e 44100)
	 * NumFilters - the number of filters to use in the computation. Recommended
	 * value = 48 binSize - the size of the spectralData array, usually a power
	 * of 2 m - The mth MFCC coefficient to compute
	 */
	final static double PI = 3.14159265358979323846264338327;
	final static int mNumFilters = 48;
	final static int mBinSize = 13;
	final static int mSamplingRate = 44100;

	public static double GetCoefficient(ArrayList<Double> spectralData, int m) {

		double result = 0.0;
		double outerSum = 0.0;
		double innerSum = 0.0;
		int k, l;

		// 0 <= m < L
		if (m >= mNumFilters) {
			// This represents an error condition - the specified coefficient is
			// greater than or equal to the number of filters. The behavior in
			// this case is undefined.
			return 0.0;
		}

		result = NormalizationFactor(mNumFilters, m);

		for (l = 1; l <= mNumFilters; l++) {
			// Compute inner sum
			innerSum = 0.0;
			for (k = 0; k < mBinSize - 1; k++) {
				if (!Double.isNaN(spectralData.get(k))) {
					innerSum += Math
							.abs(spectralData.get(k)
									* GetFilterParameter(mSamplingRate,
											mBinSize, k, l));
				} else {

				}
			}

			if (innerSum > 0.0) {
				innerSum = Math.log(innerSum); // The log of 0 is undefined, so
												// don't use it
			}

			innerSum = (innerSum * Math.cos(((m * PI) / mNumFilters)
					* (l - 0.5)));

			outerSum += innerSum;
		}

		result *= outerSum;

		return result;
	}

	/*
	 * Computes the Normalization Factor (Equation 6) Used for internal
	 * computation only - not to be called directly
	 */
	static double NormalizationFactor(int NumFilters, int m) {
		double normalizationFactor = 0.0;

		if (m == 0) {
			normalizationFactor = Math.sqrt(1.0 / NumFilters);
		} else {
			normalizationFactor = Math.sqrt(2.0 / NumFilters);
		}

		return normalizationFactor;
	}

	/*
	 * Compute the filter parameter for the specified frequency and filter bands
	 * (Eq. 2) Used for internal computation only - not the be called directly
	 */
	static double GetFilterParameter(int samplingRate, int binSize,
			int frequencyBand, int filterBand) {
		double filterParameter = 0.0f;

		double boundary = (frequencyBand * samplingRate) / binSize; // k * Fs /
																	// N
		double prevCenterFrequency = GetCenterFrequency(filterBand - 1); // fc(l
																			// -
																			// 1)
																			// etc.
		double thisCenterFrequency = GetCenterFrequency(filterBand);
		double nextCenterFrequency = GetCenterFrequency(filterBand + 1);

		if (boundary >= 0 && boundary < prevCenterFrequency) {
			filterParameter = 0.0f;
		} else if (boundary >= prevCenterFrequency
				&& boundary < thisCenterFrequency) {
			filterParameter = (boundary - prevCenterFrequency)
					/ (thisCenterFrequency - prevCenterFrequency);
			filterParameter *= GetMagnitudeFactor(filterBand);
		} else if (boundary >= thisCenterFrequency
				&& boundary < nextCenterFrequency) {
			filterParameter = (boundary - nextCenterFrequency)
					/ (thisCenterFrequency - nextCenterFrequency);
			filterParameter *= GetMagnitudeFactor(filterBand);
		} else if (boundary >= nextCenterFrequency && boundary < samplingRate) {
			filterParameter = 0.0f;
		}

		return filterParameter;
	}

	/*
	 * Compute the band-dependent magnitude factor for the given filter band
	 * (Eq. 3) Used for internal computation only - not the be called directly
	 */
	static double GetMagnitudeFactor(int filterBand) {
		double magnitudeFactor = 0.0;

		if (filterBand >= 1 && filterBand <= 14) {
			magnitudeFactor = 0.015;
		} else if (filterBand >= 15 && filterBand <= 48) {
			magnitudeFactor = 2.0 / (GetCenterFrequency(filterBand + 1) - GetCenterFrequency(filterBand - 1));
		}

		return magnitudeFactor;
	}

	/*
	 * Compute the center frequency (fc) of the specified filter band (l) (Eq.
	 * 4) This where the mel-frequency scaling occurs. Filters are specified so
	 * that their center frequencies are equally spaced on the mel scale Used
	 * for internal computation only - not the be called directly
	 */
	static double GetCenterFrequency(int filterBand) {
		double centerFrequency = 0.0;
		double exponent;

		if (filterBand == 0) {
			centerFrequency = 0;
		} else if (filterBand >= 1 && filterBand <= 14) {
			centerFrequency = (200.0 * filterBand) / 3.0;
		} else {
			exponent = filterBand - 14.0;
			centerFrequency = Math.pow(1.0711703f, exponent);
			centerFrequency *= 1073.4;
		}

		return centerFrequency;
	}
}
