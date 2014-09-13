package com.github.sbugat.logsanalyzer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.sbugat.logsanalyzer.IniConfigurationFileLoader.IniConfigurationSection;

/**
 * Configuration loading and logs container class
 *
 * @author Sylvain Bugat
 *
 */
public class LogsAnalyzerConfiguration {

	/**Default section containing unknown logs group*/
	private static final String CONFIGURATION_DEFAULT_GROUP = "UNKNOWN"; //$NON-NLS-1$

	/**Section containing config parameters and input files/directories*/
	private static final String CONFIGURATION_SECTION = "CONFIG"; //$NON-NLS-1$
	/**Parameter containing the distance*/
	private static final String CONFIGURATION_DISTANCE = "distance"; //$NON-NLS-1$
	/**Default distance used if none is found*/
	private static final int DISTANCE_DEFAULT_VALUE = 20;
	/**Default file/dir source used if none is found */
	private static final String SOURCE_DEFAULT_VALUE = "logs"; //$NON-NLS-1$

	/**List of all logs definition loaded and found*/
	private final List<LogsGroup> logsGroups = new ArrayList<>();
	/**Map of all loaded section in the read order*/
	private final Map<String,List<LogsGroup>> logsSectionsMap = new LinkedHashMap<>();

	/**Configured Levenshtein distance*/
	private int distance = DISTANCE_DEFAULT_VALUE;
	/**List of sources files/directories to read*/
	private final List<String> sources = new ArrayList<>();

	/**
	 * Default configuration loading if no ini configuration file is used
	 * Load all default parameters
	 */
	public LogsAnalyzerConfiguration() {

		sources.add( SOURCE_DEFAULT_VALUE );

		//Default configuration group for unknown logs found
		logsSectionsMap.put( CONFIGURATION_DEFAULT_GROUP, new ArrayList<LogsGroup>() );
	}

	/**
	 * Initialize and load the configuration based on an ini file
	 *
	 * @param configurationIniFileName name of the ini configuration file to load
	 * @throws IOException if parsing error occurs when the ini file is loaded
	 */
	public LogsAnalyzerConfiguration( final String configurationIniFileName ) throws IOException {

		//Load the configuration from the INI format configuration file
		final IniConfigurationFileLoader iniConfigurationFileLoader = new IniConfigurationFileLoader( configurationIniFileName );

		//Default configuration group for unknown logs found
		logsSectionsMap.put( CONFIGURATION_DEFAULT_GROUP, new ArrayList<LogsGroup>() );

		for( final String section : iniConfigurationFileLoader.getSections() ) {

			final IniConfigurationSection iniConfigurationSection = iniConfigurationFileLoader.getSection( section );

			//Configuration section
			if( CONFIGURATION_SECTION.equals( section ) ) {

				for( final Entry<String, String> sectionEntry : iniConfigurationSection.getEntries() ) {

					//Load the distance parameter
					if( CONFIGURATION_DISTANCE.equals( sectionEntry.getKey() ) ) {
						distance = Integer.parseInt( sectionEntry.getValue() );
					}
					//Everything else is a source file or a source dir
					else {
						sources.add( sectionEntry.getValue() );
					}
				}
			}
			//Other sections
			else {

				final List<LogsGroup> groupsList = new ArrayList<>();

				for( final Entry<String, String> sectionEntry : iniConfigurationSection.getEntries() ) {

					final LogsGroup logsGroup = new LogsGroup( sectionEntry.getKey(), sectionEntry.getValue() );
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

	/**
	 * Add a new unknown group to the unknown default section with an initial log
	 *
	 * @param newGroupName name of the new unknown group
	 * @param log first log and sample log of the group
	 * @param fileName filename where the first log was read
	 */
	public void addNewUnknowGroup( final String newGroupName, final String log, final Path fileName ) {

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
