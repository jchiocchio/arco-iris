package scenariosui.test;

import java.util.ResourceBundle;

import scenariosui.properties.ScenariosUILabels;
import scenariosui.properties.ScenariosUIMessages;
import scenariosui.properties.UniqueTableIdentifier;

import commons.test.PropertiesVerifier;

/**
 * Verifies that all the properties exist in both ways. This is, all the labels defined in the properties files are
 * defined in their corresponding Enum classes and the constants on the latter, are defined in the properties files.
 */
public class ScenariosUIPropertiesVerifier extends PropertiesVerifier {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		boolean allPropsAreInPropertiesFiles = testAllPropertiesAreDefined();
		boolean allDefinedPropsAreInEnums = testAllDefinedPropsAreInEnums();
		boolean thereAreNoRepetitions = testThereAreNoRepetitions(UniqueTableIdentifier.class);

		if (allPropsAreInPropertiesFiles && allDefinedPropsAreInEnums) {
			System.out.println("* All EnumProperties registered in this test"
					+ " are synchronized with their corresponding '.properties' file");
		}
		if (thereAreNoRepetitions) {
			System.out.println("* There are no repetitions between the constants of different EnumProperties");
		}
	}

	static {
		bundleMap.put(ResourceBundle.getBundle("scenariosUI_labels"), ScenariosUILabels.class);
		bundleMap.put(ResourceBundle.getBundle("scenariosUImessages"), ScenariosUIMessages.class);
		bundleMap.put(ResourceBundle.getBundle("uniqueTableIdentifier"), UniqueTableIdentifier.class);
	}
}