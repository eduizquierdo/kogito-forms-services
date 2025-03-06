package com.decide.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.decide.forms.commands.FormCommand;
import com.decide.forms.model.Form;
import com.decide.forms.model.Group;
import com.decide.forms.model.GroupOccurrence;

public class GroupHelper {
	
	private GroupHelper() {}

	public static List<Group> createFormGroups(Map<String, Object> formData) {
		List<Group> groups = new ArrayList<>();
		
		// recorremos las paginas
		for (Map.Entry<String, Object> entryPage : formData.entrySet()) {
			String pageCode = entryPage.getKey();
			Object obj = entryPage.getValue();
			Map<String, Object> page = MapUtil.toMapStringObject(obj);
			if(page != null) {
				List<Group> pageGroups = createPageGroups(page, pageCode);

				groups.addAll(pageGroups);

			}
		}

		return groups;
	}

	protected static List<Group> createPageGroups(Map<String, Object> page, String pageCode) {
		List<Group> groups = new ArrayList<>();
		for (Map.Entry<String, Object> entryPage : page.entrySet()) {
			String code = entryPage.getKey();
			Object obj = entryPage.getValue();
			List<Map<String, Object>> occurrences =  ListUtil.objectToListMapStringObject(obj);
			if(occurrences != null && occurrences.get(0).containsKey(Form.GROUP_INDEX_KEY)) {
				groups.add(createGroup(occurrences, pageCode, code));
			} else {
				if (obj instanceof Map) {
					// estamos en una seccion, buscamos los grupos dentro de la seccion
					String groupReference = pageCode+FormCommand.REFERENCE_SEPARATOR+code;
					
					@SuppressWarnings("unchecked")
					List<Group> sectionGroups = createSectionGroups((Map<String, Object>) obj,groupReference);
					if(!sectionGroups.isEmpty()) {
						groups.addAll(sectionGroups);					
					}
				}
			}
		}
		return groups;
	}

	protected static List<Group> createSectionGroups(Map<String, Object> section, String sectionReference) {
		List<Group> groups = new ArrayList<>();
		for (Map.Entry<String, Object> entryPage : section.entrySet()) {
			String code = entryPage.getKey();
			Object obj = entryPage.getValue();
			
			List<Map<String, Object>> occurrences =  ListUtil.objectToListMapStringObject(obj);
			if(occurrences != null && occurrences.get(0).containsKey(Form.GROUP_INDEX_KEY)) {
				groups.add(createGroup(occurrences, sectionReference, code));
			}
		}
		return groups;
	}
	
	protected static Group createGroup(List<Map<String, Object>> occurrences, String referenceCode, String groupCode) {
		String reference = referenceCode+FormCommand.REFERENCE_SEPARATOR+groupCode;
		Group group = new Group(reference);
		LoggerHelper.log(Level.FINE, () -> 
								 String.format("creating group %s ...", reference));
		for (Map<String, Object> occurrenceMap : occurrences) {
			if (occurrenceMap.containsKey(Form.GROUP_INDEX_KEY) && occurrenceMap.get(Form.GROUP_INDEX_KEY) instanceof Integer) {
				Integer index = (Integer) occurrenceMap.get(Form.GROUP_INDEX_KEY);
					if (index != -1) {
						group.addOcurrence(new GroupOccurrence(group, occurrenceMap));
						LoggerHelper.log(Level.FINE, () -> 
						String.format("adding occurrence %d to group %s ...", index,reference));
					}
			}
		}
		return group;
	}
}
