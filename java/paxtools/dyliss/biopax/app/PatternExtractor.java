package dyliss.biopax.app;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.biopax.paxtools.controller.EditorMap;
import org.biopax.paxtools.controller.SimpleEditorMap;
import org.biopax.paxtools.converter.LevelUpgrader;
import org.biopax.paxtools.io.BioPAXIOHandler;
import org.biopax.paxtools.io.SimpleIOHandler;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.EntityReference;
import org.biopax.paxtools.model.level3.Named;
import org.biopax.paxtools.model.level3.SimplePhysicalEntity;
import org.biopax.paxtools.pattern.Match;
import org.biopax.paxtools.pattern.Pattern;
import org.biopax.paxtools.pattern.Searcher;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
import dyliss.biopax.pattern.SpaimPatternBox;
import dyliss.biopax.util.BioPaxReaderError;
import dyliss.biopax.util.ClassLoaderHack;

import dyliss.biopax.util.LogUtils;

public class PatternExtractor {

	
	public static final Logger log = LoggerFactory.getLogger(PatternExtractor.class);
	
	
	public static void main(String[] args) throws IOException, URISyntaxException, JDOMException {
	
		
		File inputBiopaxFile = new File(args[0]);
		File outFilePath = new File(args[1]);
		
		Model mInput =readBiopax3File( inputBiopaxFile);
		
	    Pattern p = SpaimPatternBox.controlsExpressionWithTemplateReac();
		 
		Map<BioPAXElement, List<Match>> matches = Searcher.search(mInput, p);
		
	 
		Model targetModel =SimpleEditorMap.L3.getLevel().getDefaultFactory().createModel();

		for(Entry<BioPAXElement, List<Match>> ent  : matches.entrySet()){
			 
			BioPAXElement k = ent.getKey();
			targetModel.add(k);
		}
		saveBioPax3(outFilePath.getAbsolutePath(), targetModel);
	}
	
	private static Model readBiopax3File(File inputBiopaxFile) throws IOException, FileNotFoundException {
		File tmpf = File.createTempFile("tmp_biopax", ".xml");
		LogUtils.info(tmpf.getAbsolutePath());
		formatFlat(inputBiopaxFile,tmpf ) ;
		
		InputStream stream = new FileInputStream(tmpf);
		
		 
		Model model = null;
		try {
			 
			model = read(stream);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new BioPaxReaderError("readBiopax3File failed to build a BioPAX model " +
					" - " + e);
		}
		
		if(model == null) {
			throw new BioPaxReaderError("readBiopax3File did not find any BioPAX data there.");
		}
		return model;
	}
	
	public static Model read(final InputStream in) throws FileNotFoundException {
		Model model = convertFromOwl(in);
		LogUtils.info("--1");
		 
		if(model != null && BioPAXLevel.L2.equals(model.getLevel())) {
			model = new LevelUpgrader().filter(model);
			LogUtils.info("2");
		}
		LogUtils.info("3");
		if(model != null){
			fixDisplayName(model);
			LogUtils.info("4");
		}
		
		return model;
	}
	 
	private static Model convertFromOwl(final InputStream stream) {
		final Model[] model = new Model[1];
		final SimpleIOHandler handler = new SimpleIOHandler();
	
		handler.mergeDuplicates(true); 
		ClassLoaderHack.runWithHack(new Runnable() {
			@Override
			public void run() {
				try {
					model[0] =  handler.convertFromOWL(stream);	
				} catch (Throwable e) {
					log.error("convertFromOwl failed: " + e);
					e.printStackTrace();
				}
			}
		}, com.ctc.wstx.stax.WstxInputFactory.class);
		return model[0];
	} 
	
	public static void fixDisplayName(Model model) {
		log.info("Trying to auto-set displayName for all BioPAX elements");
 
		for (Named e : model.getObjects(Named.class)) {
			if (e.getDisplayName() == null) {
				if (e.getStandardName() != null) {
					e.setDisplayName(e.getStandardName());
				} else if (!e.getName().isEmpty()) {
					String dsp = e.getName().iterator().next();
					for (String name : e.getName()) {
						if (name.length() < dsp.length())
							dsp = name;
					}
					e.setDisplayName(dsp);
				}
			}
		}
		// if required, set PE name to (already fixed) ER's name...
		for(EntityReference er : model.getObjects(EntityReference.class)) {
			for(SimplePhysicalEntity spe : er.getEntityReferenceOf()) {
				if(spe.getDisplayName() == null || spe.getDisplayName().trim().length() == 0) {
					if(er.getDisplayName() != null && er.getDisplayName().trim().length() > 0) {
						spe.setDisplayName(er.getDisplayName());
					}
				}
			}
		}
	}
	
	public static void formatFlat(File inf,File outf ) {
	    try {
	        Source xmlInput = new StreamSource(inf);
	 
	        StreamResult xmlOutput = new StreamResult(outf);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", 0);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "no");
	        
	        transformer.transform(xmlInput, xmlOutput);
	        
	        
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}
	
	
	private static void saveBioPax3(String outFilePath,  Model targetModel) throws FileNotFoundException, IOException {
		
		EditorMap editorMap = SimpleEditorMap.L3;
		 
        
        BioPAXIOHandler handler = new SimpleIOHandler();
        
       
		File outFile= new File(outFilePath);;
		OutputStream outputStream=new FileOutputStream(outFile);
 
       handler.convertToOWL(targetModel, outputStream);
       outputStream.close();
}
}
