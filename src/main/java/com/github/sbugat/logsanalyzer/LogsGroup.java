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

	public LogsGroup( final String groupNameArg, final String sampleLogArg ) {

		groupName = groupNameArg;
		sampleLog = sampleLogArg;
	}

	public boolean addLog( final String log, final int maxDistance ){

		if( -1 != StringUtils.getLevenshteinDistance( log, sampleLog, maxDistance ) ) {
			logs.add( log );
			return true;
		}

		return false;
	}

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