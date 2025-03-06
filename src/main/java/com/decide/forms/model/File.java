package com.decide.forms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.decide.forms.commands.FormCommand;
import com.decide.forms.commands.FormCommandFeature;
import com.decide.forms.util.LoggerHelper;

public class File {
	/*--------------------------------------------------------------*
	 * Constants
	 *--------------------------------------------------------------*/    
	private static final String FILE_OBJ_ID = "__file__";
	private static final String FILE_ATTR_FILENAME = "filename";
	private static final String FILE_ATTR_DOCID = "documentId";
	private static final String FILE_ATTR_STATUS = "status";
	private static final String FILE_ATTR_MESSAGE = "message";
	private static final String FILE_ATTR_TYPE = "type";
	private static final String FILE_ATTR_SIZE = "size";
	private static final String REF_SEPARATOR=".";
	
	
	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/    
	private String reference;
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

	private String filename;
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

	private String documentId;
    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

	private String status;
    public String getStatus() {
        return status;
    }
    public void setstatus(String status) {
        this.status = status;
    }

	private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

	private String type;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

	private Integer size;
    public Integer getSize() {
        return size;
    }
    public void setSize(Integer size) {
        this.size = size;
    }

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/    
	protected File(String reference, String filename, String documentId, String status, String message, String type,
			Integer size) {
		super();
		this.reference = reference;
		this.filename = filename;
		this.documentId = documentId;
		this.status = status;
		this.message = message;
		this.type = type;
		this.size = size;
	}

	/*--------------------------------------------------------------*
	 * Support for BOM Verbalizations
	 *--------------------------------------------------------------*/    
	public Boolean fileAttached() {
		return filename!=null && !filename.isEmpty();
	}

	public Boolean equalsReference(String pageCode, String sectionCode, String groupCode, String questionCode) {
		// the file question should be always included in a paga or in an element inside a page
		if( checkStringEmpty(pageCode) ) {
			return false;
		}

		// check reference of file question in a page
		if(checkStringEmpty(sectionCode) && checkStringEmpty(groupCode)) {
			StringBuilder ref = new StringBuilder();
			ref.append(pageCode).append(REF_SEPARATOR).append(questionCode);
			return ref.toString().equals(this.reference); 
		}
		
		// check reference of file question in section in a page
		if(!checkStringEmpty(sectionCode) && checkStringEmpty(groupCode)) {
			StringBuilder ref = new StringBuilder();
			ref.append(pageCode).append(REF_SEPARATOR).append(sectionCode).append(REF_SEPARATOR).append(questionCode);
			return ref.toString().equals(this.reference); 
		}

		// check reference of file question in group in a page
		if(checkStringEmpty(sectionCode) && !checkStringEmpty(groupCode)) {
			StringBuilder ref = new StringBuilder();
			ref.append(pageCode).append(REF_SEPARATOR).append(groupCode).append(REF_SEPARATOR).append(questionCode);
			return ref.toString().equals(this.reference); 
		}

		// check reference of file question in a group in a section in a page
		if(!checkStringEmpty(sectionCode) && !checkStringEmpty(groupCode)) {
			StringBuilder ref = new StringBuilder();
			ref.append(pageCode).append(REF_SEPARATOR).append(sectionCode).append(REF_SEPARATOR).append(groupCode).append(REF_SEPARATOR).append(questionCode);
			return ref.toString().equals(this.reference); 
		}

		return false;
	}

	/*--------------------------------------------------------------*
	 * Support for BOM actions verbalizations 
	 *--------------------------------------------------------------*/ 
	//SONAR: Remove these unused method parameters "form", "ruleName"
	//No lo elimino porque afecta a reglas.
	public FormCommand cmdInvalidarFileQuestion(Form form, String message, String ruleName) {
		FormCommand command = new FormCommand(this.reference, FormCommandFeature.VALIDATED, false);
		if(!checkStringEmpty(message)) {
			command.setMessage(message);
		}
		return command;
	}

	/*--------------------------------------------------------------*
	 * External Utilities
	 *--------------------------------------------------------------*/    
	@SuppressWarnings("unchecked")
	public static File[] instantiateFiles(Map<String, Object> formData) {
		LoggerHelper.log(Level.INFO, "Instantiating File objects from formData");
		ArrayList<File> result = new ArrayList<>();
		// iterar las paginas del formulario		
		for (Map.Entry<String, Object> entryPage: formData.entrySet()) {
			String pageCode = entryPage.getKey();
			Object obj = entryPage.getValue();
			if(obj instanceof Map<?, ?>) {
				Map<String, Object> page = (Map<String,Object>) obj;
				result.addAll(instantiatePageFiles(pageCode, page));
			} 
		}
		
		return result.toArray(new File[0]);
	}

	@SuppressWarnings("unchecked")
	protected static ArrayList<File> instantiatePageFiles(String pageCode, Map<String, Object> data) {
		ArrayList<File> files = new ArrayList<>();
		for (Map.Entry<String, Object> entryPage: data.entrySet()) {
			String itemCode = entryPage.getKey();
			Object obj = entryPage.getValue();
			String ref = pageCode+REF_SEPARATOR+itemCode; 
			File file = checkAndInstantiateFile(ref, obj);
			if(file!=null) {
				files.add(file);
				continue;
			}
			if(obj instanceof Map<?, ?>) {
				// es una sección, se procesa
				Map<String, Object> section = (Map<String,Object>) obj;
				files.addAll(instantiateSectionFiles(ref,section));
			}
			
		}

		return files;
	}

	protected static ArrayList<File> instantiateSectionFiles(String sectionCode, Map<String, Object> data) {
		ArrayList<File> files = new ArrayList<>();
		for (Map.Entry<String, Object> entryPage: data.entrySet()) {
			String itemCode = entryPage.getKey();
			Object obj = entryPage.getValue();
			String ref = sectionCode+REF_SEPARATOR+itemCode; 
			File file = checkAndInstantiateFile(ref, obj);
			if(file!=null) {
				files.add(file);
			}
		}

		return files;
	}
	
	public static boolean isFile(Object obj) {
		File file = checkAndInstantiateFile(null, obj);
		return file != null;
	}
	
	/*--------------------------------------------------------------*
	 * Internal Utilities
	 *--------------------------------------------------------------*/    
	protected static boolean checkStringEmpty(String str) {
		return str==null || str.isEmpty();
	}
	
	protected static File checkAndInstantiateFile(String reference, Object obj) {
		File file = null;
		if(obj instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> mapFile = (Map<String,Object>) obj;
			if(mapFile.containsKey(FILE_OBJ_ID)) {
				String filename = (String) mapFile.get(FILE_ATTR_FILENAME);
				String documentId = (String) mapFile.get(FILE_ATTR_DOCID);
				String status = (String) mapFile.get(FILE_ATTR_STATUS);
				String message = (String) mapFile.get(FILE_ATTR_MESSAGE);
				String type = (String) mapFile.get(FILE_ATTR_TYPE);
				Integer size = (Integer) mapFile.get(FILE_ATTR_SIZE);
				file = new File(reference, filename, documentId, status, message, type, size);
			}
		}
		return file;
	}
	
	public static Map<String, Object> createFormDataQuestionFile() {
		Map<String, Object> file = new HashMap<>();
		file.put(FILE_OBJ_ID, true);
		file.put(FILE_ATTR_FILENAME, null);
		file.put(FILE_ATTR_DOCID, null);
		file.put(FILE_ATTR_STATUS, null);
		file.put(FILE_ATTR_MESSAGE, null);
		file.put(FILE_ATTR_TYPE, null);
		file.put(FILE_ATTR_SIZE, null);
		return file;
	}
}
