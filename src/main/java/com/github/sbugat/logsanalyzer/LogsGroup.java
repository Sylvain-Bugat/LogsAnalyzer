package com.github.sbugat.logsanalyzer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Group of log lines coufigured and found
 *
 * @author Sylvain Bugat
 *
 */
public class LogsGroup {

	/**Alias name for display*/
	private final String groupName;

	/**Example of a log of this group */
	private final String sampleLog;

	/**Log lines found*/
	private final List<String> logs = new ArrayList<>();

	/**Indicate if it's an implicit group*/
	private final boolean unkown;

	/**Closest configured log group for unknown group*/
	private LogsGroup nearestLogsGroup;

	/**Closest distance with a configured log group for unknown group*/
	private int closestDistance;

	/**
	 * Constructor to create a configured empty group
	 *
	 * @param groupNameArg
	 * @param sampleLogArg
	 */
	public LogsGroup( final String groupNameArg, final String sampleLogArg ) {

		groupName = groupNameArg;
		sampleLog = sampleLogArg;
		unkown = false;
	}

	/**
	 * Constructor to create an unkown group with an initial log line
	 *
	 * @param groupNameArg
	 * @param newLogArg
	 * @param fileName
	 */
	public LogsGroup( final String groupNameArg, final String newLogArg, final Path fileName ) {

		groupName = groupNameArg;
		sampleLog = newLogArg;
		unkown = true;

		addLog( newLogArg, fileName );
	}

	/**
	 * Compare a log with the sample log of a group and add if it is near enough
	 *
	 * @param log log to compare
	 * @param fileName filename of the log
	 * @param maxDistance maximum distance
	 * @return if the log is added to the group
	 */
	public boolean compareAndAddLog( final String log, final Path fileName, final int maxDistance ){

		if( -1 != StringUtils.getLevenshteinDistance( log, sampleLog, maxDistance ) ) {

			addLog( log, fileName );
			return true;
		}

		return false;
	}

	/**
	 * Compare another sample log with the current sample log and return the Levenshtein distance
	 *
	 * @param otherGroup other group to compare with
	 * @return Levenshtein distance between these groups
	 */
	public int getDistanceWith( final LogsGroup otherGroup ){

		return StringUtils.getLevenshteinDistance( otherGroup.sampleLog, sampleLog );
	}

	/**
	 * Add a log the logs list with the filename as prefix
	 *
	 * @param log
	 * @param fileName
	 */
	private void addLog( final String log, final Path fileName ){
		logs.add( fileName + "> " + log );
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

	public String getGroupName() {
		return groupName;
	}

	public String getSampleLog() {
		return sampleLog;
	}

	public boolean isUnkown() {
		return unkown;
	}

	public LogsGroup getNearestLogsGroup() {
		return nearestLogsGroup;
	}

	public void setNearestLogsGroup( final LogsGroup nearestLogsGroupArg ) {
		nearestLogsGroup = nearestLogsGroupArg;
	}

	public int getClosestDistance() {
		return closestDistance;
	}

	public void setClosestDistance( final int closestDistanceArg ) {
		closestDistance = closestDistanceArg;
	}
}