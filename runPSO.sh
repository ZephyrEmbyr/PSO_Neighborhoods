#!/bin/bash

echo "Running PSO Code"

# filetest="maxsat-problems/maxsat-crafted/MAXCUT/SPINGLASS/t3pm3-5555.spn.cnf"

top[0]="gl"
top[1]="ri"
top[2]="vn"
top[3]="ra"

size[0]=16
size[1]=30
size[2]=49

iters=10000

func[0]="rok"
func[1]="ack"
func[2]="ras"

dim=30

for topology in $(seq )
numind=100
selectiontype="ts"
crossovermethod="1c"
crossoverprob=0.7
mutationprob=0.01
numgen=1000
whichalgo="g"

crossover[0]="1c"
crossover[1]="uc"

for method in $(seq 0 1 2)
do
    # echo $probability
    # echo ${mutAmt[probability]}
    printf "Crossover Method: %s\n" "${crossover[method]}" >> "allOutput/gaOutput/runCrossoverMethod.txt"
    ./evolAlg $filetest $numind $selectiontype ${crossover[method]} $crossoverprob $mutationprob $numgen $whichalgo >> "allOutput/gaOutput/runCrossoverMethod.txt"
done
