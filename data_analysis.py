import csv
import sys


data = []


with open('output.csv') as csv_file:
	reader = csv.reader(csv_file, delimiter=',')
	for row in reader:
		data.append(row[0:19]) # for some reason we have 8 extra blank columns

# print(data)

'''
Topology, swarm size, iterations, function, dimensions, best fit, 
0	    , 1         , 2			, 3		  , 4		  , 5		,
perthousandfit, solution generation, solution time, time elapsed
6-15		  , 16				   , 17			  , 18
'''

# for each of the 36 cases, measure medium of best, measure median per 1000 iterations

dataMeans = []
for j in range(len(data)//20):
	tempMean = [0]*11
	working = data[20*j]
	for k in range(20):
		workingWith = data[20*j+k]
		tempMean = [float((list(x)[0])+float((list(x)[1]))) for x in zip(tempMean,workingWith[5:16])]
	tempMean = [x/20 for x in tempMean]
	dataMeans.append(working[0:5]+tempMean)

dataMedians = []
for j in range(len(data)//20):
	tempMedian = [0]*11
	workingWith = data[j*20]
	for k in range(5,16):
		tempMedian[k-5] = [x[k] for x in data[j:j+20]]
		tempMedian[k-5].sort()
		tempMedian[k-5] = (float(tempMedian[k-5][9])+float(tempMedian[k-5][10]))/2
	dataMedians.append(workingWith[0:5]+tempMedian)



'''
Topology, swarm size, iterations, function, dimensions, best fit, 
0	    , 1         , 2			, 3		  , 4		  , 5		,
perthousandfit, solution generation, solution time, time elapsed
6-15		  , 16				   , 17			  , 18
'''

# # test function data sets:
# rokData = [x for x in data if x[3] == "rok"]
# rasData = [x for x in data if x[3] == "ras"]
# ackData = [x for x in data if x[3] == "ack"]
# rokDataMeans = [x for x in dataMeans if x[3] == "rok"]
# rasDataMeans = [x for x in dataMeans if x[3] == "ras"]
# ackDataMeans = [x for x in dataMeans if x[3] == "ack"]
# rokDataMedians = [x for x in dataMedians if x[3] == "rok"]
# rasDataMedians = [x for x in dataMedians if x[3] == "ras"]
# ackDataMedians = [x for x in dataMedians if x[3] == "ack"]

# # example:
# rok16glData = [x for x in rokData if x[1] == "16" and x[0] == "gl"]
# rok16glDataMeans = [x for x in rokDataMeans if x[1] == "16" and x[0] == "gl"]
# rok16glDataMedians = [x for x in rokDataMedians if x[1] == "16" and x[0] == "gl"]
# # print(rok16glData)


topologies = ["gl", "ri", "vn", "ra"]
swarm = ["16","30","49"]
function = ["rok","ack","ras"]

presortedData = []
presortedDataMeans = []
presortedDataMedians = []

for fn in function:
	for sw in swarm:
		for top in topologies:
			presortedData = presortedData + [x for x in data if x[3] == fn and x[1] == sw and x[0] == top]

for fn in function:
	for sw in swarm:
		for top in topologies:
			presortedDataMeans = presortedDataMeans + [x for x in dataMeans if x[3] == fn and x[1] == sw and x[0] == top]

for fn in function:
	for sw in swarm:
		for top in topologies:
			presortedDataMedians = presortedDataMedians + [x for x in dataMedians if x[3] == fn and x[1] == sw and x[0] == top]

# sort order: test function, swarm size, topology