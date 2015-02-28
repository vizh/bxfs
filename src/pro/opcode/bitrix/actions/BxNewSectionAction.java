package pro.opcode.bitrix.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.impl.CustomFileTemplate;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import pro.opcode.bitrix.BitrixFramework;
import pro.opcode.bitrix.api.BxCore;

public class BxNewSectionAction extends CreateFileFromTemplateAction
{
	public BxNewSectionAction() {
		super("Раздел", "Битрикс: Раздел", BitrixFramework.bxIcon);
	}

	@Override
	protected void buildDialog(Project project, PsiDirectory psiDirectory, CreateFileFromTemplateDialog.Builder builder) {
		builder.setTitle("Битрикс: Раздел")
			.addKind("Простая раздел", BitrixFramework.bxIcon, "bxSimpleSection");
//			.addKind("Подробная страница", StdFileTypes.HTML.getIcon(), "Html")
//			.addKind("XHTML file", StdFileTypes.XHTML.getIcon(), "Xhtml");
	}

	@Override
	protected String getActionName(PsiDirectory psiDirectory, String s, String s1) {
		return "Битрикс: Раздел";
	}

	@Override
	protected PsiFile createFileFromTemplate(String s, FileTemplate template, PsiDirectory directory) {
		if (directory.findSubdirectory(s) != null || directory.findFile(s) != null) {
			/* toDo: Выдавать сообщение, что файл или директория с таким именем не существует */
			return null;
		}

		PsiDirectory createdSect = createdSect = directory.createSubdirectory(s);
		CustomFileTemplate createdFile = new CustomFileTemplate("someName", "php");

		if (template.getName().equals("bxSimpleSection")) {
			/* .section.php */
			createdFile.setText(BxCore.loadTemplate("bxSimpleSectionConfig"));
			super.createFileFromTemplate(".section", createdFile, createdSect);

			/* index.php */
			createdFile.setText(BxCore.loadTemplate("bxSimpleSectionIndex"));
			return super.createFileFromTemplate("index", createdFile, createdSect);
		}

		return null;
	}
}
