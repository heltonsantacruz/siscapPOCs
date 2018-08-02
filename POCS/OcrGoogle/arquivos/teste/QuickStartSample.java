package teste;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Block;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Page;
import com.google.cloud.vision.v1.Paragraph;
import com.google.cloud.vision.v1.Symbol;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.cloud.vision.v1.Word;
import com.google.cloud.vision.v1p2beta1.Block.BlockType;
import com.google.protobuf.ByteString;

public class QuickStartSample {
	
  public static void main(String... args) throws Exception {
	  System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
	  
	// The path to the image file to annotate
      String fileName = "D:\\Cesar\\GIT\\siscapPOCs\\POCS\\OcrGoogle\\arquivos\\LadoEsquerdoComTabelaDOM.jpg";

	 
	  QuickStartSample a = new QuickStartSample();
	  a.detectDocumentText(fileName);
	  
	  //a.detectText(fileName);
	  
	  //a.detectText("D:\\\\Cesar\\\\GIT\\\\siscapPOCs\\\\POCS\\\\OcrGoogle\\\\arquivos\\\\TesteSimples.jpg", out);
   
  }
  
  
  public void sample(PrintStream out, String fileName)  throws Exception{
	// Instantiates a client
	    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

	      

	      // Reads the image file into memory
	      Path path = Paths.get(fileName);
	      byte[] data = Files.readAllBytes(path);
	      ByteString imgBytes = ByteString.copyFrom(data);

	      // Builds the image annotation request
	      List<AnnotateImageRequest> requests = new ArrayList<>();
	      Image img = Image.newBuilder().setContent(imgBytes).build();
	      Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
	      AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
	          .addFeatures(feat)
	          .setImage(img)
	          .build();
	      requests.add(request);

	      // Performs label detection on the image file
	      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
	      List<AnnotateImageResponse> responses = response.getResponsesList();

	      for (AnnotateImageResponse res : responses) {
	        if (res.hasError()) {
	          System.out.printf("Error: %s\n", res.getError().getMessage());
	          return;
	        }

	        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
	          annotation.getAllFields().forEach((k, v) ->
	              System.out.printf("%s : %s\n", k, v.toString()));
	        }
	      }
	    }
  }
  
  
  public void detectDocumentText(String filePath) throws Exception,
  IOException {
	  
	     PrintStream out = new java.io.PrintStream("siscap_DOCUMENT_TEXT_DETECTION.txt");
	  
		List<AnnotateImageRequest> requests = new ArrayList<>();
		
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
		
		Image img = Image.newBuilder().setContent(imgBytes).build();
		Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
		AnnotateImageRequest request =
		    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);
		
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
		  BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		  List<AnnotateImageResponse> responses = response.getResponsesList();
		  client.close();
		
		  for (AnnotateImageResponse res : responses) {
		    if (res.hasError()) {
		      out.printf("Error: %s\n", res.getError().getMessage());
		      return;
		    }
		
		    // For full list of available annotations, see http://g.co/cloud/vision/docs
		    TextAnnotation annotation = res.getFullTextAnnotation();
		    for (Page page: annotation.getPagesList()) {
		      String pageText = "";
		      for (Block block : page.getBlocksList()) {
		        String blockText = "";
		        System.out.println(block.getBlockTypeValue());
		        System.out.println(BlockType.TABLE_VALUE);
		        if(block.getBlockTypeValue() != BlockType.TABLE_VALUE) {			        
		        	for (Paragraph para : block.getParagraphsList()) {
			          String paraText = "";
			          for (Word word: para.getWordsList()) {
			            String wordText = "";
			            for (Symbol symbol: word.getSymbolsList()) {
			              wordText = wordText + symbol.getText();
			              out.format("Symbol text: %s (confidence: %f)\n", symbol.getText(),
			                  symbol.getConfidence());
			            }
			            out.format("Word text: %s (confidence: %f)\n\n", wordText, word.getConfidence());
			            paraText = String.format("%s %s", paraText, wordText);
			          }
			          // Output Example using Paragraph:
			          out.println("\nParagraph: \n" + paraText);
			          out.format("Paragraph Confidence: %f\n", para.getConfidence());
			          blockText = blockText + paraText;
			        }
		        }    
		        pageText = pageText + blockText;
		      }
		    }
		    out.println("\nComplete annotation:");
		    out.println(annotation.getText());
		  }
		}
		}
  
  
  public void detectText(String filePath) throws Exception, IOException {
	  
	  PrintStream out = new java.io.PrintStream("siscap_TEXT_DETECTION.txt");
	  List<AnnotateImageRequest> requests = new ArrayList<>();

	  ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

	  Image img = Image.newBuilder().setContent(imgBytes).build();
	  Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
	  AnnotateImageRequest request =
	      AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
	  requests.add(request);

	  try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
	    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
	    List<AnnotateImageResponse> responses = response.getResponsesList();

	    for (AnnotateImageResponse res : responses) {
	      if (res.hasError()) {
	        out.printf("Error: %s\n", res.getError().getMessage());
	        return;
	      }

	      // For full list of available annotations, see http://g.co/cloud/vision/docs
	      for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
	        out.printf("Text: %s\n", annotation.getDescription());
	        out.printf("Position : %s\n", annotation.getBoundingPoly());
	      }
	    }
	  }
	}
  
  
  
}