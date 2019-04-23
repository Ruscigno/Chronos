CREATE TABLE empresa (
  id SERIAL PRIMARY KEY,
  cnpj varchar(255) NOT NULL,
  data_atualizacao timestamp NOT NULL,
  data_criacao timestamp NOT NULL,
  razao_social varchar(255) NOT NULL
);

CREATE TABLE funcionario (
  id SERIAL PRIMARY KEY,
  cpf varchar(255) NOT NULL,
  data_atualizacao timestamp NOT NULL,
  data_criacao timestamp NOT NULL,
  email varchar(255) NOT NULL,
  nome varchar(255) NOT NULL,
  perfil varchar(255) NOT NULL,
  qtd_horas_almoco float DEFAULT NULL,
  qtd_horas_trabalho_dia float DEFAULT NULL,
  senha varchar(255) NOT NULL,
  valor_hora decimal(19,2) DEFAULT NULL,
  empresa_id bigint DEFAULT NULL
);

CREATE TABLE lancamento (
  id SERIAL PRIMARY KEY,
  data timestamp NOT NULL,
  data_atualizacao timestamp NOT NULL,
  data_criacao timestamp NOT NULL,
  descricao varchar(255) DEFAULT NULL,
  localizacao varchar(255) DEFAULT NULL,
  tipo varchar(255) NOT NULL,
  funcionario_id bigint DEFAULT NULL
);

--
-- Constraints for table funcionario
--
ALTER TABLE funcionario ADD CONSTRAINT FK4cm1kg523jlopyexjbmi6y54j FOREIGN KEY (empresa_id) REFERENCES empresa (id);

--
-- Constraints for table lancamento
--
ALTER TABLE lancamento ADD CONSTRAINT FK46i4k5vl8wah7feutye9kbpi4 FOREIGN KEY (funcionario_id) REFERENCES funcionario (id);
