echo off
TITLE PDF Changer
echo Welcome To PDF Changer 
echo Please make sure you updated Input Src to reflect the paths you need to change.
pause
echo Errors will be reported to err.txt
java -jar pdfChanger-1.0-jar-with-dependencies.jar change 2>err.txt 1>out.txt
echo please check err.txt for any reported errors
pause