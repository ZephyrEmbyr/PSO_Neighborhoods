#!/bin/bash

echo "Running PSO Code"

javac PSO.java 

topology[0]="gl"
topology[1]="ri"
topology[2]="vn"
topology[3]="ra"

numParticles[0]=16
numParticles[1]=30
numParticles[2]=49

iters=1

function[0]="rok"
function[1]="ack"
function[2]="ras"

dim=30

for top in $(seq 0 1 3)
do
	for size in $(seq 0 1 2)
	do
		for func in $(seq 0 1 2)
		do
			for run in $(seq 0 1 19)
			do
				java PSO ${topology[top]} ${numParticles[size]} $iters ${function[func]} $dim
			done
		done
	done
done

