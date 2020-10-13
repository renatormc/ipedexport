# ipedexport

## Como funciona

1- Abrir o terminal na pasta "processamento" do IPED  
2- Iniciar com o comando "s-ipedexport init"  
3- Alterar o arquivo .ipedexport/config.ini e .ipedexport/query  
4- Executar a exportação com o comando "s-ipedexport run"  

## Desenvolvimento 

O Projeto utiliza maven como build system, é necessário ter ele instalado no sistema em momento de desenvolvimento   

Em momento de desenvolvimento para rodar copie o arquivo run_example.bat e renomeie para run.bat. Modifique os seus dados e o execute

### A lib do sleuthkit tem de ser instalada em separado:

- Executar o comando abaixo mudando o path do arquivo jar da lib:

```bat
mvn deploy:deploy-file -DgroupId="org.sleuthkit" -DartifactId=datamodel -Dversion="4.6.5" -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile="%SINFTOOLS%\extras\iped\iped-3.17-snapshot\lib\sleuthkit-4.6.5-p04.jar"
```
