# ipedexport

O Projeto utiliza maven como build system, é necessário ter ele instalado no sistema em momento de desenvolvimento   

Em momento de desenvolvimento para rodar edite os parâmetros do plugin "org.codehaus.mojo:exec-maven-plugin" no arquivo pom.xml e execute run.bat  

### A lib do sleuthkit tem de ser instalada em separado:

- Executar o comando abaixo mudando o path do arquivo jar da lib:

```bat
mvn deploy:deploy-file -DgroupId="org.sleuthkit" -DartifactId=datamodel -Dversion="4.6.5" -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile="%SINFTOOLS%\extras\iped\iped-3.17-snapshot\lib\sleuthkit-4.6.5-p04.jar"
```
