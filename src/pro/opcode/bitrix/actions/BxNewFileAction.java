package pro.opcode.bitrix.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import pro.opcode.bitrix.BitrixFramework;
import pro.opcode.bitrix.api.BxCore;

public class BxNewFileAction extends CreateFileFromTemplateAction
{
	public BxNewFileAction() {
		super("Страница", "Битрикс: Страница", BitrixFramework.bxIcon);
	}

	@Override
	protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
		builder.setTitle("Битрикс: Страница")
			.addKind("Простая страница", BitrixFramework.bxIcon, "bxSimplePage");
//			.addKind("Подробная страница", StdFileTypes.HTML.getIcon(), "Html")
//			.addKind("XHTML file", StdFileTypes.XHTML.getIcon(), "Xhtml");
	}

	@Override
	protected String getActionName(PsiDirectory psiDirectory, String s, String s1) {
		return "Битрикс: Страница";
	}

	@Override
	protected PsiFile createFileFromTemplate(String s, FileTemplate template, PsiDirectory directory) {
		template.setExtension("php");
		template.setText(BxCore.loadTemplate(template.getName()));
		return super.createFileFromTemplate(s, template, directory);
	}
}
