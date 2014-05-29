package com.github.sbugat.logsanalyzer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Sylvain Bugat
 *
 */
public class LogsGroup {

	public final String groupName;

	private final String sampleLog;

	private final List<String> logs = new ArrayList<>();

	/**
	 * Constructor to create an empty group
	 *
	 * @param groupNameArg
	 * @param sampleLogArg
	 */
	public LogsGroup( final String groupNameArg, final String sampleLogArg ) {

		groupName = groupNameArg;
		sampleLog = sampleLogArg;
	}

	/**
	 * Constructor to create a group with a log line
	 *
	 * @param groupNameArg
	 * @param newLogArg
	 * @param fileName
	 */
	public LogsGroup( final String groupNameArg, final String newLogArg, final String fileName ) {

		groupName = groupNameArg;
		sampleLog = newLogArg;

		logs.add( fileName + "> " + newLogArg );
	}

	/**
	 *
	 *
	 * @param log
	 * @param fileName
	 * @param maxDistance
	 * @return if the log is added to the group
	 */
	public boolean addLog( final String log, final String fileName, final int maxDistance ){

		if( -1 != StringUtils.getLevenshteinDistance( log, sampleLog, maxDistance ) ) {

			logs.add( fileName + "> " + log );
			return true;
		}

		return false;
	}

	/**
	 * Used to print the group of logs
	 */
	@Override
	public String toString() {

		if( logs.isEmpty() ) {
			return StringUtils.EMPTY;
		}

		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append( groupName );
		stringBuilder.append( ": " );
		stringBuilder.append( logs.size() );
		stringBuilder.append( " log(s)" );

		for( final String log : logs ){

			stringBuilder.append( System.lineSeparator() );
			stringBuilder.append( '\t' );
			stringBuilder.append( log );
		}

		return stringBuilder.toString();
	}
}