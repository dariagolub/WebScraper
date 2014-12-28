WebScraper
===========
Console web scraper utility which:

1) accepts as command line parameters:

web resources URL or path to plain text file containing a list of URLs
data command(s)
word (or list of words with “,” delimiter)
2) supports the following data processing commands:

count number of provided word(s) occurrence on webpage(s). (-w)
count number of characters of each web page (-c)

Data processing results printed to output for each web resources separately.

Command line parameters example for Java implementation:

java –jar scraper.jar http://www.cnn.com Greece,default –v –w –c –e

without using 3d party libraries
