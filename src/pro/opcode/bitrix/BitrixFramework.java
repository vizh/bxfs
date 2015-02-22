package pro.opcode.bitrix;

import com.intellij.ProjectTopics;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import org.jetbrains.annotations.NotNull;

public class BitrixFramework implements ProjectComponent, ModuleRootListener
{
	private final Project project;
//	public static VirtualFile[] projectSourceRoots = new VirtualFile[]{};
//	public static VirtualFile[] projectResourceRoots = new VirtualFile[]{};

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
