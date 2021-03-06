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
package ch.ethz.matsim.socialnetworkgeneration.toy;

import ch.ethz.matsim.socialnetworkgeneration.framework.CliquesFiller;
import ch.ethz.matsim.socialnetworkgeneration.framework.EgoCharacteristicsDistribution;
import ch.ethz.matsim.socialnetworkgeneration.framework.EgoLocator;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import ch.ethz.matsim.socialnetworkgeneration.utils.spatialcollections.SpatialCollectionUtils;

/**
 * @author thibautd
 */
public class ToySocialNetworkModule extends AbstractModule {
	@Override
	protected void configure() {
		bind( CliquesFiller.class ).to( ToyCliquesFiller.class );
		bind( EgoCharacteristicsDistribution.class ).to( ToyEgoDistribution.class );
		bind( EgoLocator.class ).to( ToyEgoLocator.class );
		bind( new TypeLiteral<SpatialCollectionUtils.Metric<double[]>>(){} ).toInstance( SpatialCollectionUtils::squaredEuclidean );
	}
}
