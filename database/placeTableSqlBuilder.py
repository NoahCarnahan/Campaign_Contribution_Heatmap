'''
Noah Carnahan
'''

file = open("US/US.txt", "r")

print "DROP TABLE IF EXISTS placeTable;"
print "CREATE TABLE placeTable ("
print "Zip text,"
print "Lat decimal(7,4),"
print "Lon decimal(7,4)"
print ");"

for line in file :
	parts = line.split("\t")
	zip = parts[1]
	lat = parts[9]
	lon = parts[10]
	print 'INSERT INTO placeTable (Zip, Lat, Lon) VALUES("' + zip + '", '+lat+', '+lon+');'


