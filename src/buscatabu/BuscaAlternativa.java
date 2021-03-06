/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buscatabu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Usuario
 */
public class BuscaAlternativa {

    double b = 400;                 //Peso máximo
    private int maxBT = 400;          //Número máximo de iterações sem melhora

    private String[] itemNames;     //Nomes dos itens
    private double[] pesos;         //Pesos dos itens
    private double[] beneficios;    //Valores dos itens

    private double alpha;      //Somatório de todas as utilidades

    private int[] bestSolucao;
    private double bestAvaliacao;
    private int bestIt = 0;         //Melhor iteração

    private int[] currentSolution;

    private List<Integer> tabu;

    public BuscaAlternativa() {
        itemNames = new String[]{"map", "compass", "water", "sandwich", "glucose", "tin", "banana", "apple",
            "cheese", "beer", "suntan cream", "camera", "T-shirt", "trousers", "umbrella", "waterproof trousers",
            "waterproof overclothes", "note-case", "sunglasses", "towel", "socks", "book"};
        pesos = new double[]{9, 13, 153, 50, 15, 68, 27, 39, 23, 52, 11, 32, 24, 48, 73,
            42, 43, 22, 7, 18, 4, 30};

        beneficios = new double[]{150, 35, 200, 160, 60, 45, 60, 40, 30, 10, 70, 30, 15, 10, 40,
            70, 75, 80, 20, 12, 50, 10};

        tabu = new ArrayList<>();

        currentSolution = new int[itemNames.length];
        bestSolucao = new int[itemNames.length];
        initAlpha();

        initFirstSolution();

        bestAvaliacao = avaliacao(currentSolution);
        bestSolucao = currentSolution.clone();
        bestIt = 0;
    }

    //<editor-fold defaultstate="collapsed" desc="INIT">
    private void initAlpha() {
        for (double d : beneficios) {
            alpha += d;
        }
    }

    private void initFirstSolution() {
        Random r = new Random();

        for (int i = 0; i < currentSolution.length; i++) {
            //currentSolution[i] = r.nextInt(2);
            currentSolution[i] = 1;
        }
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="ALGORITMO">
    public void run() {
        int itAtual = 0;
        int random = 0;

        while ((itAtual - bestIt) < maxBT) {
            itAtual++;
            //random = getRandomPosition();
            if (random == itemNames.length) {
                random = 0;
            }
            System.out.println("\n" + Arrays.toString(currentSolution));
            System.out.println("Random: " + random);
            if (!isTabu(random)) {
                addToTabu(random);
                if (currentSolution[random] == 0) {
                    currentSolution[random] = 1;
                } else {
                    currentSolution[random] = 0;
                }
            } else {
                if (funcaoAspiracao(random, currentSolution.clone())) {
                    if (currentSolution[random] == 0) {
                        currentSolution[random] = 1;
                    } else {
                        currentSolution[random] = 0;
                    }
                }
            }

            int[] bestNeighbor = findBestNeighbor(currentSolution);
            double aval = avaliacao(bestNeighbor);
            System.out.println("Ite: " + itAtual + " avaliacao: " + aval);
            if (aval > bestAvaliacao) {
                bestAvaliacao = aval;
                bestIt = itAtual;
                bestSolucao = bestNeighbor;
            }
            random++;
        }
    }

    private int[] findBestNeighbor(int[] currentSolution) {
        int[][] neighbors = new int[currentSolution.length][currentSolution.length];

        for (int i = 0; i < currentSolution.length; i++) {
            int[] temp = currentSolution.clone();
            if (temp[i] == 0) {
                temp[i] = 1;
            } else {
                temp[i] = 0;
            }

            neighbors[i] = temp.clone();
        }

        int[] bestNeighborFound = new int[bestSolucao.length];
        double bestBeneficio = 0;

        for (int[] sol : neighbors) {
            double val = avaliacao(sol);
            if (val > bestBeneficio) {
                bestNeighborFound = sol.clone();
            }
        }

        return bestNeighborFound;
    }

    /**
     * Avalia se a solução gerada é melhor que a atual mesmo com as penalidades
     * caso ela for maior que o peso
     */
    private double avaliacao(int[] solution) {
        double beneficio = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                beneficio += beneficios[i];
            }
        }

        double peso = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                peso += pesos[i];
            }
        }

        System.out.println("Aval Beneficio: " + beneficio);
        System.out.println("Aval peso: " + peso);
        double max = Math.max(0, peso - b);

        return beneficio - alpha * max;
    }

    private int getRandomPosition() {
        return new Random().nextInt(itemNames.length);
    }

    private void addToTabu(int value) {
        if (tabu.size() > 10) {
            tabu.remove(0);
        }
        tabu.add(value);
    }

    private boolean isTabu(int i) {
        return tabu.contains(i);
    }

    private double calcBeneficio() {
        double value = 0;
        for (int i = 0; i < currentSolution.length; i++) {
            if (currentSolution[i] == 1) {
                value += beneficios[i];
            }
        }
        return value;
    }

    /**
     * Função que avalia se deve ser permitido um movimento tabu após um tempo
     */
    private boolean funcaoAspiracao(int position, int[] solution) {
        if (solution[position] == 0) {
            solution[position] = 1;
        } else {
            solution[position] = 0;
        }

        double beneficio = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                beneficio += beneficios[i];
            }
        }

        double peso = 0;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                peso += pesos[i];
            }
        }

        System.out.println("Aval Beneficio: " + beneficio);
        System.out.println("Aval peso: " + peso);
        double max = Math.max(0, peso - b);

        double aval = beneficio - alpha * max;

        return aval > bestAvaliacao;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PRINT">
    public void printData() {
        System.out.println("\nItens: " + Arrays.toString(bestSolucao));
        System.out.println("Beneficio: " + bestAvaliacao);
        System.out.println("Best iterção: " + bestIt);

        double beneficio = 0;
        double peso = 0;
        for (int i = 0; i < bestSolucao.length; i++) {
            if (bestSolucao[i] == 1) {
                System.out.println("Item: " + itemNames[i] + "\tPeso: " + pesos[i] + "\nBeneficio: " + beneficios[i]);
                beneficio += beneficios[i];
                peso += pesos[i];
            }
        }
        System.out.println("\nBeneficio real: " + beneficio);
        System.out.println("Peso real: " + peso);

    }
    //</editor-fold>

}
