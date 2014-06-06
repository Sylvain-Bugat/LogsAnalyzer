package com.github.sbugat.logsanalyzer;

import org.apache.commons.configuration.ConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Main class of the LogsAnalyzer
 *
 * @author Sylvain Bugat
 *
 */
public class LogsAnalyzer {

	/** Name of created unknown groups*/
	private static final String NEW_GROUP_NAME = "Unknow group n°"; //$NON-NLS-1$

	/**Configuration*/
	private final LogsAnalyzerConfiguration logsAnalyzerConfiguration;

	/** Number of the next unkonw log group to create*/
	private int unknowGroupNumber = 1;

	/**
	 * Initialize with the default configuration
	 */
	public LogsAnalyzer() {

		//Loading configuration
		logsAnalyzerConfiguration = new LogsAnalyzerConfiguration();
	}

	/**
	 * Initialize and load the configuration file
	 *
	 * @param iniConfigurationFileName name of the ini type configuration file
	 * @throws ConfigurationException when parsing of the ine file exception accur
	 */
	public LogsAnalyzer( final String iniConfigurationFileName ) throws ConfigurationException{

		//Loading configuration
		logsAnalyzerConfiguration = new LogsAnalyzerConfiguration( iniConfigurationFileName );
	}

	/**
	 * Open and analyze a file, each line is compared with configured and existing log groups
	 *
	 * @param logFile file to analyze
	 */
	private void analyzeFile( final File logFile ) {

		try( final BufferedReader logReader = new BufferedReader( new FileReader( logFile ) ) ) {

			//Loop on the file lines
			String line = logReader.readLine();
			while( null != line ){

				boolean groupFound = false;

				//Find the first configured group that is close enough to the line
				for ( final LogsGroup logsGrp : logsAnalyzerConfiguration.getLogsGroups() ) {

					if( logsGrp.compareAndAddLog( line, logFile.getPath(), logsAnalyzerConfiguration.getDistance() ) ) {

						groupFound = true;
						break;
					}
				}

				//If none was found, create a new one
				if( ! groupFound ) {

					//Create a new group of logs, with this line as the first log
					logsAnalyzerConfiguration.addNewUnknowGroup( NEW_GROUP_NAME + unknowGroupNumber, line, logFile.getPath() );
					unknowGroupNumber ++;
				}

				line = logReader.readLine();
			}
		}
		//Open or read error
		catch( final IOException e ) {

			System.out.println( "Error during " +  logFile.getPath() + " file analysis: " + e );
			e.printStackTrace();
		}
	}

	/**
	 * Analyze a file or a directory
	 *
	 * @param entryPointFile File or directory
	 */
	private void analyzeEntryPoint( final File entryPointFile ) {

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

	/**
	 * Analyze files/directories main loop
	 *
	 * @throws IOException
	 */
	public void analyze() {

		for( final String sourceFile : logsAnalyzerConfiguration.getSources() ) {

			analyzeEntryPoint( new File( sourceFile) );
		}
	}

	/**
	 * Find the nearest logs group to each unkown group
	 * it helps to set the correct distance for a log analysis
	 */
	public void postAnalyze() {

		for( final LogsGroup logsGroup : logsAnalyzerConfiguration.getLogsGroups() ) {

			if( logsGroup.isUnkown() ) {

				int closestDistance = Integer.MAX_VALUE;

				//Find the nearest group, it can be very far
				for( final LogsGroup configuredlogsGroup : logsAnalyzerConfiguration.getLogsGroups() ) {

					if( logsGroup != configuredlogsGroup ) {

						final int distance = configuredlogsGroup.getDistanceWith( logsGroup );

						if( distance < closestDistance ) {

							closestDistance = distance;
							logsGroup.setNearestLogsGroup( configuredlogsGroup );
							logsGroup.setClosestDistance( distance );
						}
					}
				}
			}
		}
	}

	/**
	 * Print the result to the standard output
	 */
	public void print( final PrintStream printStream ){

		//Print all section and logs
		for( final Map.Entry<String,List< LogsGroup >> logsSection : logsAnalyzerConfiguration.getLogsSectionsMap().entrySet() ) {

			printStream.println( "[" + logsSection.getKey() + "]");

			for( final LogsGroup logsGrp : logsSection.getValue() ) {

				final String logs = logsGrp.toString();

				if( ! logs.isEmpty() ) {

					printStream.println( logs );
					printStream.println();

					//Print one line about the nearest logs group if it exists
					if( null != logsGrp.getNearestLogsGroup() ) {

						printStream.println( "\tConfigured candidate group (distance: " + logsGrp.getClosestDistance() + ") : " + logsGrp.getNearestLogsGroup().getGroupName() + " ( " + logsGrp.getNearestLogsGroup().getSampleLog() + " ) " );
						printStream.println();
					}
				}
			}

			printStream.println();
		}

		printStream.flush();
	}

	public static void main( final String args[] ) throws ConfigurationException, FileNotFoundException{

		//Load the configuration file, analyze logs files and directory and print logs groups
		final LogsAnalyzer logsAnalyzer = new LogsAnalyzer( "logs-analyzer.ini" ); //$NON-NLS-1$
		logsAnalyzer.analyze();
		logsAnalyzer.postAnalyze();
		if( 1 == args.length ) {
			logsAnalyzer.print( new PrintStream( args[ 0 ] ) );
		}
		else {
			logsAnalyzer.print( System.out );
		}
	}

}
