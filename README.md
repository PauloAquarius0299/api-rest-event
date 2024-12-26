# ApiRest de Eventos
Devenvolvendo uma api que retorna informações sobre eventos marcados pelo usuario 
## Introdução 
Projeto backend desenvolvido com Java Spring Boot e Maven como gerenciador de dependências. A estrutura do projeto está organizada nas seguintes camadas:


Domain:
Implementei as classes de entidade para o domínio do negócio:

* Event: representa eventos com seus atributos
* Address: gerencia endereços dos eventos
* Coupon: controla cupons de desconto
* DTOs (Data Transfer Objects) para garantir segurança e validação dos dados:

* EventRequestDTO: para receber dados de criação/atualização
* EventResponseDTO: para retornar dados de eventos
* EventDetailsDTO: para detalhes completos do evento

Repositories
Criei repositórios para cada entidade extendendo JpaRepository:

* EventRepository: com consultas JPQL personalizadas para:

Busca de eventos futuros, filtros por título, cidade e data, busca com paginação


* AddressRepository: operações CRUD de endereços
* CouponRepository: gerenciamento de cupons

Services
Implementei a camada de serviços com as regras de negócio:

EventService:

* Criação e atualização de eventos
* Consultas e filtros
* Exclusão lógica


*StorageService:

* Upload de imagens para Amazon S3
* Gerenciamento de arquivos no bucket



Controllers
Desenvolvi endpoints REST para:

* EventController:

* POST /api/event: criação de eventos
* GET /api/event/{id}: consulta de eventos
* PUT /api/event/{id}: atualização
* DELETE /api/event/{id}: remoção


CouponController:

Endpoints para gerenciar cupons de desconto



Config
Configurações do projeto:

* AWSConfig: integração com Amazon S3

* Configuração de credenciais
* Definição de região
* Cliente S3 para upload de imagens



O projeto segue boas práticas de desenvolvimento como:

* Arquitetura em camadas
* Uso de DTOs para segurança
* Injeção de dependências
* Tratamento de exceções
* Documentação dos endpoints
## Ferramentas 
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-FF9900?style=for-the-badge&logo=amazons3&logoColor=white)
