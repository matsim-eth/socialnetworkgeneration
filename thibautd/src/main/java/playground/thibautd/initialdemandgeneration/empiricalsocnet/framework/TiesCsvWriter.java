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
package playground.thibautd.initialdemandgeneration.empiricalsocnet.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author thibautd
 */
public class TiesCsvWriter extends AbstractCsvWriter {
	public TiesCsvWriter( final String file ) {
		super( file );
	}

	@Override
	protected String titleLine() {
		return "egoId\tegoPlannedDegree\talterId\talterPlannedDegree";
	}

	@Override
	protected Iterable<String> cliqueLines( final Set<Ego> clique ) {
		final List<String> lines = new ArrayList<>();

		for ( Ego ego : clique ) {
			for ( Ego alter : clique ) {
				// only write in one direction?
				if ( alter == ego ) break;
				lines.add( ego.getId() +"\t"+ ego.getDegree() +"\t"+ alter.getId() +"\t"+ alter.getDegree() );
			}
		}

		return lines;
	}
}
