package pro.opcode.bitrix;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import pro.opcode.bitrix.references.BxComponentIncludeReference;
import pro.opcode.bitrix.references.BxComponentReference;
import pro.opcode.bitrix.references.BxComponentTemplateReference;
import pro.opcode.bitrix.references.BxPathReference;

public class BxReferenceContributors extends PsiReferenceContributor
{	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		// provider for component name of $APPLICATION->IncludeComponent() call
		registrar.registerReferenceProvider(BxReferencePatterns.bxComponentReference(), new PsiReferenceProvider() {
			@NotNull @Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxComponentReference(element).getReference();
		}});

		// provider for component template name of $APPLICATION->IncludeComponent() call
		registrar.registerReferenceProvider(BxReferencePatterns.bxComponentTemplateReference(), new PsiReferenceProvider(){
			@NotNull @Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxComponentTemplateReference(element).getReference();
		}});

		// provider for include file of $APPLICATION->IncludeComponent('bitrix:main.include') call
		registrar.registerReferenceProvider(BxReferencePatterns.bxComponentMainIncludeReference(), new PsiReferenceProvider(){
			@NotNull @Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxComponentIncludeReference(element).getReference();
		}});

		/* Образботчик строковых переменных содержащих путь */
		registrar.registerReferenceProvider(BxReferencePatterns.bxPathReference(), new PsiReferenceProvider(){
				@NotNull @Override
				public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			return new BxPathReference(element).getReference();
		}});
	}
}
