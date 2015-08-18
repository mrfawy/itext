# itext
Change PDFs via itext , batch operation

Input can be either a file or a directory
File : a text file that has list of file paths to be processed 
sample inputFile.txt is included

Dir: it'll scan recursvivley for all directories locating .pdf files ( Warning :it's a slow operation)

After generating a list of files to be processed in eaither way above :
Each annotation link is inspected , appropriate changes are done
If file has at least one updated link , a new backup file will be created on the same path 
e.g x.pdf => x._NBPBKP.pdf , and the file will be overriden by the new updates

if the script was run mutiple times , any already changed links will be skipped 

Dependencies : Itext 5.5.6, apache commons io 2.4

