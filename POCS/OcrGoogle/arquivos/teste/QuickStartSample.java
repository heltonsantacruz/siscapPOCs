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
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

public class QuickStartSample {
	
  public static void main(String... args) throws Exception {
	  
	  QuickStartSample a = new QuickStartSample();
	  a.sample();
	  
	  //PrintStream out = new java.io.PrintStream("siscap_DOCUMENT_TEXT_DETECTION.txt");
	  //a.detectText("E:\\helton\\DOCS\\Mestrado Cesar GIT\\POCS\\OcrGoogle\\arquivos\\TesteSimples.jpg", out);
   
  }
  
  
  public void sample()  throws Exception{
	// Instantiates a client
	    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

	      // The path to the image file to annotate
	      String fileName = "E:\\helton\\DOCS\\Mestrado Cesar GIT\\POCS\\OcrGoogle\\arquivos\\TesteSimples.jpg";

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
  
  
  
  public void detectText(String filePath, PrintStream out) throws Exception, IOException {
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