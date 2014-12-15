package pro.opcode.bitrix.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BxCore
{
	private Project project;
	private static final String[] BitrixPaths = {"local", "bitrix"};

	public BxCore(Project project) {
		this.project = project;
	}

	/* Проверяет, есть ли в текущем проекте папка (bitrix|local)/components/bitrix */
	public boolean isComponentsFolderExists() {
		for (String bitrixPath : BitrixPaths) {
			VirtualFile componentsDir = project.getBaseDir().findFileByRelativePath(bitrixPath + "/components/bitrix");
			if (componentsDir != null && componentsDir.isDirectory() && componentsDir.exists())
				return true;
		}
		return false;
	}

	/* Возвращает список всех доступных шаблонов сайтов */
	public VirtualFile[] getSiteTemplates() {
		List<VirtualFile> templates = new ArrayList<VirtualFile>();
		for (String bitrixPath : BitrixPaths) {
			VirtualFile templatesDir = project.getBaseDir().findChild("templates"); if (templatesDir != null && templatesDir.isDirectory()) {
				for (VirtualFile templateDir : templatesDir.getChildren()) if (templateDir != null && templateDir.isDirectory()) {
					VirtualFile templateComponent;
					if ((templateComponent = templateDir.findChild("header.php")) != null && !templateComponent.isDirectory() && templateComponent.isValid()
					 && (templateComponent = templateDir.findChild("footer.php")) != null && !templateComponent.isDirectory() && templateComponent.isValid())
						templates.add(templateDir);
				}
			}
		}
		return templates.toArray(new VirtualFile[templates.size()]);
	}

	/* Возвращает список указанных компонентов всех доступных шаблонов сайтов, где они есть */
	public VirtualFile[] getSiteTemplatesComponents(String templateComponent) {
		List<VirtualFile> templates = new ArrayList<VirtualFile>();
		for (String bitrixPath : BitrixPaths) {
			VirtualFile templatesDir = project.getBaseDir().findFileByRelativePath(bitrixPath + "/templates"); if (templatesDir != null && templatesDir.isDirectory()) {
				for (VirtualFile templateDir : templatesDir.getChildren()) if (templateDir != null && templateDir.isDirectory()) {
					VirtualFile component = templateDir.findChild(templateComponent); if (component != null && !component.isDirectory() && component.isValid())
						templates.add(component);
				}
			}
		}
		return templates.toArray(new VirtualFile[templates.size()]);
	}

	/* Возвращает список всех доступных компонентов */
	public VirtualFile[] getComponents() {
		List<VirtualFile> components = new ArrayList<VirtualFile>();
		for (String bitrixPath : BitrixPaths) {
			VirtualFile vendorsDir = project.getBaseDir().findFileByRelativePath(bitrixPath + "/components"); if (vendorsDir != null && vendorsDir.isDirectory()) {
				for (VirtualFile componentsDir : vendorsDir.getChildren()) if (componentsDir != null && componentsDir.isDirectory()) {
					Collections.addAll(components, componentsDir.getChildren());
				}
			}
		}
		return components.toArray(new VirtualFile[components.size()]);
	}

	public VirtualFile[] getComponentVendors() {
		List<VirtualFile> vendors = new ArrayList<VirtualFile>();
		for (String bitrixPath : BitrixPaths) {
			VirtualFile vendorsDir = project.getBaseDir().findFileByRelativePath(bitrixPath + "/components"); if (vendorsDir != null && vendorsDir.isDirectory()) {
				Collections.addAll(vendors, vendorsDir.getChildren());
			}
		}
		return vendors.toArray(new VirtualFile[vendors.size()]);
	}

	public VirtualFile getComponentTemplateSourceFile(PsiElement element) {
		BxComponent credentials = new BxComponent(element); if (credentials.component != null && credentials.template != null) {
			for (String path : new String[]{"{local,bitrix}/templates/*/components/%s/%s/%s/template.php", "{local,bitrix}/components/%s/%s/templates/%s/template.php"}) {
				VirtualFile founded = findFile(project.getBaseDir(), path, credentials.vendor, credentials.component, credentials.template); if (founded != null)
					return founded;
			}
		}

		return null;
	}

	public VirtualFile getPageIncludeFile(PsiElement element, String suffix) {

		assert element != null
			&& element.getContainingFile() != null
			&& element.getContainingFile().getParent() != null;

		PsiFile founded = getPageIncludeFile(
			element.getContainingFile().getParent(),
			((StringLiteralExpression) element).getContents(),
			suffix
		);

		return founded == null ? null : founded.getVirtualFile();
	}

	public static boolean isFilenameValid(String fileName) {
		return fileName.matches("^[/^\\-_.A-Za-z0-9]+$");
		/* Лучший, но более медленный вариант:
		try {
			new File(fileName).getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
		*/
	}

	@Nullable
	public static VirtualFile findFile(VirtualFile baseDir, String path, String... vars) {
		VirtualFile[] files = findFiles(baseDir, path, vars);
		return files == null ? null : files[0];
	}

	@Nullable
	public static VirtualFile[] findFiles(VirtualFile baseDir, String path, String... vars) {
		if (vars.length > 0)
			path = String.format(path, vars);

		List<VirtualFile> result = new ArrayList<VirtualFile>();
		List<VirtualFile> buffer = new ArrayList<VirtualFile>();

		VirtualFile child;

		result.add(baseDir);

		for (String pathComponent : path.split("/")) if (!pathComponent.isEmpty()) {
			for (VirtualFile parent : result) {
				/* Перечисления */
				if (pathComponent.startsWith("{") && pathComponent.endsWith("}")) {
					for (String enumeration : pathComponent.substring(1, pathComponent.length() - 1).split(","))
						if ((child = parent.findChild(enumeration)) != null)
							buffer.add(child);
					continue;
				}
				/* Любой деть */
				if (pathComponent.equals("*")) {
					buffer.addAll(Arrays.asList(parent.getChildren()));
					continue;
				}
				/* Конкретный деть */
				if ((child = parent.findChild(pathComponent)) != null) {
					buffer.add(child);
				}
			}
			result.clear(); result.addAll(buffer);
			buffer.clear();
		}

		/* Вместопустых массивов возвращаем null */
		if (result.isEmpty())
			return null;

		/* Если путь оканчивается на /, то проверим что все результаты являются директориями */
		if (path.endsWith("/")) {
			for (VirtualFile file : result) {
				if (file.isDirectory())
					buffer.add(file);
			}
			result.clear(); result.addAll(buffer);
			buffer.clear();
		}

		return result.toArray(new VirtualFile[result.size()]);
	}

	private PsiFile getPageIncludeFile(PsiDirectory directory, String name, String suffix) {
		PsiFile file; if ((file = directory.findFile(String.format("%s_%s.php", suffix, name))) == null && suffix.equals("sect") && directory.getParent() != null)
			file = getPageIncludeFile(directory.getParent(), name, suffix);

		return file;
	}
}
