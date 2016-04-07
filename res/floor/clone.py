#!/usr/bin/python

import urllib.request

url = 'https://raw.githubusercontent.com/luckychess/innomapsserver/master/res/floor/'

for i in range(1, 7):
	dlurl = url + str(i) + ".xml"
	savename = str(i) + ".xml"
	urllib.request.urlretrieve (dlurl, savename)
