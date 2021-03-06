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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.socnetsim.framework.population.SocialNetwork;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.misc.Counter;
import ch.ethz.matsim.socialnetworkgeneration.utils.spatialcollections.SpatialCollectionUtils;
import ch.ethz.matsim.socialnetworkgeneration.utils.spatialcollections.SpatialTree;
import ch.ethz.matsim.socialnetworkgeneration.utils.spatialcollections.VPTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <p>
 * Class that contains the general logic for generating a social network,
 * using a heuristic approach that was shown empirically to perform well and fast on large scale networks.
 * </p>
 * 
 * <p>
 * It implements the appraoch described in chapter 5 of <a href="https://doi.org/10.3929/ethz-b-000165685">this dissertation</a>. Please refer to it for details.
 * </p>
 * 
 * @author thibautd
 */
@Singleton
public class SocialNetworkSampler {
	private static final Logger log = Logger.getLogger( SocialNetworkSampler.class );

	private final Population population;
	private final EgoCharacteristicsDistribution egoDistribution;
	private final CliquesFiller cliquesFiller;
	private final EgoLocator egoLocator;
	private final SpatialCollectionUtils.Metric<double[]> metric;

	private Consumer<Set<Ego>> cliquesListener = (e) -> {};

	/**
	 * 
	 * @param population the population for which a social network will be generated
	 * @param degreeDistribution defines the rules to sample the {@link CliqueStub}s for each agent
	 * @param cliquesFiller defines the rules to match {@link CliqueStub}s to close the network
	 * @param egoLocator translates ego sociodemographics into a point in a cartesian space, used for closest-point matching
	 * @param metric defines the metric used to find the "closest" {@link Ego} to a point in the space of sociodemographics.
	 */
	@Inject
	public SocialNetworkSampler(
			final Population population,
			final EgoCharacteristicsDistribution degreeDistribution,
			final CliquesFiller cliquesFiller,
			final EgoLocator egoLocator,
			final SpatialCollectionUtils.Metric<double[]> metric ) {
		this.population = population;
		this.egoDistribution = degreeDistribution;
		this.cliquesFiller = cliquesFiller;
		this.egoLocator = egoLocator;
		this.metric = metric;
	}

	public void addCliqueListener( final Consumer<Set<Ego>> l ) {
		cliquesListener = cliquesListener.andThen( l );
	}

	public SocialNetwork sampleSocialNetwork() {
		log.info( "Prepare data for social network sampling" );
		final Collection<Tuple<Ego, Collection<CliqueStub>>> egos = new ArrayList<>();
		for ( Person p : population.getPersons().values() ) {
			final Tuple<Ego,Collection<CliqueStub>> ego = egoDistribution.sampleEgo( p );
			egos.add( ego );
		}
		final SpatialTree<double[],CliqueStub> freeStubs = createSpatialTree();
		freeStubs.add(
				egos.stream()
						.map( Tuple::getSecond )
						.flatMap( Collection::stream )
						.collect( Collectors.toList() ) );

		log.info( "Start sampling with "+egos.size()+" egos" );
		log.info( "Start sampling with "+freeStubs.size()+" free stubs" );
		final Counter counter = new Counter( "Sample clique # " );
		while ( freeStubs.size() > 1 ) {
			counter.incCounter();

			final CliqueStub stub = freeStubs.getAny();

			final Set<Ego> clique = cliquesFiller.sampleClique( stub , freeStubs );
			if ( clique == null ) continue;

			link( clique );
			cliquesListener.accept( clique );
		}
		counter.printCounter();

		// to assess what kind of damage the resolution of "conflicts" did
		final int sumPlannedDegrees =
				egos.stream()
						.map( Tuple::getFirst )
						.mapToInt( Ego::getDegree )
						.sum();
		final int sumActualDegrees =
				egos.stream()
						.map( Tuple::getFirst )
						.map( Ego::getAlters )
						.mapToInt( Collection::size )
						.sum();

		log.info( "Average planned degree was "+((double) sumPlannedDegrees / egos.size()) );
		log.info( "Average actual degree is "+((double) sumActualDegrees / egos.size()) );
		log.info( "Number of excedentary ties: "+(sumActualDegrees - sumPlannedDegrees) );

		return new SampledSocialNetwork(
				egos.stream()
						.map( Tuple::getFirst )
						.collect(
							Collectors.toMap(
									Ego::getId,
									e -> e ) ) );
	}

	private static void link( final Set<Ego> members ) {
		for ( Ego ego : members ) {
			for ( Ego alter : members ) {
				if ( alter == ego ) break;
				alter.getAlters().add( ego );
				ego.getAlters().add( alter );
			}
		}
	}

	private SpatialTree<double[],CliqueStub> createSpatialTree() {
		return new VPTree<>(
				metric,
				egoLocator );
	}
}
