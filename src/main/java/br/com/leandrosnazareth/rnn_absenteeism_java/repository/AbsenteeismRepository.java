package br.com.leandrosnazareth.rnn_absenteeism_java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.leandrosnazareth.rnn_absenteeism_java.model.AbsenteeismPrediction;

@Repository
public interface AbsenteeismRepository extends JpaRepository<AbsenteeismPrediction, Long> {
}
