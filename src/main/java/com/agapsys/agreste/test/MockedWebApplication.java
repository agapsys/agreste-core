/* 
 * Copyright (C) 2015 Agapsys Tecnologia - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.agapsys.agreste.test;

import com.agapsys.agreste.AbstractAgrestApplication;
import com.agapsys.web.toolkit.utils.FileUtils;
import java.io.File;
import javax.servlet.annotation.WebListener;

@WebListener
public class MockedWebApplication extends AbstractAgrestApplication {

	@Override
	public String getName() {
		return "test-app";
	}

	@Override
	public String getVersion() {
		return "0.1.0";
	}
	
	@Override
	protected String getDirectoryAbsolutePath() {
		return new File(FileUtils.DEFAULT_TEMPORARY_FOLDER, "." + getName()).getAbsolutePath();
	}
}
