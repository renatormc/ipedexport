# ipedexport

## Como funciona

1- Abrir o terminal na pasta "processamento" do IPED  
2- Iniciar com o comando "s-ipedexport init"  
3- Alterar o arquivo .ipedexport/config.ini e .ipedexport/query  
4- Executar a exportação com o comando "s-ipedexport export"  

## Desenvolvimento 

### Configuração do VSCode
1- Intale o plugin do Java  
2- Instale o java JDK 11   
3- Adicione o caminho da pasta do jdk nas configurações do VSCode   
```json
{
  "java.home": "C:\\Program Files\\Java\\jdk-11.0.8",
}
```

O Projeto utiliza maven como build system, é necessário ter ele instalado no sistema em momento de desenvolvimento   

Em momento de desenvolvimento para rodar edite a parte do arquivo pom.xml relacionada a execução inserindo a pasta do processamento do iped e depois utilize o comando:
```bat
mvn exec:exec
```

### A lib do sleuthkit tem de ser instalada em separado:

- Executar o comando abaixo mudando o path do arquivo jar da lib:

```bat
mvn deploy:deploy-file -DgroupId="org.sleuthkit" -DartifactId=sleuthkit -Dversion="4.6.5" -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile="%SINFTOOLS%\extras\iped\iped-3.17-snapshot\lib\sleuthkit-4.6.5-p04.jar"
```

## Empacotar e gerar jar
Após estar tudo certo e pronto para finalizar utilize o comando abaixo para gerar um arquivo jar com todas as dependências inclusas.

```bat
mvn clean compile assembly:single
```
O jar será gerado dentro da pasta "target"
