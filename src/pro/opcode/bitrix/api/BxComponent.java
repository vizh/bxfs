package pro.opcode.bitrix.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BxComponent
{
	public String vendor;
	public String component;
	public String template;

	private Project project;

	public BxComponent(PsiElement element) {
		project = element.getProject();

		PsiElement[] parameters = ((ParameterList) element.getParent())
			.getParameters();

		String[] cmpParts = ((StringLiteralExpression) parameters[0]).getContents().split(":");
			vendor = cmpParts[0].isEmpty() ? null : cmpParts[0];
			component = cmpParts.length > 1 ? cmpParts[1] : null;

		if (parameters.length > 1 && parameters[1] instanceof StringLiteralExpression) {
			template = ((StringLiteralExpression) parameters[1]).getContents(); if (template.isEmpty())
				template = ".default";
		}
	}

	public VirtualFile getComponentFile(String path) {
		return BxCore.findFile(project.getBaseDir(), "/{local,bitrix}/components/%s/%s/%s", vendor, component, path);
	}

	public VirtualFile getComponentTemplateFile(String path) {
		if (vendor != null && component != null && template != null) {
			for (String search : new String[]{"{local,bitrix}/templates/*/components/%s/%s/%s/%s", "{local,bitrix}/components/%s/%s/templates/%s/%s"}) {
				VirtualFile founded = BxCore.findFile(project.getBaseDir(), search, vendor, component, template, path); if (founded != null)
					return founded;
			}
		}
		return null;
	}

	public VirtualFile[] getPossibleTemplates() {
		List<VirtualFile> foundedTemplates = new ArrayList<VirtualFile>();

		if (vendor != null && component != null) {
			for (String search : new String[]{"{local,bitrix}/templates/*/components/%s/%s/*", "{local,bitrix}/components/%s/%s/templates/*"}) {
				VirtualFile[] templates = BxCore.findFiles(project.getBaseDir(), search, vendor, component); if (templates != null)
					foundedTemplates.addAll(Arrays.asList(templates));
			}
		}

		return foundedTemplates.toArray(new VirtualFile[foundedTemplates.size()]);
	}
}
