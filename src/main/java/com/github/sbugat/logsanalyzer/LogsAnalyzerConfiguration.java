package com.github.sbugat.logsanalyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 *
 *
 * @author Sylvain Bugat
 *
 */
public class LogsAnalyzerConfiguration {

	static final String CONFIGURATION_DEFAULT_GROUP = "UNKNOWN"; //$NON-NLS-1$

	private static final String CONFIGURATION_SECTION = "CONFIG"; //$NON-NLS-1$
	private static final String CONFIGURATION_DISTANCE = "distance"; //$NON-NLS-1$
	private static final int DISTANCE_DEFAULT_VALUE = 20;
	private static final String SOURCE_DEFAULT_VALUE = "logs"; //$NON-NLS-1$

	private final List<LogsGroup> logsGroups = new ArrayList<>();

	private final Map<String,List<LogsGroup>> logsSectionsMap = new LinkedHashMap<>();

	private int distance = DISTANCE_DEFAULT_VALUE;

	private List<String> sources = new ArrayList<>();

	public LogsAnalyzerConfiguration( final String configurationIniFileName ) throws ConfigurationException {

		//Load the configuration from the INI format configuration file
		final HierarchicalINIConfiguration hierarchicalINIConfigurationnew = new HierarchicalINIConfiguration( configurationIniFileName );

		//Default configuration group for unknown logs found
		logsSectionsMap.put( CONFIGURATION_DEFAULT_GROUP, new ArrayList<LogsGroup>() );

		for( final String section : hierarchicalINIConfigurationnew.getSections() ) {

			final SubnodeConfiguration subnodeConfiguration = hierarchicalINIConfigurationnew.getSection( section );

			//Configuration section
			if( CONFIGURATION_SECTION.equals( section ) ) {

				distance = subnodeConfiguration.getInt( CONFIGURATION_DISTANCE, DISTANCE_DEFAULT_VALUE );

				final Iterator<String> iterator = subnodeConfiguration.getKeys();
				while( iterator.hasNext() ) {

					final String keyName = iterator.next();

					//Load the distance parameter
					if( CONFIGURATION_DISTANCE.equals( keyName ) ) {
						distance = subnodeConfiguration.getInt( keyName, DISTANCE_DEFAULT_VALUE );
					}
					//Everything else is a source file or a source dir
					else {
						sources.add( subnodeConfiguration.getString( keyName ) );
					}
				}
			}
			//Other sections
			else {

				final List<LogsGroup> groupsList = new ArrayList<>();

				final Iterator<String> iterator = subnodeConfiguration.getKeys();
				while( iterator.hasNext() ) {

					final String logName = iterator.next();
					final LogsGroup logsGroup = new LogsGroup( logName, subnodeConfiguration.getString( logName ) );
					groupsList.add( logsGroup );
					logsGroups.add( logsGroup );
				}

				logsSectionsMap.put( section, groupsList);
			}
		}

		//Add logs file/dir as default if no sources is configured
		if( sources.isEmpty() ) {

			sources.add( SOURCE_DEFAULT_VALUE );
		}
	}

	public void addNewUnknowGroup( final String newGroupName, final String log, final String fileName ) {

		final LogsGroup newLogsGroup = new LogsGroup( newGroupName , log, fileName );
		logsGroups.add( newLogsGroup );
		logsSectionsMap.get( CONFIGURATION_DEFAULT_GROUP ).add( newLogsGroup );
	}

	public List<LogsGroup> getLogsGroups() {
		return logsGroups;
	}

	public Map<String, List<LogsGroup>> getLogsSectionsMap() {
		return logsSectionsMap;
	}

	public int getDistance() {
		return distance;
	}

	public List<String> getSources() {
		return sources;
	}
}
