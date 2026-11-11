//Beatriz de Assis Siqueira, RA:10741570 
//Daniel Arais Motta, RA:10419718 
//Matheus Santos Medeiros, RA:10748040 
//Pedro Araujo Botelho, RA:10738317

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

        } else if(args[2].equalsIgnoreCase("topK")){
            if(args.length < 4) {
                System.err.println("Faltou o valor de K!");
                return;
            }
            int k;
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
    }   
}
