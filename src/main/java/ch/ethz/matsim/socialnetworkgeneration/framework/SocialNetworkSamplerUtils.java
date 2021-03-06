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

import com.google.inject.Module;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.socnetsim.framework.population.SocialNetwork;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Injector;

import java.util.Arrays;

/**
 * @author thibautd
 */
public class SocialNetworkSamplerUtils {
	public static SocialNetwork sampleSocialNetwork( final Config config, final Module... modules ) {
		final Module[] allModules = Arrays.copyOf( modules , modules.length + 1 );
		allModules[ allModules.length - 1 ] = new SocialNetworkSamplerModule();
		final com.google.inject.Injector injector = Injector.createInjector( config , allModules );

		return injector.getInstance( SocialNetworkSampler.class ).sampleSocialNetwork();
	}

	public static SocialNetwork sampleSocialNetwork( final Scenario scenario, final Module... modules ) {
		final Module[] allModules = Arrays.copyOf( modules , modules.length + 1 );
		allModules[ allModules.length - 1 ] = new SocialNetworkSamplerModule( scenario );
		final com.google.inject.Injector injector = Injector.createInjector( scenario.getConfig() , allModules );

		return injector.getInstance( SocialNetworkSampler.class ).sampleSocialNetwork();
	}


}

