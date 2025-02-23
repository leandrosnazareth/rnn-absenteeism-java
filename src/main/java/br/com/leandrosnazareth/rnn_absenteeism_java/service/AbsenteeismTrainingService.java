package br.com.leandrosnazareth.rnn_absenteeism_java.service;

import java.time.DayOfWeek;
import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Service;

import br.com.leandrosnazareth.rnn_absenteeism_java.ia.AbsenteeismRNNModel;
import br.com.leandrosnazareth.rnn_absenteeism_java.model.AttendanceRecord;
import br.com.leandrosnazareth.rnn_absenteeism_java.repository.AttendanceRepository;

@Service
public class AbsenteeismTrainingService {

    private final AttendanceRepository attendanceRepository;
    private final AbsenteeismRNNModel rnnModel;

    public AbsenteeismTrainingService(AttendanceRepository attendanceRepository, AbsenteeismRNNModel rnnModel) {
        this.attendanceRepository = attendanceRepository;
        this.rnnModel = rnnModel;
    }

    public void trainModel() {
        List<AttendanceRecord> records = attendanceRepository.findAll();

        if (records.isEmpty()) {
            System.out.println("Nenhum dado histórico de absenteísmo encontrado. Treinamento abortado.");
            return;
        }

        // Converter os dados para arrays bidimensionais
        double[][] featuresArray = records.stream().map(record -> {
            DayOfWeek dayOfWeek = record.getPontoData().getDayOfWeek();
            double shift = record.getEntrada().getHour() < 12 ? 0 : 1;
            double historicalAbsences = getHistoricalAbsenceRate(record.getSiape());
            boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

            return new double[]{dayOfWeek.getValue(), shift, historicalAbsences, isWeekend ? 1 : 0};
        }).toArray(double[][]::new);

        double[] labelsArray = records.stream()
                .map(record -> record.getPresente() ? 0.0 : 1.0)  // 1 = faltou, 0 = compareceu
                .mapToDouble(Double::doubleValue)
                .toArray();

        // Criar matrizes INDArray
        INDArray features = Nd4j.create(featuresArray);
        INDArray labels = Nd4j.create(labelsArray, new long[]{labelsArray.length, 1});

        // Criar conjunto de dados para treinamento
        DataSet dataset = new DataSet(features, labels);

        // Treinar a rede neural
        rnnModel.train(dataset);

        System.out.println("Modelo treinado com sucesso!");
    }

    private double getHistoricalAbsenceRate(String siape) {
        long totalDays = attendanceRepository.count();
        long absentDays = attendanceRepository.findAll().stream()
                .filter(a -> a.getSiape().equals(siape) && !a.getPresente())
                .count();
        return (double) absentDays / totalDays;
    }
}
