import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ReadImage {

	public static void main(String[] args){	
		
		//File imageFile = new File("arquivos\\DIARIO06_92739cf520.pdf");
		//File imageFile = new File("arquivos\\Proposta TCE_PI (1).pdf");
		File imageFile = new File("arquivos\\DOM2310-28062018-ASSINADO.pdf");
		File dirSaida = new File("saidas\\");
		
		Tesseract instance = new Tesseract();
		instance.setDatapath("E:\\helton\\tessdata\\tessdata-3.04.00\\");
		instance.setLanguage("por");
		//instance.setTessVariable("enable_new_segsearch", "1");
		
		try {
			String result = instance.doOCR(imageFile);
			gravarArquivo(result,imageFile.getName(),"saidas");
			
			
			System.out.println(result);
			System.out.println("Acabou");
		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private static void gravarArquivo (String texto, String nomeArquivo, String caminhoDirSaida) {

		PrintWriter stream = null;

		File arquivo = new File(caminhoDirSaida + "\\" + nomeArquivo + ".txt");

		try {
			stream = new PrintWriter(new BufferedWriter(criarFileWriter(arquivo)));
			stream.println(texto);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}finally {
			fecharArquivo(stream);
		}
	}
	
	private static Writer criarFileWriter(File arquivo) throws IOException {
		String charset = Charset.defaultCharset().toString();
		return new OutputStreamWriter(new FileOutputStream(arquivo), charset);
	}
	
	protected static void fecharArquivo(PrintWriter arquivo) {
		if(arquivo != null) {
			arquivo.flush();
			arquivo.close();
		}
	}

	
	
//	private static ITesseract buildTesseractInstance() {
//        return new Tesseract()
//            .setLanguage("chi_sim+eng")
//            .setTessVariable("user_words_file", Resources.getResource("chi_sim.user-words").getFile())
//            .setTessVariable("user_patterns_file", Resources.getResource("chi_sim.user-patterns").getFile())
//            .setTessVariable("load_freq_dawg", "F")
//            .setTessVariable("load_system_dawg", "F")
//            .setTessVariable("chop_enable", "T")
//            .setTessVariable("use_new_state_cost", "F")
//            .setTessVariable("segment_segcost_rating", "F")
//            .setTessVariable("enable_new_segsearch", "0")
//            .setTessVariable("language_model_ngram_on", "0")
//            .setTessVariable("textord_force_make_prop_words", "F")
//            .setTessVariable("edges_max_children_per_outline", "40")
////            .setTessVariable("language_model_penalty_non_dict_word", "1")
//            .setTessVariable("load_punc_dawg", "F")
//    }

}
