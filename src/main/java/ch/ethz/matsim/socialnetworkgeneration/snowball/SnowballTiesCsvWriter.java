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
package ch.ethz.matsim.socialnetworkgeneration.snowball;

import ch.ethz.matsim.socialnetworkgeneration.framework.AbstractCsvWriter;
import ch.ethz.matsim.socialnetworkgeneration.framework.AutocloserModule;
import ch.ethz.matsim.socialnetworkgeneration.framework.Ego;
import ch.ethz.matsim.socialnetworkgeneration.framework.SocialNetworkSampler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.population.PersonUtils;
import org.matsim.core.utils.geometry.CoordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Writer that writes egos' attributes
 *
 * @author thibautd
 */
@Singleton
public class SnowballTiesCsvWriter extends AbstractCsvWriter {
	@Inject
	public SnowballTiesCsvWriter(
			final ControlerConfigGroup config,
			final SocialNetworkSampler sampler,
			final AutocloserModule.Closer closer ) {
		super( config.getOutputDirectory() +"/output_ties_attr.csv" , sampler , closer );
	}

	@Override
	protected String titleLine() {
		return "egoId\tegoPlannedDegree\tegoAge\tegoSex" +
				"\tegoX\tegoY" +
				"\talterId\talterPlannedDegree\talterAge\talterSex" +
				"\talterX\talterY" +
				"\tdistance_m";
	}

	@Override
	protected Iterable<String> cliqueLines( final Set<Ego> clique ) {
		final List<String> lines = new ArrayList<>();

		for ( Ego ego : clique ) {
			final Coord egoCoord = SnowballLocator.calcCoord( ego );
			final String egoPart =
					ego.getId() +"\t"+ ego.getDegree() + "\t" +
							PersonUtils.getAge( ego.getPerson() ) + "\t" +
							SocialPositions.getSex( ego ) + "\t" +
							egoCoord.getX()+"\t"+egoCoord.getY();
			for ( Ego alter : clique ) {
				// only write in one direction?
				if ( alter == ego ) continue;

				final Coord alterCoord = SnowballLocator.calcCoord( alter );
				lines.add( egoPart +"\t"+
						alter.getId() +"\t"+ alter.getDegree() + "\t" +
						PersonUtils.getAge( alter.getPerson() ) + "\t" +
						SocialPositions.getSex( alter ) + "\t" +
						alterCoord.getX()+"\t"+alterCoord.getY() +"\t"+
						CoordUtils.calcEuclideanDistance( egoCoord , alterCoord ) );
			}
		}

		return lines;
	}
}
