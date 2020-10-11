@echo off
set PATH=%PATH%;"%SINFTOOLS%\extras\iped\iped-3.17-snapshot\tools\tsk\x64"
mvn compile exec:java ^
-Dexec.mainClass="go.sptc.sinf.App" ^
-Dexec.workingdirectory="H:\grupo_geccor\B10\processamento" ^
-Dexec.args="-c H:\grupo_geccor\B10\processamento -d D:\temp\deletar -q H:\grupo_geccor\B10\processamento\query.txt -l 50" ^
-Dexec.classpathScope=runtime 