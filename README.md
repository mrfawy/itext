# PDF scanner changer
Java based batch operation. Change PDFs via itext 

## Input
Input can be either a file or a directory
- File Path : a text file that has list of file paths to be processed 
    - sample inputFile.txt is included 
    - format : file://pathToyourfile.pdf  , empty lines or lines starting with # will be skipped
- Dir Path: it'll scan recursively for all directories locating .pdf files ( Warning :it's a slow operation)

## General flow
* Generate a list of files to be processed in either way above 
* Process each file
    * annotation link is inspected , appropriate changes are done
* If file has at least one updated link :
    * anew backup file will be created on the same path , e.g x.pdf => x._NBPBKP.pdf 
    * the file will be overridden by the new updates

* Note: if the script was run multiple times , any already changed links will be skipped 

### Dependencies :
Itext 5.5.6, apache commons io 2.4

