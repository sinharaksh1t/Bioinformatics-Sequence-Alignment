# Bioinformatics: Protein Sequence Alignment
Three types of Protein Sequence Alignment methods are implemented in this project:
* [Global Sequence Alignment](https://en.wikipedia.org/wiki/Sequence_alignment#Global_and_local_alignments)
* [Local Sequence Alignment](https://en.wikipedia.org/wiki/Sequence_alignment#Global_and_local_alignments)
* [Dove-Tail Sequence (End Space Free) Alignment](http://www.cs.tau.ac.il/~rshamir/algmb/98/scribe/html/lec02/node16.html)
## Description
The code takes in a protein sequence in Fasta format as query along with a number k, aligns it with the sequences in the database and displays the top k alignments over all query/database sequence pairs along with the matching score.
## Usage
The required files to successfully run this code can be found in this repo. The query and database files are structured in a way that running this code on a regular daily use system (PC, Mac, Laptop) will not stress the system resources too much. Different set of files may be chosen if this code is required to run on an extensive data set. However, it may either consume a lot of time, or a more powerful system may be required.
### Running the code
Run the hw1.java file in the following format passing the required arguments:
```
hw1 a# queryFile databaseFile alphabetFile scoringMatrix k# -3
```
Where a# represents the alignment method. Plug 1 in place of a# for global, 2 for local and 3 for dove-tail alignment method; and k# represents top k alignment sequences that you'd like to be displayed. -3 is the gap penalty which can be changed as per your choice but for the purpose of this project we are keeping it constant at -3.