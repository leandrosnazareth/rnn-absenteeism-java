package br.com.leandrosnazareth.rnn_absenteeism_java.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.leandrosnazareth.rnn_absenteeism_java.model.AbsenteeismPrediction;
import br.com.leandrosnazareth.rnn_absenteeism_java.service.AbsenteeismService;

@RestController
@RequestMapping("/api/absenteeism")
public class AbsenteeismController {

    private final AbsenteeismService service;

    public AbsenteeismController(AbsenteeismService service) {
        this.service = service;
    }

    @GetMapping("/predictToday")
    public List<AbsenteeismPrediction> predictToday() {
        return service.predictTodaysAbsenteeism();
    }
}
