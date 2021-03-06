/**
 * Copyright (C) 2012 Simeon J. Morgan (smorgan@digitalfeed.net)
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * The software has the following requirements (GNU GPL version 3 section 7):
 * You must retain in pdroid-manager, any modifications or derivatives of
 * pdroid-manager, or any code or components taken from pdroid-manager the author
 * attribution included in the files.
 * In pdroid-manager, any modifications or derivatives of pdroid-manager, or any
 * application utilizing code or components taken from pdroid-manager must include
 * in any display or listing of its creators, authors, contributors or developers
 * the names or pseudonyms included in the author attributions of pdroid-manager
 * or pdroid-manager derived code.
 * Modified or derivative versions of the pdroid-manager application must use an
 * alternative name, rather than the name pdroid-manager.
 */

/**
 * @author Simeon J. Morgan <smorgan@digitalfeed.net>
 */
package net.digitalfeed.pdroidalternative;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;

import android.privacy.PrivacySettings;


/**
 * This class provides a 'Setting' with the actual selected option for the application included.
 * This is used with the AppDetailAdapter to show the settings for an application.
 * @author smorgan
 *
 */
public class PDroidAppSetting extends PDroidSetting {
	int selectedOptionBit;
	//if there is only one value, then we use 'customValue' to hold it (this applies to almost all
	//of the currently available settings).
	//For multiple-value settings (e.g. lat, long) set the list to have entries name->value such that
	//when we use reflection to write values, we can use setting postfixed with the key.
	List<SimpleImmutableEntry<String, String>> customValues;
	String customValue;
	
	public PDroidAppSetting(String id, String name, String settingFunctionName, String valueFunctionNameStub, String title, String group,
			String groupTitle, String[] options, String trustedOption, int sort) {
		super(id, name, settingFunctionName, valueFunctionNameStub, title, group, groupTitle, options, trustedOption, sort);
		this.selectedOptionBit = OPTION_FLAG_ALLOW; //if something isn't set, it is assumed to be 'allowed'
	}
	
	public PDroidAppSetting(String id, String name, String settingFunctionName, String valueFunctionNameStub, String title, String group,
			String groupTitle, String[] options, String trustedOption, int sort, int selectedOptionBit) {
		super(id, name, settingFunctionName, valueFunctionNameStub, title, group, groupTitle, options, trustedOption, sort);
		this.setSelectedOptionBit(selectedOptionBit);
	}
	
	public PDroidAppSetting(String id, String name, String settingFunctionName, String valueFunctionNameStub, String title, String group,
			String groupTitle, String[] options, String trustedOption, int sort, int selectedOptionBit, List<SimpleImmutableEntry<String, String>> customValues) {
		super(id, name, settingFunctionName, valueFunctionNameStub, title, group, groupTitle, options, trustedOption, sort);
		this.setSelectedOptionBit(selectedOptionBit);
		this.customValues = customValues;
	}
	
	public PDroidAppSetting(String id, String name, String settingFunctionName, String valueFunctionNameStub, String title, String group,
			String groupTitle, String[] options, String trustedOption, int sort, int selectedOptionBit, String customValue) {
		super(id, name, settingFunctionName, valueFunctionNameStub, title, group, groupTitle, options, trustedOption, sort);
		this.setSelectedOptionBit(selectedOptionBit);
		this.customValue = customValue;
	}
	
	public String getSelectedOption() {
			switch (this.selectedOptionBit) {
			case 0:
				return null;
			case OPTION_FLAG_ALLOW:
				return OPTION_TEXT_ALLOW;
			case OPTION_FLAG_CUSTOM:
				return OPTION_TEXT_CUSTOM;
			case OPTION_FLAG_CUSTOMLOCATION:
				return OPTION_TEXT_CUSTOMLOCATION;
			case OPTION_FLAG_DENY:
				return OPTION_TEXT_DENY;
			case OPTION_FLAG_NO:
				return OPTION_TEXT_NO;
			case OPTION_FLAG_RANDOM:
				return OPTION_TEXT_RANDOM;
			case OPTION_FLAG_YES:
				return OPTION_TEXT_YES;
			default:
				throw new InvalidParameterException("Selected option bits can only nominate one option");
			}
	}
	
	public int getSelectedOptionBit() {
		return this.selectedOptionBit;
	}
	
	public void setSelectedOptionBit(int selectedOptionBit) {
		//Validation check the selected option bit - only one option can be chosen,
		//so if we detect more than 1 selected we'll raise an exception
		if (Integer.bitCount(selectedOptionBit) > 1) {
                throw new InvalidParameterException("Selected option bits can only nominate one option");
        }
        
		this.selectedOptionBit = selectedOptionBit; 
	}
	
	public void setCustomValue(String customValue) {
		this.customValue = customValue;
		this.customValues = null;
	}

	public void setCustomValues(List<SimpleImmutableEntry<String, String>> customValues) {
		this.customValue = null;
		this.customValues = customValues;
	}
	
	public List<SimpleImmutableEntry<String, String>> getCustomValues() {
		if (this.customValue != null) {
			LinkedList<SimpleImmutableEntry<String, String>> returnMap = new LinkedList<SimpleImmutableEntry<String, String>>();
			returnMap.add(new SimpleImmutableEntry<String, String>("", this.customValue));
			return returnMap;
		} else {
			return this.customValues;
		}
	}
	
	public void setSelectedCoreOption(byte coreOption) {
		this.setSelectedOptionBit(convertCoreOptionToSettingOption(coreOption, this.optionsBits));
	}
	
	public byte getSelectedCoreOption() {
		return optionBitToCoreOption(this.selectedOptionBit);
	}
	
	public static int convertCoreOptionToSettingOption(byte coreOption, int optionsBits) {
		switch (coreOption) {
		case PrivacySettings.REAL:
			if (0 != (optionsBits & PDroidSetting.OPTION_FLAG_ALLOW)) {
				return PDroidSetting.OPTION_FLAG_ALLOW;
			} else if (0 != (optionsBits & PDroidSetting.OPTION_FLAG_YES)) {
				return PDroidSetting.OPTION_FLAG_YES;
			} else {
				throw new InvalidParameterException("The Setting option must be a recognised option type"); 
			}
		case PrivacySettings.CUSTOM:
			if (0 != (optionsBits & PDroidSetting.OPTION_FLAG_CUSTOM)) {
				return PDroidSetting.OPTION_FLAG_CUSTOM;
			} else if (0 != (optionsBits & PDroidSetting.OPTION_FLAG_CUSTOMLOCATION)) {
				return PDroidSetting.OPTION_FLAG_CUSTOMLOCATION;
			} else {
				throw new InvalidParameterException("The Setting option must be a recognised option type"); 
			}
		case PrivacySettings.RANDOM:
			if (0 != (optionsBits & PDroidSetting.OPTION_FLAG_RANDOM)) {
				return PDroidSetting.OPTION_FLAG_RANDOM;
			} else {
				throw new InvalidParameterException("The Setting option must be a recognised option type"); 
			}
		case PrivacySettings.EMPTY:
			if (0 != (optionsBits & PDroidSetting.OPTION_FLAG_DENY)) {
				return PDroidSetting.OPTION_FLAG_DENY;
			} else if (0 != (optionsBits & PDroidSetting.OPTION_FLAG_NO)) {
				return PDroidSetting.OPTION_FLAG_NO;
			} else {
				throw new InvalidParameterException("The Setting option must be a recognised option type"); 
			}
		default:
			throw new InvalidParameterException("The core setting is not recognised");
		}
	}
	
}