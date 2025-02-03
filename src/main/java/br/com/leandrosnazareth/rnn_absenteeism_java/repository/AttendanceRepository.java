package br.com.leandrosnazareth.rnn_absenteeism_java.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.leandrosnazareth.rnn_absenteeism_java.model.AttendanceRecord;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByPontoDataBetween(LocalDate start, LocalDate end);
}
