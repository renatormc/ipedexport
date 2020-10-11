# ipedexport

O Projeto utiliza maven como build system, é necessário ter ele instalado no sistema em momento de desenvolvimento   

Em momento de desenvolvimento para rodar edite os parâmetros do plugin "org.codehaus.mojo:exec-maven-plugin" no arquivo pom.xml e execute run.bat  

### A lib do sleuthkit tem de ser instalada em separado:

- Criar uma pasta para ser repositório local de nome "local-maven-repo" e execitar p comando abaixo mudando o path do arquivo jar da lib:

mvn deploy:deploy-file -DgroupId="org.sleuthkit" -DartifactId=datamodel -Dversion="4.6.5" -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=D:\projetos\sinf\ipedexport\lib\sleuthkit-4.6.5-p04.jar