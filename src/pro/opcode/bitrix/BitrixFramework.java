package pro.opcode.bitrix;

import com.intellij.ProjectTopics;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ResourceUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class BitrixFramework implements ProjectComponent, ModuleRootListener {
	public static final Icon bxIcon;

	private final Project project;
//	public static VirtualFile[] projectSourceRoots = new VirtualFile[]{};
//	public static VirtualFile[] projectResourceRoots = new VirtualFile[]{};
	private static final HashMap<String, String> templates = new HashMap<>();

	static {
		bxIcon = IconLoader.getIcon("/pro/opcode/bitrix/resources/icon.bx.png");

		templates.put("Битрикс - Страница", "bxSimplePage");
		templates.put("Битрикс - Страница (модерн)", "bxSimplePageModern");
		templates.put("Битрикс - Сервис", "bxSimpleService");
		templates.put("Битрикс - Раздел (настройки)", "bxSimpleSectionConfig");
		templates.put("Битрикс - Раздел (титульная)", "bxSimpleSectionIndex");
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
		FileTemplateManager templateManager = FileTemplateManager.getInstance(project);

		for (String templateName : templates.keySet()) {
			if (null == templateManager.findInternalTemplate(templateName)) {
				Optional<String> content = getResourceFileContent(
					"fileTemplates/" + templates.get(templateName) + ".php.ft");

				if (content.isPresent()) {
					FileTemplate template
						= templateManager.addTemplate(templateName, "php");

					template.setText(content.get());
					template.setReformatCode(true);
				}
			}
		}
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

	@NotNull
	private Optional<String> getResourceFileContent(@NotNull String resourceFilePath) {
		try {
			return Optional.of(ResourceUtil.loadText(BitrixFramework.class.getResource(
				"/pro/opcode/bitrix/resources/" + resourceFilePath
			)));
		}
		catch (IOException e) {
			return Optional.empty();
		}
	}
}
