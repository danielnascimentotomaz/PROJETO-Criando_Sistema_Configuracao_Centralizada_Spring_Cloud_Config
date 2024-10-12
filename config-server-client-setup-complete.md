---
modified: 2024-10-12T13:12:59.631Z
title: "Passo a Passo: Criando um Sistema de Configuração Centralizada com
  Spring Cloud Config"
---


# Passo a Passo: Criando um Sistema de Configuração Centralizada com Spring Cloud Config

## Etapa 1: Criando o Config Server

### 1.1. Criar um novo projeto Spring Boot para o Config Server:

Acesse o [Spring Initializr](https://start.spring.io/).

Selecione as seguintes opções:
- **Project**: Maven
- **Language**: Java
- **Spring Boot**: versão estável mais recente
- **Group**: algo como `com.example`
- **Artifact**: `config-server`
- **Packaging**: Jar
- **Java Version**: 17 (ou a versão que você está usando)

#### Dependências:

- Spring Cloud Config Server
- Git (para integrar com repositório Git)

Clique em **Generate** para baixar o projeto.

### 1.2. Configurar o `application.yml` do Config Server:

No arquivo `src/main/resources/application.yml`, configure o servidor com o seguinte conteúdo:

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/danielnascimentotomaz/Config-Server-01.git
          clone-on-start: true
```

Aqui, o servidor está configurado para rodar na porta 8888 e centralizar as configurações em um repositório Git.

### 1.3. Adicionar a anotação `@EnableConfigServer`:

No arquivo `ConfigServerApplication.java`, adicione a anotação `@EnableConfigServer` para ativar o servidor de configuração:

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### 1.4. Subir o Config Server:

Execute o projeto com `mvn spring-boot:run` ou diretamente na sua IDE e certifique-se de que ele está rodando na porta 8888.

## Etapa 2: Criando o Config Client

### 2.1. Criar um novo projeto Spring Boot para o Config Client:

Acesse o [Spring Initializr](https://start.spring.io/).

Selecione as seguintes opções:
- **Project**: Maven
- **Language**: Java
- **Spring Boot**: versão estável mais recente
- **Group**: algo como `com.example`
- **Artifact**: `config-client`
- **Packaging**: Jar
- **Java Version**: 17 (ou a versão que você está usando)

#### Dependências:

- Spring Web
- Spring Boot Actuator
- Spring Cloud Config Client

Clique em **Generate** para baixar o projeto.

### 2.2. Configurar o `application.yml` do Config Client:

No arquivo `src/main/resources/application.yml`, configure o cliente:

```yaml
server:
  port: 8080

spring:
  application:
    name: config-client
  config:
    import: "configserver:http://localhost:8888" # Apontando para o Config Server
```

Aqui, o cliente é configurado para rodar na porta 8080 e se conectar ao Config Server rodando na porta 8888.

### 2.3. Criar o controlador para acessar a propriedade:

No arquivo `src/main/java/com/example/configclient/ClientController.java`, crie um controlador para expor as configurações recebidas do Config Server:

```java
package com.example.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Value("${example.property}")
    private String exampleProperty;

    @GetMapping("/config")
    public String getConfigProperty() {
        return "A propriedade configurada é: " + exampleProperty;
    }
}
```

### 2.4. Configurando os arquivos de configuração no Git:

No seu repositório Git (exemplo: https://github.com/danielnascimentotomaz/Config-Server-01.git), adicione um arquivo `config-client.properties` ou `config-client.yml` com o seguinte conteúdo:

```yaml
example:
  property: "Valor da propriedade vindo do Config Server"
```
### OBS:
## Nome dos Arquivos

Resumindo, o nome do arquivo que vai funcionar pode ser:

- **Específico do aplicativo** (`config-client.properties` ou `config-client.yml`)
- **Genérico** (`application.properties` ou `application.yml`)

### 2.5. Executar o projeto Config Client:

Agora, execute o Config Client e acesse a rota: `http://localhost:8080/client/config`. Isso deve retornar a propriedade `example.property` configurada no Config Server.

## Etapa 3: Testando

1. Certifique-se de que o Config Server está rodando (localhost:8888).
2. Inicie o Config Client.
3. Acesse o endpoint `http://localhost:8080/client/config` e você verá a mensagem:

```bash
A propriedade configurada é: Valor da propriedade vindo do Config Server
```

### Dependências usadas:

#### Config Server:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR12</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### Config Client:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>Hoxton.SR12</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Atualização das Propriedades no Config Client

### 1. Atualização manual via endpoint `/actuator/refresh`

Para forçar o Config Client a atualizar suas configurações, habilite o endpoint `/actuator/refresh`.

#### Passos:

1. Adicione as dependências do Actuator no Config Client:
   Certifique-se de que a seguinte dependência está presente no `pom.xml`:

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   ```

2. Habilite o endpoint `/actuator/refresh` no Config Client:

   No arquivo `application.yml`, adicione as seguintes configurações:

   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: refresh, health
   ```

3. Adicione a anotação `@RefreshScope` no Controller para garantir que as propriedades sejam atualizadas dinamicamente:

   ```java
   import org.springframework.cloud.context.config.annotation.RefreshScope;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;

   @RestController
   @RefreshScope
   @RequestMapping("/client")
   public class ClientController {

       @Value("${example.property}")
       private String exampleProperty;

       @GetMapping("/config")
       public String getConfigProperty() {
           return "A propriedade configurada é: " + exampleProperty;
       }
   }
   ```

4. Faça uma requisição POST ao endpoint `/actuator/refresh`:

   - Usando cURL:

     ```bash
     curl -X POST http://localhost:8080/actuator/refresh
     ```

   - Ou use o Postman para fazer a requisição POST.

### 2. Atualização automática com Spring Cloud Bus (opcional)

Caso queira atualizar as configurações automaticamente, sem a necessidade de chamar o endpoint `/refresh`, você pode configurar o **Spring Cloud Bus** com uma plataforma de mensagens (como RabbitMQ ou Kafka). Este é um passo opcional e mais complexo.

## Conclusão

Seguindo esses passos, você terá um Spring Cloud Config Server e um Config Client configurados, com as propriedades centralizadas em um repositório Git. Você pode alterar as configurações diretamente no Git, e o Config Client pode consumi-las com um simples refresh, sem reiniciar o servidor.
