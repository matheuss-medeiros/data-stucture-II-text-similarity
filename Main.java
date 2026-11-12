//Beatriz de Assis Siqueira, RA:10741570 
//Daniel Arais Motta, RA:10419718 
//Matheus Santos Medeiros, RA:10748040 
//Pedro Araujo Botelho, RA:10738317

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Main {

    // 1 = hashDJB2 (hashMultiplicativo), 2 = hashPolinomial
    private static final int TIPO_HASH_PADRAO = 1;
    private static final String NOME_FUNCAO_HASH = (TIPO_HASH_PADRAO == 1) ? "hashMultiplicativo" : "hashPolinomial";
    private static final String METRICA_SIMILARIDADE = "Cosseno";
    private static final String ARQUIVO_SAIDA = "resultado.txt";

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Uso: java Main <diretorio_documentos> <limiar> <modo> [argumentos_opcionais]");
            return;
        }

        File diretorio = new File(args[0]);
        if (!diretorio.exists() || !diretorio.isDirectory()) {
            System.err.println("Erro: Diretorio invalido ou inexistente (" + args[0] + ").");
            return;
        }

        double limiar;
        try {
            limiar = Double.parseDouble(args[1]);
            if (limiar < 0.0 || limiar > 1.0) {
                System.err.println("Erro: O limiar deve estar entre 0.0 e 1.0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.err.println("Erro: Valor de limiar invalido (" + args[1] + ").");
            return;
        }

        String modo = args[2].toLowerCase(Locale.ROOT);

        try {
            Set<String> stopWords = carregarStopWordsComFallback();
            String saidaRelatorio;

            switch (modo) {
                case "busca":
                    if (args.length < 5) {
                        System.err.println("Uso no modo busca: java Main <diretorio> <limiar> busca <arq1> <arq2>");
                        return;
                    }
                    saidaRelatorio = executarModoBusca(diretorio, args[3], args[4], stopWords);
                    break;

                case "lista":
                    saidaRelatorio = executarModoLote(diretorio, limiar, -1, stopWords);
                    break;

                case "topk":
                    if (args.length < 4) {
                        System.err.println("Uso no modo topK: java Main <diretorio> <limiar> topK <K>");
                        return;
                    }
                    int k = Integer.parseInt(args[3]);
                    if (k < 1) {
                        System.err.println("Erro: O valor de K deve ser um inteiro maior que zero.");
                        return;
                    }
                    saidaRelatorio = executarModoLote(diretorio, limiar, k, stopWords);
                    break;

                default:
                    System.err.println("Modo invalido! Use: lista, topK ou busca.");
                    return;
            }

            if (saidaRelatorio != null) {
                exportarResultado(saidaRelatorio);
            }

        } catch (NumberFormatException e) {
            System.err.println("Erro: O parametro K no modo topK deve ser um numero inteiro valido.");
        } catch (IOException e) {
            System.err.println("Erro de E/S durante o processamento: " + e.getMessage());
        }
    }

    /**
     * Executa o modo 'busca', instanciando apenas os dois arquivos solicitados.
     */
    private static String executarModoBusca(File diretorio, String nomeArq1, String nomeArq2, Set<String> stopWords)
            throws IOException {
        File arq1 = new File(diretorio, nomeArq1);
        File arq2 = new File(diretorio, nomeArq2);

        if (!arq1.exists() || !arq1.isFile() || !arq2.exists() || !arq2.isFile()) {
            System.err.println("Erro: Um ou ambos os arquivos informados nao existem no diretorio.");
            return null;
        }

        Documento doc1 = new Documento(arq1.getPath(), stopWords, TIPO_HASH_PADRAO);
        Documento doc2 = new Documento(arq2.getPath(), stopWords, TIPO_HASH_PADRAO);

        double similaridade = ComparadorDeDocumentos.calcularCosseno(
                doc1.getTabelaFrequencias(),
                doc2.getTabelaFrequencias()
        );

        StringBuilder sb = new StringBuilder();
        sb.append("=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===\n");
        sb.append(String.format("Comparando: %s <-> %s\n", doc1.getNomeArquivo(), doc2.getNomeArquivo()));
        sb.append(String.format(Locale.US, "Similaridade calculada: %.2f\n", similaridade));
        sb.append("Métrica utilizada: ").append(METRICA_SIMILARIDADE).append("\n");

        return sb.toString();
    }

    /**
     * Executa os modos 'lista' (k == -1) e 'topK' (k > 0).
     * Pré-processa cada Documento uma única vez em O(N) antes de comparar os pares em O(N^2).
     */
    private static String executarModoLote(File diretorio, double limiar, int k, Set<String> stopWords)
            throws IOException {
        List<File> arquivosTxt = listarArquivosValidos(diretorio);
        if (arquivosTxt.size() < 2) {
            System.err.println("Erro: Sao necessarios pelo menos 2 arquivos .txt no diretorio para comparacao.");
            return null;
        }

        // 1. Instancia cada Documento uma única vez (evita reprocessar I/O e Hash no loop duplo)
        List<Documento> documentos = new ArrayList<>(arquivosTxt.size());
        for (File arq : arquivosTxt) {
            documentos.add(new Documento(arq.getPath(), stopWords, TIPO_HASH_PADRAO));
        }

        // 2. Compara todos os pares únicos e insere na Árvore AVL
        AVLTree avl = new AVLTree();
        int totalParesComparados = 0;

        for (int i = 0; i < documentos.size(); i++) {
            Documento docA = documentos.get(i);
            for (int j = i + 1; j < documentos.size(); j++) {
                Documento docB = documentos.get(j);

                double sim = ComparadorDeDocumentos.calcularCosseno(
                        docA.getTabelaFrequencias(),
                        docB.getTabelaFrequencias()
                );

                avl.inserir(new Resultado(docA.getNomeArquivo(), docB.getNomeArquivo(), sim));
                totalParesComparados++;
            }
        }

        // 3. Monta o relatório conforme o layout estrito da Página 6 da especificação
        StringBuilder sb = new StringBuilder();
        sb.append("=== VERIFICADOR DE SIMILARIDADE DE TEXTOS ===\n");
        sb.append("Total de documentos processados: ").append(documentos.size()).append("\n");
        sb.append("Total de pares comparados: ").append(totalParesComparados).append("\n");
        sb.append("Função hash utilizada: ").append(NOME_FUNCAO_HASH).append("\n");
        sb.append("Métrica de similaridade: ").append(METRICA_SIMILARIDADE).append("\n\n");

        List<Resultado> selecionados;
        if (k > 0) {
            sb.append(String.format("Top %d pares mais semelhantes:\n", k));
            selecionados = avl.obterTopK(k);
        } else {
            sb.append(String.format(Locale.US, "Pares com similaridade >= %.2f:\n", limiar));
            selecionados = avl.obterAcimaDe(limiar);
        }

        sb.append("---------------------------------\n");
        if (selecionados.isEmpty()) {
            sb.append("Nenhum par encontrado para os criterios informados.\n");
        } else {
            for (Resultado r : selecionados) {
                sb.append(formatarPar(r)).append("\n");
            }
        }

        sb.append("\nPares com menor similaridade:\n");
        sb.append("---------------------------------\n");
        List<Resultado> menores = obterMenoresDaAVL(avl.getRaiz());
        for (Resultado r : menores) {
            sb.append(formatarPar(r)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Filtra apenas arquivos .txt válidos da coleção, ignorando arquivos de log ou de stop words.
     */
    private static List<File> listarArquivosValidos(File diretorio) throws IOException {
        File[] todosArquivos = diretorio.listFiles();
        if (todosArquivos == null) {
            throw new IOException("Nao foi possivel listar os arquivos do diretorio: " + diretorio.getPath());
        }

        List<File> arquivosTxt = new ArrayList<>();
        for (File arq : todosArquivos) {
            String nome = arq.getName();
            if (arq.isFile()
                    && nome.toLowerCase(Locale.ROOT).endsWith(".txt")
                    && !nome.equalsIgnoreCase(ARQUIVO_SAIDA)
                    && !nome.equalsIgnoreCase("stop_word.txt")
                    && !nome.equalsIgnoreCase("stopwords.txt")) {
                arquivosTxt.add(arq);
            }
        }
        arquivosTxt.sort(Comparator.comparing(File::getName));
        return arquivosTxt;
    }

    /**
     * Busca o nó mais à esquerda da Árvore AVL (menor chave de similaridade).
     */
    private static List<Resultado> obterMenoresDaAVL(NoAVL raiz) {
        if (raiz == null) {
            return new ArrayList<>();
        }
        NoAVL atual = raiz;
        while (atual.getEsquerda() != null) {
            atual = atual.getEsquerda();
        }
        return atual.getResultados();
    }

    /**
     * Formata o objeto Resultado no padrão exato da especificação: doc1.txt <-> doc2.txt = 0.82
     */
    private static String formatarPar(Resultado r) {
        return String.format(Locale.US, "%s <-> %s = %.2f",
                r.getDocumento1(), r.getDocumento2(), r.getSimilaridade());
    }

    /**
     * Tenta carregar stop_word.txt do disco; se não existir no diretório de teste do professor,
     * utiliza a lista padrão em memória para evitar falha de execução.
     */
    private static Set<String> carregarStopWordsComFallback() {
        File arquivoStop = new File("stop_word.txt");
        if (arquivoStop.exists() && arquivoStop.isFile()) {
            try {
                return Documento.carregarStopWords(arquivoStop.getPath());
            } catch (IOException ignored) {
                // Cai para o fallback abaixo caso ocorra erro de leitura
            }
        }
        return new HashSet<>(Arrays.asList(
                "a", "o", "e", "de", "do", "da", "em", "para", "com", "um", "uma",
                "os", "as", "que", "é", "são", "na", "no", "por", "dos", "das",
                "se", "como", "mais", "mas", "ao"
        ));
    }

    /**
     * Imprime a saída no terminal e grava simultaneamente em resultado.txt.
     */
    private static void exportarResultado(String conteudo) throws IOException {
        System.out.print(conteudo);
        Files.writeString(Path.of(ARQUIVO_SAIDA), conteudo, StandardCharsets.UTF_8);
    }
}