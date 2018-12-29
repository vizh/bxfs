package pro.opcode.bitrix.actions;

import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateDirectoryOrPackageHandler;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import pro.opcode.bitrix.BitrixFramework;

import java.util.Properties;

public class BxNewSectionAction extends AnAction implements DumbAware
{
	public BxNewSectionAction() {
		super("Раздел", "Битрикс: Создание нового раздела", BitrixFramework.bxIcon);
	}

	@Override
	public void actionPerformed(@NotNull AnActionEvent event) {
		IdeView view
			= event.getData(LangDataKeys.IDE_VIEW);

		if (view != null) {
			Project project
				= event.getProject();

			if (project != null) {
				PsiDirectory directory
					= DirectoryChooserUtil.getOrChooseDirectory(view);

				if (directory != null) {
					CreateDirectoryOrPackageHandler validator
						= new CreateDirectoryOrPackageHandler(project, directory, true, "\\/");

					// Сообщение не нужно, так как заголовок окна и так близко. Они резонируют.
					Messages.showInputDialog(project, null, "Битрикс: Создание Нового Раздела", BitrixFramework.bxIcon, "", validator);

					PsiElement result = validator.getCreatedElement();
					if (result instanceof PsiDirectory) {
						PsiDirectory createdDir = (PsiDirectory) result;

						FileTemplateManager templateManager
							= FileTemplateManager.getInstance(project);

						FileTemplate cfgTemplate = templateManager.findInternalTemplate("Битрикс - Раздел (настройки)");
						FileTemplate idxTemplate = templateManager.findInternalTemplate("Битрикс - Раздел (титульная)");

						Properties properties
							= FileTemplateManager.getInstance(project).getDefaultProperties();

						try {
							PsiElement cfgFile = FileTemplateUtil.createFromTemplate(cfgTemplate, ".section", properties, createdDir);
							PsiElement idxFile = FileTemplateUtil.createFromTemplate(idxTemplate, "index", properties, createdDir);

							view.selectElement(idxFile);
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isDumbAware() {
		return false;
	}
}
