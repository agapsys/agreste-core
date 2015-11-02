/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.web.toolkit.utils.FileUtils;
import java.io.File;

public class TestWebApplication extends AbstractWebApplication {
	private static final boolean CREATE_PROPERTIES_FILE = false;
	private static final boolean LOAD_PROPERTIES_FILE   = false;
	
	@Override
	public String getName() {
		return "test";
	}

	@Override
	public String getVersion() {
		return "0.1.0";
	}
	
	@Override
	public String getDirectoryAbsolutePath() {
		return new File(FileUtils.DEFAULT_TEMPORARY_FOLDER, "." + getName()).getAbsolutePath();
	}

	@Override
	protected boolean isPropertiesFileLoadingEnabled() {
		return LOAD_PROPERTIES_FILE;
	}
	
	@Override
	protected boolean isPropertiesFileCreationEnabled() {
		return CREATE_PROPERTIES_FILE;
	}
}
