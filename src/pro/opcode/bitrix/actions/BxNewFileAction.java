package pro.opcode.bitrix.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import pro.opcode.bitrix.BitrixFramework;

public class BxNewFileAction extends CreateFileFromTemplateAction
{
	public BxNewFileAction() {
		super("Страница", "Битрикс: Создание новой страницы", BitrixFramework.bxIcon);
	}

	@Override
	protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
		builder.setTitle("Битрикс: Новая страница")
			.addKind("Страница", BitrixFramework.bxIcon, "Битрикс - Страница")
			.addKind("Страница (модерн)", BitrixFramework.bxIcon, "Битрикс - Страница (модерн)")
			.addKind("Сервис", BitrixFramework.bxIcon, "Битрикс - Сервис");
	}

	@Override
	protected String getActionName(PsiDirectory psiDirectory, String s, String s1) {
		return "Битрикс: Новая страница";
	}
}
