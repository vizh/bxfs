package pro.opcode.bitrix.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BxCore
{
	private Project project;
	private static final String[] BitrixPaths = {"local", "bitrix"};
	private static class ComponentCredentials
	{
		String vendor;
		String component;
		String template;
	}

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

	public VirtualFile getComponentDir(PsiElement element) {
		ComponentCredentials credentials = GetComponentCredentials(element); if (credentials.component != null) {
			for (String bitrixPath : BitrixPaths) {
				String path = String.format("%s/components/%s/%s", bitrixPath, credentials.vendor, credentials.component);
				VirtualFile componentDir = project.getBaseDir().findFileByRelativePath(path); if (componentDir != null && componentDir.isDirectory())
					return componentDir;
			}
		}
		return null;
	}

	public VirtualFile getComponentSourceFile(PsiElement element) {
		VirtualFile componentDir = getComponentDir(element); if (componentDir != null) {
			VirtualFile componentSourceFile = componentDir.findChild("component.php"); if (componentSourceFile != null && componentSourceFile.isValid())
				return componentSourceFile;
		}
		return null;
	}

	public VirtualFile getComponentTemplateSourceFile(PsiElement element) {
		ComponentCredentials credentials = GetComponentCredentials(element); if (credentials.component != null && credentials.template != null) {
			for (String bitrixPath : BitrixPaths) {
				/* Добавляем кастомные шаблоны компонента */
				VirtualFile templatesDir = project.getBaseDir().findFileByRelativePath(bitrixPath + "/templates"); if (templatesDir != null && templatesDir.isDirectory()) {
					for (VirtualFile templateDir : templatesDir.getChildren()) {
						VirtualFile componentsDir = templateDir.findChild("components"); if (componentsDir != null && componentsDir.isDirectory()) {
							VirtualFile vendorsDir = componentsDir.findChild(credentials.vendor); if (vendorsDir != null && vendorsDir.isDirectory()) {
								VirtualFile componentDir = vendorsDir.findChild(credentials.component); if (componentDir != null && componentDir.isDirectory()) {
									VirtualFile componentTemplateDir = componentDir.findChild(credentials.template); if (componentTemplateDir != null && componentTemplateDir.isDirectory()) {
										return componentTemplateDir.findChild("template.php");
									}
								}
							}
						}
					}
				}
			}
			/* Кастомный шаблон компонента не нашли, посмотрим среди системных шаблонов */
			VirtualFile componentDir = getComponentDir(element); if (componentDir != null) {
				VirtualFile componentTemplateSourceFile = componentDir.findFileByRelativePath("templates/" + credentials.template + "/template.php"); if (componentTemplateSourceFile != null && componentTemplateSourceFile.isValid())
					return componentTemplateSourceFile;
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

	private PsiFile getPageIncludeFile(PsiDirectory directory, String name, String suffix) {
		PsiFile file; if ((file = directory.findFile(String.format("%s_%s.php", suffix, name))) == null && suffix.equals("sect") && directory.getParent() != null)
			file = getPageIncludeFile(directory.getParent(), name, suffix);

		return file;
	}

	private static ComponentCredentials GetComponentCredentials(PsiElement element) {
		PsiElement[] methodParameters = ((ParameterList) element.getParent()).getParameters();
		String[] cmpParts = ((StringLiteralExpression) methodParameters[0]).getContents().split(":");
		ComponentCredentials credentials = new ComponentCredentials();
		credentials.vendor = cmpParts[0].isEmpty() ? null : cmpParts[0];
		credentials.component = cmpParts.length > 1 ? cmpParts[1] : null;

		if (methodParameters.length > 1 && methodParameters[1] instanceof StringLiteralExpression) {
			credentials.template = ((StringLiteralExpression) methodParameters[1]).getContents(); if (credentials.template.isEmpty())
				credentials.template = null;
		}

		return credentials;
	}
}
