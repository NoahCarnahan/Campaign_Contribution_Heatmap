Campaign Contribution Heatmap
=============================

This tool creates heatmaps (very very slowly) that show where candidates in the 2012 U.S federal elections recieved contributions from. It only maps contributions from individuals; PAC contributions are not included.

## Setup

To run this project, a sql server needs to be running on localhost:8889 (this configuration can of course be changed in the source). I used [MAMP](www.mamp.info/‎) for this purpose. Import the .sql files from the database directory into a database called HeatmapData. Unfortunately, some of the .sql files that should be imported into this database are too large for github. I'm currently working out an alternative solution...

The database directory also contains several python scripts for building .sql files from the raw text files for import into the database. It contains the raw .txt files containing contribution, candidate, and zipcode data (except that the contribution file is too large for github...). One does not need to run the python scripts to get the tool up and running. Simply import the sql files to your database. The database directory also contains the mysql connector needed for database communication.


## Sources
The contribution data was obtained from [OpenSectrets.org](http://www.opensecrets.org) and the Center for Responsive Politics.

The geolocation data for zip codes was obtained from [GeoNames](http://www.geonames.org).
