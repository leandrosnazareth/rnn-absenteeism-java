package br.com.leandrosnazareth.rnn_absenteeism_java.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String siape;
    private String nome;
    private String codigoUnidade;
    private String nomeUnidade;
    private Integer cargaHoraria;

    private LocalDate pontoData;
    private LocalDateTime entrada;
    private LocalDateTime saida;

    private Boolean presente;  // True se registrou entrada e saída, False se faltou
}