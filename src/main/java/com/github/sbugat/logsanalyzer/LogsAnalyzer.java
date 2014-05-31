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

	/** Name of created unknown groups*/
	private static final String NEW_GROUP_NAME = "Unknow group n°"; //$NON-NLS-1$

	/**Configuration*/
	private final LogsAnalyzerConfiguration logsAnalyzerConfiguration;

	/** Number of the next unkonw log group to create*/
	private int unknowGroupNumber = 1;

	public LogsAnalyzer( final String fileName ) throws ConfigurationException{

		//Loading configuration
		logsAnalyzerConfiguration = new LogsAnalyzerConfiguration( fileName );
	}

	private void analyzeFile( final File logFile ) {

		try( final BufferedReader logReader = new BufferedReader( new FileReader( logFile ) ) ) {

			//Loop on the file lines
			String line = logReader.readLine();
			while( null != line ){

				boolean groupFound = false;

				for ( final LogsGroup logsGrp : logsAnalyzerConfiguration.getLogsGroups() ) {

					if( logsGrp.compareAndAddLog( line, logFile.getPath(), logsAnalyzerConfiguration.getDistance() ) ) {

						groupFound = true;
						break;
					}
				}

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

			System.out.println( "Error during " +  logFile.getPath() + " file analysis" + e );
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

	public void postAnalyze() {

		for( final LogsGroup logsGroup : logsAnalyzerConfiguration.getLogsGroups() ) {

			if( logsGroup.isUnkown() ) {

				int closestDistance = Integer.MAX_VALUE;

				for( final LogsGroup configuredlogsGroup : logsAnalyzerConfiguration.getLogsGroups() ) {

					if( ! configuredlogsGroup.isUnkown() ) {

						final int distance = configuredlogsGroup.getDistance( logsGroup );

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

	public void print(){

		//Print all section and logs
		for( final Map.Entry<String,List< LogsGroup >> logsSection : logsAnalyzerConfiguration.getLogsSectionsMap().entrySet() ) {

			System.out.println( "[" + logsSection.getKey() + "]");

			for( final LogsGroup logsGrp : logsSection.getValue() ) {

				final String logs = logsGrp.toString();

				if( ! logs.isEmpty() ) {

					System.out.println( logs );
					System.out.println();

					if( null != logsGrp.getNearestLogsGroup() ) {

						System.out.println( "\tConfigured candidate group (distance: " + logsGrp.getClosestDistance() + ") : " + logsGrp.getNearestLogsGroup().groupName + " ( " + logsGrp.getNearestLogsGroup().getSampleLog() + " ) " );
						System.out.println();
					}
				}
			}

			System.out.println();
		}
	}

	public static void main( final String args[] ) throws ConfigurationException{

		//Load the configuration file, analyze logs files and directory and print logs groups
		final LogsAnalyzer logsAnalyzer = new LogsAnalyzer( "logs-analyzer.ini" ); //$NON-NLS-1$
		logsAnalyzer.analyze();
		logsAnalyzer.postAnalyze();
		logsAnalyzer.print();
	}

}
