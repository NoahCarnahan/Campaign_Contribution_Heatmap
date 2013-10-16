'''
Noah Carnahan

Recycled/adapted from Webapp project by
Noah Carnahan and Will Biagi, Software Design, CS 204, 4.20.12
'''

import sys

file = open("CampaignFin12/indivs12.txt", "r")

print "DROP TABLE IF EXISTS contributionTable;"
print "CREATE TABLE contributionTable ("
print "ContribID text,"
print "RecipID text,"
print "Amount integer,"
print "Zip text"
print ");"

for line in file:
    origLine = line
    line = line.replace("|,,|", "|,||,|")
    line = line.replace(",,", ",")
    line = line.replace("|,|", "###")
    line = line.replace("|,", "###")
    line = line.replace(",|", "###")
    individuals = line.split("###")
    ContribID = individuals[2].strip("|").replace('"', '\\"')
    RecipID = individuals[4].strip("|").replace('"', '\\"')
    try:
        Amount = individuals[8].strip("|").replace('"', '\\"').split(",")[1]
    except IndexError:
        Amount =  individuals[8]
    Zip = individuals[12].strip("|").replace('"', '\\"')
    
    query = 'INSERT INTO contributionTable (ContribID, RecipID, Amount, Zip)'
    query += ' VALUES ("%s", "%s", %s , "%s");' % (ContribID, RecipID, Amount, Zip)
    print query
