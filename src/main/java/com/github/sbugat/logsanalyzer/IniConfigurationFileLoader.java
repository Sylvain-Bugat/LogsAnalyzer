package com.github.sbugat.logsanalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IniConfigurationFileLoader {

	private final Map<String, IniConfigurationSection> sections = new LinkedHashMap<>();

	public IniConfigurationFileLoader( final String iniConfigurationFileName ) throws FileNotFoundException, IOException {

		try( final BufferedReader logReader = new BufferedReader( new FileReader( iniConfigurationFileName ) ) ) {

			String currentSection = null;

			//Loop on the file lines
			String line = logReader.readLine();
			while( null != line ){

				line = line.trim();

				if( ! line.startsWith( "#" ) ) {

					if( line.startsWith( "[" ) ) {

						currentSection = addNewSection( line );
					}
					else if( ! line.isEmpty() ) {

						addNewParameter( line, currentSection );

					}
				}

				line = logReader.readLine();
			}
		}
	}

	private String addNewSection( final String line ) {

		final String sectionName = line.replaceFirst( "^\\[", "" ).replaceFirst( "\\]$", "" );
		sections.put( sectionName, new IniConfigurationSection() );
		return sectionName;
	}

	private void addNewParameter( final String line, final String currentSection ) {

		final IniConfigurationSection iniConfigurationSection= sections.get( currentSection );

		final int equalPosition = line.indexOf( '=' );

		final String parameter = line.substring( 0, equalPosition );
		final String value = line.substring( equalPosition + 1 );

		iniConfigurationSection.parameters.put( parameter, value );
	}

	public Set<String> getSections() {
		return sections.keySet();
	}

	public IniConfigurationSection getSection( final String sectionName ) {
		return sections.get( sectionName );
	}

	public final class IniConfigurationSection {

		private Map<String, String> parameters = new LinkedHashMap<>();

		public Set<Entry<String, String>> getEntries() {

			return parameters.entrySet();
		}
	}
}
