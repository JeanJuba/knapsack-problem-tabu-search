/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buscatabu;

/**
 *
 * @author m95952
 */
public class BuscaTabu {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Busca b = new Busca();
        //BuscaAlternativa b = new BuscaAlternativa();
        b.run();
        b.printData();
        
    }
    
}
