/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package ch.ethz.matsim.socialnetworkgeneration.framework;

import ch.ethz.matsim.socialnetworkgeneration.snowball.cliquedistributionsnowball.CliquesDistributionCliquesFiller;
import ch.ethz.matsim.socialnetworkgeneration.utils.spatialcollections.SpatialTree;

import java.util.Set;

/**
 * Provides a method to allocate an {@link Ego} to each free spot in a {@link CliqueStub}.
 * Look at {@link CliquesDistributionCliquesFiller} for an example implementation.
 * 
 * @author thibautd
 */
public interface CliquesFiller {
	/**
	 * Sample a feasible clique, fills the alters lists of the egos, and returns the clique.
	 * 
	 * @param stub the "center" of the clique
	 * @param freeStubs a spatial tree containing stubs that are not yet filled
	 * @return The set of egos pertaining to the clique, including the "center", already modified.
	 */
	Set<Ego> sampleClique( CliqueStub stub , SpatialTree<double[], CliqueStub> freeStubs );
}
