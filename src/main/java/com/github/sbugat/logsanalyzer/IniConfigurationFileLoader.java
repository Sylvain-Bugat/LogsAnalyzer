package com.github.sbugat.logsanalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Class to load INI format configuration file
 *
 * @author Sylvain Bugat
 *
 */
public class IniConfigurationFileLoader {

	/**Comment line comment marker*/
	private static final String INI_CONFIGURATION_FILE_COMMENT = "#";

	/**Separator character between the parameter and the value*/
	private static final char PARAMETER_VALUE_SEPARATOR = '=';

	/**Loaded sections Map*/
	private final Map<String, IniConfigurationSection> sections = new LinkedHashMap<>();

	/**
	 * Load a configuration file with an INI format
	 *
	 * @param iniConfigurationFileName name of the configuration file
	 *
	 * @throws IOException in case of file opening/reading error
	 */
	public IniConfigurationFileLoader( final String iniConfigurationFileName ) throws IOException {

		try( final BufferedReader logReader = new BufferedReader( new FileReader( iniConfigurationFileName ) ) ) {

			String currentSection = null;

			//Loop on the file lines
			String line = logReader.readLine();
			while( null != line ){

				line = line.trim();

				//Skip line staring with #
				if( ! line.startsWith( INI_CONFIGURATION_FILE_COMMENT ) ) {

					if( line.startsWith( "[" ) ) {

						currentSection = addNewSection( line );
					}
					else if( ! line.isEmpty() && null != currentSection ) {

						addNewParameter( line, currentSection );
					}
				}

				line = logReader.readLine();
			}
		}
	}

	private String addNewSection( final String line ) {

		final String sectionName = line.replaceFirst( "^\\[", StringUtils.EMPTY ).replaceFirst( "\\]$", StringUtils.EMPTY );
		sections.put( sectionName, new IniConfigurationSection() );
		return sectionName;
	}

	/**
	 * Parse the parameter line, the parameter is before the first equel, the value after (can contain equal and comment character)
	 *
	 * @param line the line to parse
	 * @param currentSection the name of the current parsed section to add the parameter and his value into
	 */
	private void addNewParameter( final String line, final String currentSection ) {

		final IniConfigurationSection iniConfigurationSection = sections.get( currentSection );

		final int equalPosition = line.indexOf( PARAMETER_VALUE_SEPARATOR );

		//If the first equal is not the first character
		if( equalPosition > 0 ) {

			final String parameter = line.substring( 0, equalPosition );
			final String value = line.substring( equalPosition + 1 );

			iniConfigurationSection.parameters.put( parameter, value );
		}
		else {
			System.err.println( "Section:[" + currentSection + "]: invalid parameter line:" + line );
		}
	}

	/**
	 * Return all sections
	 *
	 * @return set containing all loaded sections
	 */
	public Set<String> getSections() {
		return sections.keySet();
	}

	/**
	 * Return the IniConfigurationSection object containing all parameters and theirs values of the sectionn
	 *
	 * @param sectionName name of the section to search
	 * @return IniConfigurationSection object of the section or null if the section don't exist
	 */
	public IniConfigurationSection getSection( final String sectionName ) {
		return sections.get( sectionName );
	}

	/**
	 * Class containing all parameters and theirs values of a loaded section
	 *
	 * @author Sylvain Bugat
	 *
	 */
	public final class IniConfigurationSection {

		/***/
		private Map<String, String> parameters = new LinkedHashMap<>();

		/**
		 * Return the set containing all parameters and theirs values
		 *
		 * @return
		 */
		public Set<Entry<String, String>> getEntries() {

			return parameters.entrySet();
		}
	}
}
