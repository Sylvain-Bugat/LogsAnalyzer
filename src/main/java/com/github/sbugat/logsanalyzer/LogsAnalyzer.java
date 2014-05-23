package com.github.sbugat.logsanalyzer;

import org.apache.commons.configuration.ConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Sylvain Bugat
 *
 */
public class LogsAnalyzer {

	private static final String NEW_GROUP_NAME = "Unknow group n°"; //$NON-NLS-1$

	private final LogsAnalyzerConfiguration logsAnalyzerConfiguration;

	private int unknowGroupNumber;

	public LogsAnalyzer( final String fileName ) throws ConfigurationException{

		//Loading configuration
		logsAnalyzerConfiguration = new LogsAnalyzerConfiguration( fileName );
	}

	private void analyzeFile( final File logFile ) throws IOException {

		try( final BufferedReader logReader = new BufferedReader( new FileReader( logFile ) ) ) {

			String line = logReader.readLine();
			while( null != line ){

				boolean groupFound = false;

				for ( final LogsGroup logsGrp : logsAnalyzerConfiguration.getLogsGroups() ) {

					if( logsGrp.addLog( line, logsAnalyzerConfiguration.getDistance() ) ) {

						groupFound = true;
						break;
					}
				}

				if( ! groupFound ) {

					//Create a new group of logs
					final LogsGroup newLogsGroup = new LogsGroup( NEW_GROUP_NAME + unknowGroupNumber , line );
					unknowGroupNumber ++;

					logsAnalyzerConfiguration.getLogsGroups().add( newLogsGroup );
					logsAnalyzerConfiguration.getLogsSectionsMap().get( LogsAnalyzerConfiguration.CONFIGURATION_DEFAULT_GROUP ).add( newLogsGroup );
				}

				line = logReader.readLine();
			}
		}
	}

	private void analyzeEntryPoint( final File entryPointFile ) throws IOException {

		if( ! entryPointFile.exists() ) {
			return;
		}

		if( entryPointFile.isFile() ) {

			analyzeFile( entryPointFile );
		}
		else if( entryPointFile.isDirectory() ) {

			for( final File file : entryPointFile.listFiles() ) {

				if( file.isDirectory() ) {

					analyzeEntryPoint( file );
				}
				else if( file.isFile() ) {

					analyzeFile( file );
				}
			}
		}
	}

	public void analyze() throws IOException {

		for( final String sourceFile : logsAnalyzerConfiguration.getSources() ) {

			analyzeEntryPoint( new File( sourceFile) );
		}
	}

	public void print(){

		for( final Map.Entry<String,List< LogsGroup >> logsSection : logsAnalyzerConfiguration.getLogsSectionsMap().entrySet() ) {

			System.out.println( "Section: [" + logsSection.getKey() + "]");

			for( final LogsGroup logsGrp : logsSection.getValue() ) {

				final String logs = logsGrp.toString();

				if( ! logs.isEmpty() ) {

					System.out.println( logs );
					System.out.println();
				}
			}

			System.out.println();
		}
	}

	public static void main( final String args[] ) throws IOException, ConfigurationException{

		final LogsAnalyzer logsAnalyzer = new LogsAnalyzer( "logs-analyzer.ini" ); //$NON-NLS-1$
		logsAnalyzer.analyze();
		logsAnalyzer.print();
	}

}
