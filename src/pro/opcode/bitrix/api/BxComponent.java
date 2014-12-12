package pro.opcode.bitrix.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class BxComponent
{
	public String vendor;
	public String component;
	public String template;

	private Project project;
	private VirtualFile componentDir;
	private VirtualFile templateDir;

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

	/* Возвращает директорию компонента */
	public VirtualFile getComponentDir() {
		if (componentDir == null && (vendor != null && component != null))
			componentDir = BxCore.findFile(project.getBaseDir(), "/{local,bitrix}/components/%s/%s/", vendor, component);

		return componentDir;
	}

	public VirtualFile getComponentFile(String path) {
		return BxCore.findFile(project.getBaseDir(), "/{local,bitrix}/components/%s/%s/%s", vendor, component, path);
	}
}

