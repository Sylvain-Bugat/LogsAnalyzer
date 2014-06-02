LogsAnalyzer
============

A simple automatic logs analyzer based on Levenstein distance to group logs

***

## Installation

To install LogsAnalyzer you just have to clone the repository:

	git clone https://github.com/Sylvain-Bugat/LogsAnalyzer.git

## Configuration

Edit the sample configuration file `logs-analyzer.ini` in the `\` directory and configure these parameters in the `[CONFIG]` section:  

	distance=XY
	
Maximum distance between 2 logs of a group, example with a configuration of 40: `distance=40`

All other parameters are files and/or directories input with any parameter name
	parameter=\<file/directory\>

## Additionnal Configuration

Multiples sections in the `logs-analyzer.ini` file can also be defined with logs definitions:

	log name=log sample line

***

## Launch LogsAnalyzer

Just execute the main class called `LogsAnalyzer`.


