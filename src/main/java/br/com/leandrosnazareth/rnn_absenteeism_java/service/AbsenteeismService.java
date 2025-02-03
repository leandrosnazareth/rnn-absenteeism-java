package br.com.leandrosnazareth.rnn_absenteeism_java.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import br.com.leandrosnazareth.rnn_absenteeism_java.ia.AbsenteeismRNNModel;
import br.com.leandrosnazareth.rnn_absenteeism_java.model.AbsenteeismPrediction;
import br.com.leandrosnazareth.rnn_absenteeism_java.model.AttendanceRecord;
import br.com.leandrosnazareth.rnn_absenteeism_java.repository.AbsenteeismRepository;
import br.com.leandrosnazareth.rnn_absenteeism_java.repository.AttendanceRepository;

@Service
public class AbsenteeismService {

    private final AbsenteeismRepository predictionRepository;
    private final AttendanceRepository attendanceRepository;
    private final AbsenteeismRNNModel rnnModel;

    public AbsenteeismService(AbsenteeismRepository predictionRepository, AttendanceRepository attendanceRepository) {
        this.predictionRepository = predictionRepository;
        this.attendanceRepository = attendanceRepository;
        this.rnnModel = new AbsenteeismRNNModel();
    }

    public List<AbsenteeismPrediction> predictTodaysAbsenteeism() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

        List<AttendanceRecord> allEmployees = attendanceRepository.findAll();

        return allEmployees.stream().map(record -> {
            double historicalAbsences = getHistoricalAbsenceRate(record.getSiape());
            double shift = record.getEntrada().getHour() < 12 ? 0 : 1;
            double[] features = { dayOfWeek.getValue(), shift, historicalAbsences, isWeekend ? 1 : 0 };

            INDArray input = Nd4j.create(features, new int[] { 1, 4 });
            double probability = rnnModel.predict(input);

            return new AbsenteeismPrediction(null, record.getSiape(), record.getNome(), probability, today);
        }).collect(Collectors.toList());
    }

    private double getHistoricalAbsenceRate(String siape) {
        long totalDays = attendanceRepository.count();
        long absentDays = attendanceRepository.findAll().stream()
                .filter(a -> a.getSiape().equals(siape) && !a.getPresente()).count();
        return (double) absentDays / totalDays;
    }
}
