# QRCode

### Projeto para controle de entrada e saída de luminárias da fábrica (rastreabilidade de itens com QRCode) utilizando Java, JDBC e MySQL

#### Conteúdo de cada tabela do Banco de Dados 
##### PK - Primary Key; FK - Foreign Key
- bridge_table
  - pedido_id (INT) e luminarias_id(INT) como PK
- codigos
  - ID (INT)
  - qrcode (VARCHAR (50))
  - pedidos_id e luminarias_id como FK
- pedidos
  - ID (INT) como PK
  - numero_pedido (VARCHAR(50))
  - responsavel (VARCHAR(20)
  - qtd (INT)
- luminarias
  - ID (INT) como PK
  - familias (VARCHAR(50)) 
#### Relação de ManyToMany entre pedidos e luminarias e de OneToMany entre a bridge_table e a tabela de códigos QRCode únicos, estabelecendo unicidade de códigos
