package br.com.leandrosnazareth.rnn_absenteeism_java.ia;

import java.util.Collections;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class AbsenteeismRNNModel {

    private final MultiLayerConfiguration conf;
    private final org.deeplearning4j.nn.multilayer.MultiLayerNetwork model;
    private final NormalizerMinMaxScaler normalizer;

    public AbsenteeismRNNModel() {
        conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.01))
                .list()
                .layer(0, new LSTM.Builder()
                        .nIn(4)  // Entrada: dia da semana, turno, histórico de faltas, proximidade de fim de semana
                        .nOut(10)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(10)
                        .nOut(1)  // Saída: Probabilidade de faltar
                        .activation(Activation.SIGMOID)
                        .build())
                .build();

        model = new org.deeplearning4j.nn.multilayer.MultiLayerNetwork(conf);
        model.init();
        model.setListeners(Collections.singletonList(new ScoreIterationListener(10)));

        normalizer = new NormalizerMinMaxScaler(0, 1);
    }

    public void train(INDArray features, INDArray labels) {
        DataSet dataset = new DataSet(features, labels);
        
        // Normalização correta
        normalizer.fit(dataset);
        normalizer.transform(dataset);

        // Treina o modelo
        model.fit(dataset);
    }

    public double predict(INDArray inputData) {
        normalizer.transform(inputData);
        INDArray prediction = model.output(inputData);
        return prediction.getDouble(0);
    }
}
