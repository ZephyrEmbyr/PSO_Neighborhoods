import csv
import sys
import matplotlib.pyplot as plt
import numpy as np

data = []


with open('output_swarm.csv') as csv_file:
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
	tempMean = [0]*14
	working = data[20*j]
	for k in range(20):
		workingWith = data[20*j+k]
		tempMean = [float((list(x)[0])+float((list(x)[1]))) for x in zip(tempMean,workingWith[5:19])]
	tempMean = [x/20 for x in tempMean]
	dataMeans.append(working[0:5]+tempMean)

dataMedians = []
for j in range(len(data)//20):
	tempMedian = [0]*14
	workingWith = data[j*20]
	for k in range(5,19):
		tempMedian[k-5] = [x[k] for x in data[20*j:20*j+20]]
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

#print data first

for MedianData in presortedDataMedians[24:]:
    print(MedianData)

sys.exit(0)

def createLabelsAndYData(presortedDataMediansSlice, numIndex):
    labels = []
    bestMedians = []
    for MedianData in presortedDataMediansSlice:
        #labels.append(MedianData[0] + '_' + MedianData[1])
        labels.append(MedianData[0])
        bestMedians.append(MedianData[numIndex])
    return labels, bestMedians


def graphRes(labels, bestMedians, title, ylabel, savefileName):
    print("labels: {}".format(labels))
    print("bestMedians: {}".format(bestMedians))

    maxY = max(bestMedians)
    minY = min(bestMedians)

    #One way
    #plt.rcParams.update({"pgf.rcfonts":False})
    y_pos = np.arange(len(labels))
    y_pos1 = y_pos[0:4]
    y_pos2 = y_pos[4:8]
    y_pos3 = y_pos[8:12]
    #y_pos2 = y_pos[5:9]
    #y_pos3 = y_pos[10:14]

    #plt.bar(y_pos1, bestMedians[0:4], width=1.0, align='center', alpha=0.5, color='blue', edgecolor="black")
    plt.rcParams.update({"pgf.rcfonts":False})
    plt.bar(y_pos1, bestMedians[0:4], align='center', alpha=0.5, color='blue', edgecolor="black", label="Swarm Size 16")
    plt.bar(y_pos2, bestMedians[4:8], align='center', alpha=0.5, color='red', edgecolor="black", label="Swarm Size 30")
    plt.bar(y_pos3, bestMedians[8:12], align='center', alpha=0.5, color='green', edgecolor="black", label="Swarm Size 49")

    plt.xticks(y_pos, labels, fontsize=16)
    plt.yticks(fontsize=16)
    plt.xlabel("Topology and Swarm Size", fontsize=16)
    plt.title(title,fontsize=16)
    plt.ylabel(ylabel, fontsize=16)
    plt.ylim(0, 100)
    plt.legend()
    plt.tight_layout()
    plt.savefig(savefileName + ".png")
    plt.savefig(savefileName + ".pgf")
    plt.show()


#First do rok 
title = "Ackley Time vs Topology and Swarm Size"
ylabel = "Median Solution Time" 
savefileName = "ack_solution_time"
labels, bestMedians = createLabelsAndYData(presortedDataMedians[12:24], -2)
graphRes(labels, bestMedians, title, ylabel, savefileName)




