# cmpe-car-trip-auto-grader
This repository presents a challenging project and its auto-grader prepared for Data Structures and Algorithms course at Boğaziçi University.
<br><br>
The detailed description of the project can be found in "[cmpe-car-trip.pdf](cmpe-car-trip-project.pdf)".
The main solution can be found in "[src/solutions/mainsolution](src/solutions/mainsolution)" folder. <br>
### Auto-Grader Usage
To grade submissions in Ubuntu, put submissions into [submissions](submissions) folder as a zip file, then run the following command:
```
./run.sh
```
or
```
javac ./src/*.java -d ./bin
java -cp ./bin Grader [-limitedMemory]
```
You can also directly put your submissions folder in [src/solutions](src/solutions) without zipping. <br>
First, zips in submissions folder are extracted to src/solutions folder, 
then the main solution will be run to measure the time limits for the host machine. 
After time limits are determined, each solution in src/solutions will be run.<br><br>
After grading, the log information will be in "src/graded/{submission_name}/log.txt". 
And grading points will be in "grades.txt".<br>
<br>

