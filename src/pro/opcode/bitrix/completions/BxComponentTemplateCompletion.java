package pro.opcode.bitrix.completions;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import pro.opcode.bitrix.BxReferencePatterns;
import pro.opcode.bitrix.api.BxComponent;
import pro.opcode.bitrix.api.BxCore;

public class BxComponentTemplateCompletion extends CompletionContributor
{
	/**
	 * Построение автокомплита компонента в процессе набора $APPLICATION->IncludeComponent(...)
	 */
	public BxComponentTemplateCompletion() {
		extend(CompletionType.BASIC, BxReferencePatterns.bxComponentTemplateReference(), new CompletionProvider<CompletionParameters>() {
			public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
			assert parameters.getPosition() instanceof LeafPsiElement;
			BxComponent bxComponent = BxCore.getComponent(parameters.getPosition().getParent());
			for (VirtualFile template : bxComponent.getPossibleTemplates())
				resultSet.addElement(LookupElementBuilder.create(template.getName()));
//				for (VirtualFile component : bitrix.getComponents())
//					resultSet.addElement(LookupElementBuilder.create(String.format("%s:%s",
//						component.getParent().getName(),
//						component.getName()
//					)));
			}
		});
	}
}
