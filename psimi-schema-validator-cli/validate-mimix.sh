#!/bin/bash

#SBATCH --time=06-00:00:00   # walltime
#SBATCH --ntasks=1   # number of tasks
#SBATCH --cpus-per-task=5   # number of CPUs Per Task i.e if your code is multi-threaded
#SBATCH -p production   # partition(s)
#SBATCH --mem=2G   # memory per node
#SBATCH -J "MIMIX_VALIDATOR"   # job name
#SBATCH -o "/nfs/production/hhe/intact/data/psi-mi-validator-logs/validate-mimix-%j.out"   # job output file
#SBATCH --mail-user=intact-dev@ebi.ac.uk   # email address
#SBATCH --mail-type=ALL

FILE=$1

mvn -U clean install -Pvalidate -Dfile="$FILE" -Dscope=mimix -Dlevel=WARN -Dmaven.test.skip
