package pro.opcode.bitrix.references;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.opcode.bitrix.BxReference;
import pro.opcode.bitrix.BxReferencePatterns;
import pro.opcode.bitrix.api.BxComponent;

import java.util.Arrays;
import java.util.List;

public class BxComponentTemplateReference extends BxReference
{
	private static List<String> bxCommonTemplates = Arrays.asList(
		".default"
	);

		/**
		 * Построение автокомплита компонента в процессе набора $APPLICATION->IncludeComponent("component", "...")
		 */

	public BxComponentTemplateReference() {
		extend(CompletionType.BASIC, BxReferencePatterns.bxComponentReference(), new CompletionProvider<CompletionParameters>() {
			public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
//				BxCore bitrix = new BxCore(parameters.getPosition().getProject());

//				if (!bitrix.isComponentsFolderExists())
					for (String component : bxCommonTemplates)
						resultSet.addElement(LookupElementBuilder.create("bitrix:" + component));

//				for (VirtualFile component : bitrix.getComponents())
//					resultSet.addElement(LookupElementBuilder.create(String.format("%s:%s",
//						component.getParent().getName(),
//						component.getName()
//					)));
			}
		});
	}

	public BxComponentTemplateReference(PsiElement element) {
		super(element);
	}

	@Nullable
	@Override
	public PsiElement resolve() {
//		BxCore bitrix = new BxCore(getElement().getProject());
		VirtualFile componentTemplateFile = new BxComponent(getElement()).getComponentTemplateFile("{template.php,template.twig,template.tpl}"); if (componentTemplateFile != null)
			return getElement().getManager().findFile(componentTemplateFile);
		return null;
	}
}
