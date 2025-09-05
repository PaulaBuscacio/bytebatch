package net.buscacio.bytebatch;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    private String nome;
    @Getter
    @Setter
    private String cpf;
    @Getter
    @Setter
    private String agencia;
    @Getter
    @Setter
    private String conta;
    @Getter
    @Setter
    private Double valor;
    @Getter
    @Setter
    private LocalDate mesDeReferencia;
    @Getter
    @Setter
    private LocalDateTime horaImportacao;

}
