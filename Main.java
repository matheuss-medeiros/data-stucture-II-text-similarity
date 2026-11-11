//Beatriz de Assis Siqueira, RA:10741570 
//Daniel Arais Motta, RA:10419718 
//Matheus Santos Medeiros, RA:10748040 
//Pedro Araujo Botelho, RA:10738317

import java.io.File;
import java.util.ArrayList;

public class Main{
    public static void main(String[] args) {
        if(args.length < 3) {
            System.err.println("Uso: java Main <diretorio> <limiar> <modo> [argumentos_opcionais]");
            return;
        }

        //args[0] = diretorio
        //args[1] = limiar
        //args[2] = modo
        //args[3] = valor de K ou arquivo (modo topK ou busca)
        //args[4] = arquivo (modo busca)
        double limiar;
        int k = 0;
        try{
            limiar = Double.parseDouble(args[1]);
            if(limiar <0.0 || limiar > 1.0){
                System.err.println("O limiar deve estar entre 0.0 e 1.0!");
                return;
            }

        } catch (NumberFormatException e) {
            System.err.println("Insira um limiar válido!");
            return;
        }

        if(args[2].equalsIgnoreCase("lista")){
            if (args.length != 3) {
                System.err.println("Uso no modo lista: java Main <diretorio> <limiar> lista");
                return;
            }
            
        } else if(args[2].equalsIgnoreCase("topK")){
            if(args.length < 4) {
                System.err.println("Faltou o valor de K!");
                return;
            }
            try {
                k = Integer.parseInt(args[3]);
                if(k<1) {
                    System.err.println("Insira um valor de K válido!");
                    return;
                }
            } catch (NumberFormatException e){
                System.err.println("insira um valor de k válido!");
                return;
            }
        } else if(args[2].equalsIgnoreCase("busca")){
            if(args.length<5) {
                System.err.println("Insira todos os argumentos necessarios para esse modo (Uso: java Main <diretorio> <limiar> <modo> <arq 1> <arq 2>");
                return;
            }

        } else {
            System.out.println("Modo inválido! Use: lista, topK ou busca");
            return;
        }

        File diretorio = new File(args[0]);
        if(!(diretorio.exists()) || !(diretorio.isDirectory())){
            System.err.println("Diretorio invalido!");
            return;
        }

        File[] todosArquivos = diretorio.listFiles();
        ArrayList<File> arquivosTxt = new ArrayList<>();

        if(todosArquivos==null) {
            System.err.println("Leitura do arquivo impedida. Verifique se o SO bloqueou algo e se o programa tem permissão de leitura.");
            return;
        }

        for(File arq: todosArquivos){
            String nome = arq.getName();
            if(arq.isFile() && nome.toLowerCase().endsWith(".txt") && !nome.equalsIgnoreCase("resultado.txt")){
                arquivosTxt.add(arq);
            }
        }
        arquivosTxt.sort((a,b) -> a.getName().compareTo(b.getName()));
        if(arquivosTxt.size()<2) {
            System.err.println("Impossível comparar pares com menos de 2 arquivos na pasta");
            return;
        }

        //cada arquivo File que foi pego deve ser transformado em um objeto Documento
        
    }   
}
