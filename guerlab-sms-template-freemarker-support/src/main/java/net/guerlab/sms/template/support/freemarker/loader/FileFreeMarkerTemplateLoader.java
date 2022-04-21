/*
 * Copyright 2018-2022 guerlab.net and other contributors.
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.html
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.guerlab.sms.template.support.freemarker.loader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * 文件模板加载器.
 *
 * @author guer
 */
public class FileFreeMarkerTemplateLoader implements FreeMarkerTemplateLoader<File> {

	@Override
	public String protocol() {
		return "file";
	}

	@Override
	public boolean accept(Object templateSource) {
		return templateSource instanceof File;
	}

	@Override
	public Object findTemplateSource(String name) {
		return new File(name);
	}

	@Override
	public long getLastModified(Object templateSource) {
		return ((File) templateSource).lastModified();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return new FileReader((File) templateSource);
	}

	@Override
	public void closeTemplateSource(Object templateSource) {

	}
}
