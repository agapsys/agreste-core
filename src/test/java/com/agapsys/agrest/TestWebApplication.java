/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agrest;

import com.agapsys.agreste.AbstractWebApplication;
import com.agapsys.web.toolkit.utils.FileUtils;
import java.io.File;

public class TestWebApplication extends AbstractWebApplication {
	
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
}
