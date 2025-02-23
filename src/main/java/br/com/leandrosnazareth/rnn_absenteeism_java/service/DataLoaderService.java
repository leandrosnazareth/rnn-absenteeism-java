package br.com.leandrosnazareth.rnn_absenteeism_java.service;

import br.com.leandrosnazareth.rnn_absenteeism_java.model.AttendanceRecord;
import br.com.leandrosnazareth.rnn_absenteeism_java.repository.AttendanceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DataLoaderService {

    private final AttendanceRepository attendanceRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DataLoaderService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @PostConstruct
public void loadDataFromCsv() {
    try {
        attendanceRepository.deleteAll();
        System.out.println("Carregando dados históricos de absenteísmo...");

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("data/absenteeism_data.csv").getInputStream(), StandardCharsets.UTF_8));

        String line;
        boolean firstLine = true;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            // Ignorar cabeçalho
            if (firstLine) {
                firstLine = false;
                continue;
            }

            // Alterado de split(",") para split(";")
            String[] fields = line.split(";");

            // Remover aspas duplas extras ao redor dos campos
            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].replace("\"", "").trim();
            }

            // Verificar se a linha tem todas as colunas esperadas
            if (fields.length < 8) {
                System.err.println("Linha ignorada (faltando colunas) no CSV na linha " + lineNumber + ": " + line);
                continue;
            }

            try {
                String siape = fields[0].trim();
                String nome = fields[1].trim();
                String codigoUnidade = fields[2].trim();
                String nomeUnidade = fields[3].trim();
                int cargaHoraria = Integer.parseInt(fields[4].trim());
                LocalDate pontoData = LocalDate.parse(fields[5].trim(), DATE_FORMAT);

                // Verificar entrada e saída
                LocalDateTime entrada = fields[6].trim().isEmpty() ? null : LocalDateTime.parse(fields[6].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                LocalDateTime saida = fields[7].trim().isEmpty() ? null : LocalDateTime.parse(fields[7].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                // Determinar presença: Se entrada ou saída existirem, a pessoa estava presente
                boolean presente = (entrada != null || saida != null);

                AttendanceRecord record = new AttendanceRecord(null, siape, nome, codigoUnidade, nomeUnidade, cargaHoraria, pontoData, entrada, saida, presente);
                attendanceRepository.save(record);
            } catch (Exception e) {
                System.err.println("Erro ao processar linha " + lineNumber + ": " + e.getMessage());
            }
        }

        System.out.println("Dados históricos carregados com sucesso!");

    } catch (Exception e) {
        System.err.println("Erro ao carregar dados do CSV: " + e.getMessage());
    }
}
}
