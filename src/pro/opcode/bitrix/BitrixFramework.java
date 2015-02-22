package pro.opcode.bitrix;

import com.intellij.ProjectTopics;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BitrixFramework implements ProjectComponent, ModuleRootListener
{
	public static final Icon bxIcon;

	private final Project project;
//	public static VirtualFile[] projectSourceRoots = new VirtualFile[]{};
//	public static VirtualFile[] projectResourceRoots = new VirtualFile[]{};

	static {
		bxIcon = IconLoader.getIcon("/pro/opcode/bitrix/resources/icon.bx.png");
	}

	public BitrixFramework(@NotNull Project project) {
		this.project = project;
	}

	@Override
	public void projectOpened() {
//		projectSourceRoots = ProjectRootManager.getInstance(project).getContentSourceRoots();
//		for (VirtualFile resourceRoot : WebResourcesPathsConfiguration.getInstance(project).getResourceDirectories()) {
//			projectResourceRoots = Arrays.copyOf(projectResourceRoots, projectResourceRoots.length + 1);
//			projectResourceRoots[projectResourceRoots.length - 1] = resourceRoot;
//		}
		project.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, this);

		/* Шаблоны страниц */
//		FileTemplateManager templateManager = FileTemplateManager.getInstance();
//		boolean hasBxSimplePageTemplate = false;
//		for (FileTemplate fileTemplate : Arrays.asList(templateManager.getAllTemplates())) {
//			if (fileTemplate.getName().equals("Битрикс: Простая страница")) hasBxSimplePageTemplate = true;
//		}
//
//		if (!hasBxSimplePageTemplate) {
//			FileTemplate bxSimplePage = templateManager.addTemplate("Битрикс: Простая страница", "php");
//			try {
//				bxSimplePage.setText(ResourceUtil.loadText(BitrixFramework.class.getResource("/pro/opcode/bitrix/resources/php/bxSimplePage.php")));
//			} catch (IOException e){
//				templateManager.removeTemplate(bxSimplePage);
//			}
//		}
	}

	@Override
	public void projectClosed() {
	}

	@Override
	public void initComponent() {
	}

	@Override
	public void disposeComponent() {
	}

	@NotNull
	@Override
	public String getComponentName() {
		return "BitrixFramework";
	}

	@Override
	public void beforeRootsChange(ModuleRootEvent moduleRootEvent) {
	}

	@Override
	public void rootsChanged(ModuleRootEvent moduleRootEvent) {
//		projectSourceRoots = ProjectRootManager.getInstance(project).getContentSourceRoots();
//		projectResourceRoots = new VirtualFile[0];
//		for (VirtualFile resourceRoot : WebResourcesPathsConfiguration.getInstance(project).getResourceDirectories()) {
//			projectResourceRoots = Arrays.copyOf(projectResourceRoots, projectResourceRoots.length + 1);
//			projectResourceRoots[projectResourceRoots.length - 1] = resourceRoot;
//		}
	}
}
