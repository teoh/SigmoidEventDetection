# runTest.sh

# while read line;
for filePath in ./I_ii-iv_WordsIdfSeries/*
do 
	fileName=$(basename "$filePath")
	line="${fileName%.*}"
	echo -e "$line:\n";
	Rscript ./II_i_FitSig/idfSeriesToTuples.r "${line}"
	# ./modelFitting/supportVecIDF_test.py "${line}"
done 

