package com.decide.forms.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.decide.forms.exceptions.DuplicatedItemException;
import com.decide.forms.exceptions.NonExistingFormItemException;
import com.decide.forms.exceptions.TypeMismatchException;
import com.decide.forms.model.ExecutionStatus;
import com.decide.forms.model.File;
import com.decide.forms.model.Form;
import com.decide.forms.util.LoggerHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class FormLayout {

	private static final String NULL_OBJECT_REFERECE = "NONE";
	private static final String QUESTION = "question";
	private static final String SECTION = "section";

	/*--------------------------------------------------------------*
	 * Bean
	 *--------------------------------------------------------------*/
	@JsonProperty("code")
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code.trim();
	}

	/*-----------------------*/
	@JsonProperty("title")
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String titulo) {
		this.title = titulo;
	}

	/*-----------------------*/
	@JsonProperty("locale")
	private String locale;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	/*-----------------------*/
	@JsonProperty("documentation")
	private String documentation;

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentacion) {
		this.documentation = documentacion;
	}

	/*-----------------------*/
	@JsonProperty("url")
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/*-----------------------*/
	@JsonProperty("css")
	private String css;

	public String getCss() {
		return css;
	}

	public void setCss(String estiloCSS) {
		this.css = estiloCSS;
	}

	/*-----------------------*/
	@JsonProperty("dateFormat")
	private String dateFormat;

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/*-----------------------*/
	@JsonProperty("decimalPrecision")
	private Integer decimalPrecision;

	public Integer getDecimalPrecision() {
		return decimalPrecision;
	}

	public void setDecimalPrecision(Integer decimalPrecision) {
		this.decimalPrecision = decimalPrecision;
	}

	/*-----------------------*/
	@JsonProperty("pages")
	private List<Page> pages = new ArrayList<>();

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> paginas) {
		this.pages = paginas;
	}

	/*--------------------------------------------------------------*
	 * DefaultConfiguration
	 *--------------------------------------------------------------*/
	protected void setDefaultConfiguration() {
		this.code = null;
		this.title = null;
		this.locale = null;
		this.documentation = null;
		this.url = null;
		this.css = null;
		this.decimalPrecision = null;
		this.dateFormat = null;
		this.pages = null;
		this.pageCatalog = null;
		this.sectionCatalog = null;
		this.questionCatalog = null;
		this.buttonCatalog = null;
		this.executionStatus = null;
	}

	/*--------------------------------------------------------------*
	 * Constructors
	 *--------------------------------------------------------------*/
	public FormLayout() {
		super();
	}

	public FormLayout(String code, String locale) {
		super();
		this.code = code.trim();
		this.locale = locale;
	}

	/*------------------------------------------------------------------------------------*
	 * Internal catalog of Form Items
	 *   These objects are ephemeral, they are created when el Form is started and used 
	 *   to create an instance of the Form.
	 *   After the FOrm instance is create all catalog objects are removed. 
	 *------------------------------------------------------------------------------------*/
	private Map<String, Page> pageCatalog = new HashMap<>();
	private Map<String, Section> sectionCatalog = new HashMap<>();
	private Map<String, Group> groupCatalog = new HashMap<>();
	private Map<String, Question> questionCatalog = new HashMap<>();
	private Map<String, Button> buttonCatalog = new HashMap<>();

	/*------------------------------------------------------------------------------------*
	 * Internal execution status to report Errors
	 *------------------------------------------------------------------------------------*/
	@JsonIgnore
	private ExecutionStatus executionStatus = null;

	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(ExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}

	/*------------------------------------------------------------------------------------*
	 * Catalog management
	 *   Verbalizable BOM actions to populate and configure Form Catalog objects 
	 *------------------------------------------------------------------------------------*/

	public Page createPage(String code) throws NonExistingFormItemException, DuplicatedItemException {
		if (emptyString(code)) {
			reportErrorNullInfo("page");
			return null;
		}

		LoggerHelper.log(Level.FINE, () -> String.format("Creating page %s", code));

		if (this.getCatalogPage(code) != null) {
			reportWarningDuplicateInfo("page", code);
			return null;
		}
		Page page = new Page(code);
		this.pageCatalog.put(code, page);
		return page;
	}

	public Question createQuestion(String code)
			throws NonExistingFormItemException, DuplicatedItemException {
		if (emptyString(code)) {
			reportErrorNullInfo(QUESTION);
			return null;
		}

		LoggerHelper.log(Level.FINE, () -> String.format("Creating question %s", code));

		if (this.getCatalogQuestion(code) != null) {
			reportWarningDuplicateInfo(QUESTION, code);
			return null;
		}
		Question result = new Question(code);
		this.questionCatalog.put(code, result);
		return result;
	}

	public Section createSection(String code) throws NonExistingFormItemException, DuplicatedItemException {
		if (emptyString(code)) {
			reportErrorNullInfo(SECTION);
			return null;
		}
		LoggerHelper.log(Level.FINE, () -> String.format("Creating section %s", code));
		if (this.getCatalogSection(code) != null) {
			reportWarningDuplicateInfo(SECTION, code);
			return null;
		}
		Section result = new Section(code);
		this.sectionCatalog.put(code, result);
		return result;
	}

	public void createGroup(String code, String label, GroupRenderType render)
			throws NonExistingFormItemException, DuplicatedItemException {
		if (emptyString(code)) {
			reportErrorNullInfo("group");
			return;
		}
		LoggerHelper.log(Level.FINE, () -> String.format("Creating group %s-%s", code, label));
		if (this.getCatalogSection(code) != null) {
			reportWarningDuplicateInfo(SECTION, code);
			return;
		}
		this.groupCatalog.put(code, new Group(code, label, render));
	}

	public void createQuestionOption(String questionCode, String optionCode, String label, String value,
			boolean enabled, boolean visible) throws NonExistingFormItemException, TypeMismatchException {
		if (emptyString(questionCode) || emptyString(optionCode) || emptyString(label) || emptyString(value)) {
			reportErrorNullInfo("question option");
			return;
		}

		Question question = this.getCatalogQuestion(questionCode);
		if (question == null) {
			throw new NonExistingFormItemException(
					String.format("Question %s for option %s not found. Option is ignored", questionCode, optionCode),
					Level.WARNING, questionCode);
		}
		if (question.getOptions().isEmpty()) {
			LoggerHelper.log(Level.FINE, () -> String.format("Creating options for question %s", questionCode));
		}

		QuestionOption questionOption = new QuestionOption(optionCode, label, enabled, visible);
		if (question.getDataType() == DataType.TEXT) {
			questionOption.setValue(value);
		} else if (question.getDataType() == DataType.INTEGER) {
			Integer v = null;
			try {
				v = Integer.parseInt(value);
			} catch (NumberFormatException excepcion) {
				throw new TypeMismatchException(String.format(
						"option '%s' of question '%s' ignored because has incorrect value: '%s'. Integer was expected",
						optionCode, questionCode, value), Level.WARNING, questionCode);
			}
			questionOption.setValue(v);
		} else {
			throw new TypeMismatchException(String.format(
					"question '%s' does not support option '%s'. Only TEXT and INTEGER questions support options",
					questionCode, value), Level.WARNING, questionCode);
		}

		LoggerHelper.log(Level.FINER,
				() -> String.format("Creating option %s-%s for question %s", optionCode, label, questionCode));

		question.addOption(questionOption);
	}

	public void createButton(String code, String label, String action)
			throws NonExistingFormItemException, DuplicatedItemException {
		if (emptyString(code) || emptyString(label) || action == null) {
			reportErrorNullInfo("button");
			return;
		}

		LoggerHelper.log(Level.FINER, () -> String.format("Creating button %s-%s", code, label));

		if (this.getCatalogButton(code) != null) {
			reportWarningDuplicateInfo("button", code);
			return;
		}
		this.buttonCatalog.put(code, new Button(code, label, action));
	}

	/*------------------------------------------------------------------------------------*
	 * Business objects to insert into Working Memory
	 *   To configure form items, all the configurable form items have to be inserted into
	 *   Working Memory to be matcheable by RETE algorithm
	 *   The following form items are returned for configuration:
	 *   - Pages
	 *   - Sections
	 *   - Question
	 *------------------------------------------------------------------------------------*/
	@JsonIgnore
	public List<Object> getFormItemsToInsertIntoWM() {
		List<Object> result = new ArrayList<>();
		for (Page page : this.pageCatalog.values()) {
			result.add(page);
		}
		for (Section section : this.sectionCatalog.values()) {
			result.add(section);
		}
		for (Group group : this.groupCatalog.values()) {
			result.add(group);
		}
		for (Question question : this.questionCatalog.values()) {
			result.add(question);
		}
		for (Button button : this.buttonCatalog.values()) {
			result.add(button);
		}
		return result;
	}

	/*-------------------------------------------------------------------------------------------*
	 * Catalog instantiation
	 *   Verbalizable BOM actions to define the structure of the Form.
	 *   These methods will instantiate Catalog objects while adding questions to pages,
	 *     sections or groups. 
	 *   If the intermediate objects where the question are added (like sections, pages or groups)
	 *     does not exists yet in the Form, they are created, as a lateral effect.   
	 *-------------------------------------------------------------------------------------------*/
	public void addQuestionToStructure(Boolean active, String pageCode, String sectionCode, Integer sectionOrder, String groupCode,
			Integer groupOrder, String questionCode, Integer questionOrder) throws NonExistingFormItemException {
		if(!active) return;
		questionCode = checkValidCode(questionCode);
		sectionCode = checkValidCode(sectionCode);
		groupCode = checkValidCode(groupCode);
		pageCode = checkValidCode(pageCode);

		String msg = String.format("Creating form structure %s / %s / %s / %s", questionCode, sectionCode, groupCode,
				pageCode);
		LoggerHelper.log(Level.FINE, msg);

		// check: at least question and page should be specified
		if (questionCode == null || pageCode == null) {
			reportErrorNullInfo(QUESTION);
			return;
		}

		Page page = getOrCreatePage(pageCode);

		Section section = null;
		if (sectionCode != null) {
			section = getOrCreatePageSection(page, sectionCode);
			section.setOrder(sectionOrder);
		}

		Group group = null;
		if (groupCode != null) {
			if (section != null) {
				group = getOrCreateSectionGroup(section, groupCode);
			} else {
				group = getOrCreatePageGroup(page, groupCode);
			}
			group.setOrder(groupOrder);
		}

		Question catalogQuestion = getCatalogQuestionWithCheck(questionCode);
		if (catalogQuestion == null)
			return;
		else
			catalogQuestion.setOrder(questionOrder);

		if (group != null) {
			group.addQuestion(catalogQuestion);
		} else if (section != null) {
			section.addQuestion(catalogQuestion);
		} else {
			page.addQuestion(catalogQuestion);
		}
	}

	// BOTONES: El elemento donde se colocan debe existir previamente
	public void addPageButton(String buttonCode, String pageCode)
			throws NonExistingFormItemException, DuplicatedItemException {
		if (emptyString(pageCode) || emptyString(buttonCode)) {
			reportErrorNullInfo("button in page");
			return;
		}
		LoggerHelper.log(Level.FINE, () -> String.format("Adding button %s in page %s", buttonCode, pageCode));
		// page should exits in catalog, otherwise, return
		Page catalogPage = getCatalogPage(pageCode);
		if (catalogPage == null) {
			throw new NonExistingFormItemException(
					String.format("Page %s not found to add button %s. Button is ignored", pageCode, buttonCode),
					Level.WARNING, pageCode);
		}
		Page formPage = getOrCreatePageWithChecks(pageCode);

		// question should exits in catalog, otherwise, return
		Button button = getCatalogButton(buttonCode);
		if (button == null) {
			throw new NonExistingFormItemException(String.format("Button %s not found. Button is ignored", buttonCode),
					Level.WARNING, buttonCode);
		}

		Button buttonExisting = formPage.getButton(buttonCode);
		if (buttonExisting != null) {
			throw new DuplicatedItemException(
					String.format("Duplicated Button %s in page %s. Button is ignored", buttonCode, pageCode),
					Level.WARNING, pageCode);
		}

		formPage.addButton(button);
	}

	public void addQuestionButton(String buttonCode, String pageCode, String sectionCode, String questionCode)
			throws NonExistingFormItemException, DuplicatedItemException {
		if (emptyString(pageCode) || emptyString(buttonCode) || emptyString(questionCode)) {
			reportErrorNullInfo("button in question");
			return;
		}
		LoggerHelper.log(Level.FINE, () -> String.format("Adding button %s in question %s of section %s in page %s",
				buttonCode, questionCode, ((sectionCode == null) ? "NONE" : sectionCode), pageCode));

		// page should exits
		Page page = getPage(pageCode);
		if (page == null) {
			throw new NonExistingFormItemException(
					String.format("Page %s not found to add button %s. Button is ignored", pageCode, buttonCode),
					Level.WARNING, pageCode);
		}

		// if specified, section should exist
		Section section = null;
		if (!emptyString(sectionCode)) {
			section = page.getSection(sectionCode);
			if (section == null) {
				throw new NonExistingFormItemException(
						String.format("Section %s not found in page %s to add button %s. Button is ignored",
								sectionCode, pageCode, buttonCode),
						Level.WARNING, sectionCode);
			}
		}

		// question should exits, otherwise, return
		Question question = (section != null) ? section.getQuestion(questionCode) : page.getQuestion(questionCode);
		if (question == null) {
			throw new NonExistingFormItemException(
					String.format("Question %s not found in page %s to add button %s. Button is ignored", questionCode,
							pageCode, buttonCode),
					Level.WARNING, questionCode);
		}

		Button buttonExisting = question.getButton(buttonCode);
		if (buttonExisting != null) {
			String msj = (section != null)
					? String.format("Duplicated Button %s in question %s in section %s in page %s. Button is ignored",
							buttonCode, questionCode, sectionCode, pageCode)
					: String.format("Duplicated Button %s in question %s in page %s. Button is ignored", buttonCode,
							questionCode, pageCode);
			throw new DuplicatedItemException(msj, Level.WARNING, questionCode);
		}

		// button should exits in the catalog, otherwise, return
		Button button = getCatalogButton(buttonCode);
		if (button == null) {
			throw new NonExistingFormItemException(String.format("Button %s not found. Button is ignored", buttonCode),
					Level.WARNING, buttonCode);
		}

		question.addButton(button);
	}

	/*-------------------------------------------------------------------------------------------*
	 * Catalog instantiation
	 *   Internal utilities used by catalog instantiation.   
	 *-------------------------------------------------------------------------------------------*/
	protected String checkValidCode(String code) {
		if (code == null)
			return null;
		String result = code.trim();
		if (result.isEmpty() || NULL_OBJECT_REFERECE.equals(result)) {
			return null;
		} else {
			return result;
		}
	}

	protected Page getOrCreatePage(String pageCode) throws NonExistingFormItemException {
		Page result = this.getPage(pageCode);
		if (result == null) {
			Page catalogPage = this.getCatalogPageWithCheck(pageCode);
			if (catalogPage == null)
				return null;
			result = new Page(catalogPage);
			this.pages.add(result);
		}
		return result;
	}

	private Page getOrCreatePageWithChecks(String pageCode) throws NonExistingFormItemException {
		if (emptyString(pageCode)) {
			reportErrorNullInfo("page");
		}
		Page catalogPage = getCatalogPage(pageCode);
		if (catalogPage == null) {
			throw new NonExistingFormItemException(String.format("Page %s not found", pageCode), Level.WARNING,
					pageCode);
		}
		return getOrCreatePage(catalogPage);
	}

	protected Section getOrCreatePageSection(Page page, String sectionCode) throws NonExistingFormItemException {
		Section result = page.getSection(sectionCode);
		if (result == null) {
			Section catalogSection = getCatalogSectionWithCheck(sectionCode);
			if (catalogSection == null)
				return null;
			result = page.getOrCreateSection(catalogSection);
		}
		return result;
	}

	protected Group getOrCreatePageGroup(Page page, String groupCode) throws NonExistingFormItemException {
		Group result = page.getGroup(groupCode);
		if (result == null) {
			Group catalogGroup = getCatalogGroupWithCheck(groupCode);
			if (catalogGroup == null)
				return null;
			result = page.getOrCreateGroup(catalogGroup);
		}
		return result;
	}

	protected Group getOrCreateSectionGroup(Section section, String groupCode) throws NonExistingFormItemException {
		Group result = section.getGroup(groupCode);
		if (result == null) {
			Group catalogGroup = getCatalogGroupWithCheck(groupCode);
			if (catalogGroup == null)
				return null;
			result = section.getOrCreateGroup(catalogGroup);
		}
		return result;
	}

	protected Question getCatalogQuestionWithCheck(String code) throws NonExistingFormItemException {
		if (emptyString(code))
			return null;
		Question result = this.questionCatalog.get(code);
		if (result == null) {
			throw new NonExistingFormItemException(String.format("Question %s not found. Question is ignored", code),
					Level.WARNING, code);
		}
		return result;

	}

	protected Page getCatalogPageWithCheck(String code) throws NonExistingFormItemException {
		if (emptyString(code))
			return null;
		Page result = this.pageCatalog.get(code);
		if (result == null) {
			throw new NonExistingFormItemException(
					String.format("Page %s not found. Structure fragment is ignored", code), Level.WARNING, code);
		}
		return result;
	}

	protected Section getCatalogSectionWithCheck(String code) throws NonExistingFormItemException {
		if (emptyString(code))
			return null;
		Section result = this.sectionCatalog.get(code);
		if (result == null) {
			throw new NonExistingFormItemException(
					String.format("Section %s not found. Structure fragment is ignored", code), Level.WARNING, code);
		}
		return result;
	}

	protected Group getCatalogGroupWithCheck(String code) throws NonExistingFormItemException {
		if (emptyString(code))
			return null;
		Group result = this.groupCatalog.get(code);
		if (result == null) {
			throw new NonExistingFormItemException(
					String.format("Group %s not found. Structure fragment is ignored", code), Level.WARNING, code);
		}
		return result;
	}

	/*------------------------------------------------------------------------------------*
	 * Business data instantiation
	 *   Creates business data objects that match the structure of the Form Layout:
	 *   - Pages are mapped to Maps
	 *   - Page Questions are keys in the Map of a Page. Key is mapped to String, Integer, ...
	 *   - Section are keys in the Map of the Page. Key is mapped to a Map
	 *   - Section Questions are keys in the Map of a Section. Key is mapped to String, Integer, ...
	 *------------------------------------------------------------------------------------*/
	public Map<String, Object> createFormData(Form form) {
		LoggerHelper.log(Level.INFO, "Intanciating form data from layout of from " + this.code);
		Map<String, Object> formData = new HashMap<>();

		for (Page page : getPages()) {

			LoggerHelper.log(Level.FINE, () -> String.format("Intanciating data of page %s", page.getCode()));

			Map<String, Object> pageData = new HashMap<>();
			pageData = createFormDataQuestion(pageData, page.getQuestions());

			for (Section section : page.getSections()) {
				Map<String, Object> sectionData = new HashMap<>();
				LoggerHelper.log(Level.FINE, () -> String.format("Intanciating data of section %s", section.getCode()));
				sectionData = createFormDataQuestion(sectionData, section.getQuestions());

				for (Group group : section.getGroups()) {
					ArrayList<Map<String, Object>> groupOcurrences = new ArrayList<>();
					Map<String, Object> groupData = new HashMap<>();
					groupData.put(Form.GROUP_INDEX_KEY, Integer.valueOf(-1));
					for (Question question : group.getQuestions()) {
						groupData.put(question.getCode(), question.getDefaultValue());
					}
					groupOcurrences.add(groupData);
					sectionData.put(group.getCode(), groupOcurrences);
				}
				pageData.put(section.getCode(), sectionData);
			}
			for (Group group : page.getGroups()) {
				ArrayList<Map<String, Object>> groupOcurrences = new ArrayList<>();
				Map<String, Object> groupData = new HashMap<>();
				groupData.put(Form.GROUP_INDEX_KEY, Integer.valueOf(-1));
				groupData = createFormDataQuestion(groupData, group.getQuestions());
				groupOcurrences.add(groupData);
				pageData.put(group.getCode(), groupOcurrences);
			}
			formData.put(page.getCode(), pageData);
		}

		if (form != null && Boolean.TRUE.equals(form.getAllowAttach()))
			formData.put(Form.ATTACHMENTS_PROPERTY_NAME, new ArrayList<>());

		return formData;
	}

	public Map<String, Object> createFormDataQuestion(Map<String, Object> elementData, List<Question> questions) {
		LoggerHelper.log(Level.FINE,
				() -> String.format("Intanciating %d question", (questions != null) ? questions.size() : -1));
		if (questions != null && !questions.isEmpty()) {
			for (Question question : questions) {
				if (question.getDataType().equals(DataType.FILE)) {
					Map<String, Object> file = File.createFormDataQuestionFile();
					elementData.put(question.getCode(), file);
				} else {
					elementData.put(question.getCode(), question.getDefaultValue());
				}

				LoggerHelper.log(Level.FINE,
						() -> String.format("Intanciating section question %s", question.getCode()));
			}
		}

		return elementData;
	}

	/*-------------------------------------------------------------------------------------------*
	 *   Other utilities
	 *-------------------------------------------------------------------------------------------*/
	// change all to NOT EDITABLE.
	// This metthod is called from b2x code during initizalization when the form
	// action is VIEW

	public void setViewMode() {
		// Preguntas no editables
		LoggerHelper.log(Level.INFO, () -> String.format("Switching to view mode from %s", this.code));
		setNoEditableQuestionsOrVisibleButtons(true, true);
	}

	public void setNoEditableMode() {
		// Preguntas no editables
		LoggerHelper.log(Level.INFO, () -> String.format("Switching to non-editable mode from %s", this.code));
		setNoEditableQuestionsOrVisibleButtons(true, false);
	}

	private void setNoEditableQuestionsOrVisibleButtons(boolean updateQuestion, boolean updateButtons) {
		for (Page page : pages) {
			for (Section section : page.getSections()) {
				setNoEditableQuestion(section.getQuestions(), updateQuestion, updateButtons);
			}
			setNoEditableQuestion(page.getQuestions(), updateQuestion, updateButtons);
			if(updateButtons) setNoVisibleButtons(page.getButtons());
		}
	}

	private void setNoVisibleButtons(List<Button> buttons) {
		for (Button button : buttons) {
			button.setVisible(false);
			button.setEnabled(false);
		}
	}

	private void setNoEditableQuestion(List<Question> questions, boolean updateQuestion, boolean updateButtons) {
		for (Question question : questions) {
			if (updateQuestion)
				question.setEditable(false);
			if (updateButtons)
				setNoVisibleButtons(question.getButtons());
		}
	}

	/*------------------------------------------------------------------------------------*
	 * Internal utilities 
	 *------------------------------------------------------------------------------------*/
	protected boolean emptyString(String str) {
		return str == null || str.trim().isEmpty();
	}

	protected void reportErrorNullInfo(String formObjectType) throws NonExistingFormItemException {
		throw new NonExistingFormItemException(String
				.format("Error creating a %s with with null information. Object will not be created", formObjectType),
				Level.SEVERE, formObjectType + "Null");
	}

	protected void reportWarningDuplicateInfo(String formObjectType, String code) throws DuplicatedItemException {
		throw new DuplicatedItemException(String.format("Duplicated %s %s. Ignored", formObjectType, code),
				Level.WARNING, code + formObjectType);
	}

	protected Page getCatalogPage(String code) {
		if (emptyString(code))
			return null;
		return this.pageCatalog.get(code);
	}

	protected Question getCatalogQuestion(String code) {
		if (emptyString(code))
			return null;
		return this.questionCatalog.get(code);
	}

	protected Section getCatalogSection(String code) {
		if (emptyString(code))
			return null;
		return this.sectionCatalog.get(code);
	}

	protected Group getCatalogGroup(String code) {
		if (emptyString(code))
			return null;
		return this.groupCatalog.get(code);
	}

	protected Button getCatalogButton(String code) {
		if (emptyString(code))
			return null;
		return this.buttonCatalog.get(code);
	}

	protected Page getOrCreatePage(Page catalogPage) {
		if (catalogPage == null)
			return null;
		Page page = getPage(catalogPage.getCode());
		if (page == null) {
			page = new Page(catalogPage);
			this.pages.add(page);
		}
		return page;
	}

	public Page getPage(String code) {
		if (emptyString(code))
			return null;
		for (Page page : this.pages) {
			if (code.equalsIgnoreCase(page.getCode())) {
				return page;
			}
		}
		return null;
	}

	public Section getSection(String code, Page page) {
		if (emptyString(code))
			return null;
		for (Section section : page.getSections()) {
			if (code.equalsIgnoreCase(section.getCode())) {
				return section;
			}
		}
		return null;
	}

	public Question getQuestion(String code, Page page) {
		if (emptyString(code))
			return null;
		for (Question question : page.getQuestions()) {
			if (code.equalsIgnoreCase(question.getCode())) {
				return question;
			}
		}
		return null;
	}

	public Question getQuestion(String code, Section section) {
		if (emptyString(code))
			return null;
		for (Question question : section.getQuestions()) {
			if (code.equalsIgnoreCase(question.getCode())) {
				return question;
			}
		}
		return null;
	}

	public Button getButton(String code, Page page) {
		if (emptyString(code))
			return null;
		for (Button button : page.getButtons()) {
			if (code.equalsIgnoreCase(button.getCode())) {
				return button;
			}
		}
		return null;
	}

	public Button getButton(String code, Question question) {
		if (emptyString(code))
			return null;
		for (Button button : question.getButtons()) {
			if (code.equalsIgnoreCase(button.getCode())) {
				return button;
			}
		}
		return null;
	}

	public QuestionOption getOption(String code, Question question) {
		if (emptyString(code))
			return null;
		for (QuestionOption option : question.getOptions()) {
			if (code.equalsIgnoreCase(option.getCode())) {
				return option;
			}
		}
		return null;
	}

}
