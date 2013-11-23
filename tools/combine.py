import sys
import re

outputfilename = sys.argv[1]

toOpen = []
for arg in range(2,len(sys.argv)):	
	if (arg == outputfilename):
		continue
	toOpen.append(sys.argv[arg])

prefix = ""
suffix = ""

state = 1

with open(toOpen[0]) as curr:
	for line in curr:
		if (state == 1):
			prefix = prefix + line
			if("library_animation" in line):
				state = 2
		elif (state == 2):
			if("library_animation" in line):
				suffix = suffix + line
				state = 3
		elif (state == 3):
			suffix = suffix + line

data = ""


for fname in toOpen:
	with open(fname) as curr:
		state = 1
		data = data + "<animationset id=\"" + re.sub("\\.\\/","",str(fname)) + "\">\n"
		for line in curr:
			if(state == 1):
				if("library_animation" in line):
					state = 2
			elif(state == 2):
				if("library_animation" in line):
					state = 3
				else:
					data = data + line			
		data = data + "</animationset>\n"

with open(sys.argv[1],"w") as curr:
	curr.write(prefix)
	curr.write(data)
	curr.write(suffix)
