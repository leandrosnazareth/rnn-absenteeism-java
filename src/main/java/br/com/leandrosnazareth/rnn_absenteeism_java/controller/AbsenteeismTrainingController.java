package br.com.leandrosnazareth.rnn_absenteeism_java.controller;

import br.com.leandrosnazareth.rnn_absenteeism_java.service.AbsenteeismTrainingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/absenteeism")
public class AbsenteeismTrainingController {

    private final AbsenteeismTrainingService trainingService;

    public AbsenteeismTrainingController(AbsenteeismTrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/train")
    public String trainModel() {
        trainingService.trainModel();
        return "Treinamento iniciado! Verifique os logs para mais detalhes.";
    }
}
